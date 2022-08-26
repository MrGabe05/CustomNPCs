package noppes.npcs.api.wrapper;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerWrapper<T extends ServerPlayerEntity> extends EntityLivingBaseWrapper<T> implements IPlayer{
	
	private IContainer inventory;

	private Object pixelmonPartyStorage;
	private Object pixelmonPCStorage;


	private final IData storeddata = new IData() {

		@Override
		public void put(String key, Object value) {
			CompoundNBT compound = getStoredCompound();
			if(value instanceof Number){
				compound.putDouble(key, ((Number) value).doubleValue());
			}
			else if(value instanceof String)
				compound.putString(key, (String)value);
		}

		@Override
		public Object get(String key) {
			CompoundNBT compound = getStoredCompound();
			if(!compound.contains(key))
				return null;
			INBT base = compound.get(key);
			if(base instanceof NumberNBT)
				return ((NumberNBT)base).getAsDouble();
			return ((StringNBT)base).getAsString();
		}

		@Override
		public void remove(String key) {
			CompoundNBT compound = getStoredCompound();
			compound.remove(key);
		}

		@Override
		public boolean has(String key) {
			return getStoredCompound().contains(key);
		}

		@Override
		public void clear() {
			PlayerData data = PlayerData.get(entity);
			data.scriptStoreddata = new CompoundNBT();
		}

		private CompoundNBT getStoredCompound(){
			PlayerData data = PlayerData.get(entity);
			return data.scriptStoreddata;
		}

		@Override
		public String[] getKeys() {
			CompoundNBT compound = getStoredCompound();
			return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
		}
	};
	
	public PlayerWrapper(T player) {
		super(player);
	}

	@Override
	public IData getStoreddata(){
		return storeddata;
	}

	@Override
	public String getName(){
		return entity.getName().getString();
	}

	@Override
	public String getDisplayName(){
		return entity.getDisplayName().getString();
	}

	@Override
	public int getHunger() {
		return entity.getFoodData().getFoodLevel();
	}

	@Override
	public void setHunger(int level) {
		entity.getFoodData().setFoodLevel(level);
	}

	@Override
	public boolean hasFinishedQuest(int id){
		PlayerQuestData data = this.getData().questData;
		return data.finishedQuests.containsKey(id);
	}

	@Override
	public boolean hasActiveQuest(int id){
		PlayerQuestData data = this.getData().questData;
		return data.activeQuests.containsKey(id);
	}

	@Override
	public IQuest[] getActiveQuests() {
		PlayerQuestData data = this.getData().questData;
		List<IQuest> quests = new ArrayList<IQuest>();
		for(int id : data.activeQuests.keySet()) {
			IQuest quest = QuestController.instance.quests.get(id);
			if(quest != null) {
				quests.add(quest);
			}
		}
		return quests.toArray(new IQuest[quests.size()]);
	}

	@Override
	public IQuest[] getFinishedQuests() {
		PlayerQuestData data = this.getData().questData;
		List<IQuest> quests = new ArrayList<IQuest>();
		for(int id : data.finishedQuests.keySet()) {
			IQuest quest = QuestController.instance.quests.get(id);
			if(quest != null) {
				quests.add(quest);
			}
		}
		return quests.toArray(new IQuest[quests.size()]);
	}

	@Override
	public void startQuest(int id){
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
        	return;
        QuestData questdata = new QuestData(quest);      
        PlayerData data = getData();
        data.questData.activeQuests.put(id, questdata);
		Packets.send((ServerPlayerEntity)entity, new PacketAchievement(new TranslationTextComponent("quest.newquest"), new TranslationTextComponent(quest.title), 2));
		ITextComponent text = new TranslationTextComponent("quest.newquest").append(":").append(new TranslationTextComponent(quest.title));
		Packets.send((ServerPlayerEntity)entity, new PacketChat(text));
		
		data.updateClient = true;
	}

	@Override
	public void sendNotification(String title, String msg, int type) {
		if(type < 0 || type > 3) {
			throw new CustomNPCsException("Wrong type value given " + type);
		}
		Packets.send((ServerPlayerEntity)entity, new PacketAchievement(new TranslationTextComponent(title), new TranslationTextComponent(msg), type));
	}

	@Override
	public void finishQuest(int id){
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
        	return;
        PlayerData data = getData();
        data.questData.finishedQuests.put(id, System.currentTimeMillis());  
        data.updateClient = true;
	}

	@Override
	public void stopQuest(int id){
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
        	return;
        PlayerData data = getData();
        data.questData.activeQuests.remove(id);
		data.updateClient = true;
	}

	@Override
	public void removeQuest(int id){
        Quest quest = QuestController.instance.quests.get(id);
        if (quest == null)
        	return;
        PlayerData data = getData();
        data.questData.activeQuests.remove(id);
        data.questData.finishedQuests.remove(id);
		data.updateClient = true;
	}

	@Override
	public boolean hasReadDialog(int id){
		PlayerDialogData data = this.getData().dialogData;
		return data.dialogsRead.contains(id);
	}

	@Override
	public void showDialog(int id, String name){
        Dialog dialog = DialogController.instance.dialogs.get(id);
        if(dialog == null){
        	throw new CustomNPCsException("Unknown Dialog id: " + id);
        }
        
        if(!dialog.availability.isAvailable(entity))
        	return;
        
    	EntityDialogNpc npc = new EntityDialogNpc(this.getWorld().getMCWorld());
    	npc.display.setName(name);
		EntityUtil.Copy(entity, npc);
    	DialogOption option = new DialogOption();
    	option.dialogId = id;
		option.title = dialog.title;
    	npc.dialogs.put(0, option);
    	NoppesUtilServer.openDialog(entity, npc, dialog);
	}

	@Override
	public void addFactionPoints(int faction, int points){
		PlayerData data = getData();
		data.factionData.increasePoints(entity, faction, points);
		data.updateClient = true;
	}

	@Override
	public int getFactionPoints(int faction) {
		return getData().factionData.getFactionPoints(entity, faction);
	}

	@Override
	public float getRotation(){
		return entity.yRot;
	}

	@Override
	public void setRotation(float rotation){
		entity.yRot = rotation;
	}

	@Override
	public void message(String message){
		entity.sendMessage(new TranslationTextComponent(NoppesStringUtils.formatText(message, entity)), Util.NIL_UUID);
	}

	@Override
	public int getGamemode(){
		return entity.gameMode.getGameModeForPlayer().getId();
	}

	@Override
	public void setGamemode(int type){
		entity.setGameMode(GameType.byId(type));
	}

	@Override
	public int inventoryItemCount(IItemStack item){
		int count = 0;
		for(int i = 0; i < entity.inventory.getContainerSize(); i++){
			ItemStack is = entity.inventory.getItem(i);
            if (is != null && isItemEqual(item.getMCItemStack(), is))
            	count += is.getCount();
		}
		return count;
	}
	
    private boolean isItemEqual(ItemStack stack, ItemStack other){
    	if(other.isEmpty())
    		return false;
    	return stack.getItem() == other.getItem();
    }

	@Override
	public int inventoryItemCount(String id){
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
		if(item == null)
			throw new CustomNPCsException("Unknown item id: " + id);		
		return inventoryItemCount(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1)));
	}

	@Override
	public IContainer getInventory(){
		if(inventory == null)
			inventory = new ContainerWrapper(entity.inventory);
		return inventory;
	}

	@Override
	public IItemStack getInventoryHeldItem() {
		return NpcAPI.Instance().getIItemStack(entity.inventory.getCarried());
	}

	@Override
	public boolean removeItem(IItemStack item, int amount){
		int count = inventoryItemCount(item);
		if(amount  > count)
			return false;
		else if(count == amount)
			removeAllItems(item);
		else{
			for(int i = 0; i < entity.inventory.getContainerSize(); i++){
				ItemStack is = entity.inventory.getItem(i);
	            if (is != null && isItemEqual(item.getMCItemStack(), is)){
	            	if(amount >= is.getCount()){
	                	entity.inventory.setItem(i, ItemStack.EMPTY);
	                	amount -= is.getCount();
	            	}
	            	else{
	            		is.split(amount);
	            		break;
	            	}
	            }
			}
		}
		updatePlayerInventory();
		return true;
	}

	@Override
	public boolean removeItem(String id, int amount){
		Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
		if(item == null)
			throw new CustomNPCsException("Unknown item id: " + id);		
		return removeItem(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1)), amount);
	}
	
	@Override
	public boolean giveItem(IItemStack item){
		ItemStack mcItem = item.getMCItemStack();
		if(mcItem.isEmpty())
			return false;
				
		boolean bo = entity.inventory.add(mcItem.copy());
		if(bo){
			NoppesUtilServer.playSound(entity, SoundEvents.ITEM_PICKUP, 0.2F, ((entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			updatePlayerInventory();
		}
		return bo;
	}
	
	@Override
	public boolean giveItem(String id, int amount){
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
		if(item == null)
			return false;
		ItemStack mcStack = new ItemStack(item);
		IItemStack itemStack = NpcAPI.Instance().getIItemStack(mcStack);
		itemStack.setStackSize(amount);
		
		return giveItem(itemStack);
	}

	@Override
	public void updatePlayerInventory() {
		entity.inventoryMenu.broadcastChanges();
		entity.connection.send(new SSetSlotPacket(-2, entity.inventory.selected, entity.inventory.getItem(entity.inventory.selected)));
		PlayerQuestData playerdata = getData().questData;
		playerdata.checkQuestCompletion(entity, QuestType.ITEM);
	}

	@Override
	public IBlock getSpawnPoint(){
		BlockPos pos = entity.getSleepingPos().orElse(null);
		if(pos == null)
			return getWorld().getSpawnPoint();
		return NpcAPI.Instance().getIBlock(entity.level, pos);
	}

	@Override
	public void setSpawnPoint(IBlock block){
		setSpawnpoint(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public void setSpawnpoint(int x, int y, int z){
		x = ValueUtil.CorrectInt(x, -30000000, 30000000);
		z = ValueUtil.CorrectInt(z, -30000000, 30000000);
		y = ValueUtil.CorrectInt(y, 0, 256);
        entity.setRespawnPosition(getWorld().getMCWorld().dimension, new BlockPos(x, y, z), 0, true, false);
	}

	@Override
	public void resetSpawnpoint(){
		entity.setRespawnPosition(getWorld().getMCWorld().dimension, null, 0, true, false);
		//entity.setSpawnPoint(null, false);
	}

	@Override
	public void removeAllItems(IItemStack item){
		for(int i = 0; i < entity.inventory.getContainerSize(); i++){
			ItemStack is = entity.inventory.getItem(i);
            if (is != null && is.sameItem(item.getMCItemStack()))
            	entity.inventory.setItem(i, ItemStack.EMPTY);
		}
	}
	
	@Override
	public boolean hasAdvancement(String achievement){
		Advancement advancement = this.entity.getServer().getAdvancements().getAdvancement(new ResourceLocation(achievement));
		if(advancement == null){
			throw new CustomNPCsException("Advancement doesnt exist");
		}
		AdvancementProgress progress = this.entity.getServer().getPlayerList().getPlayerAdvancements(this.entity).getOrStartProgress(advancement);
		return progress.isDone();
	}

	@Override
	public int getExpLevel(){
		return entity.experienceLevel;
	}

	@Override
	public void setExpLevel(int level){
		entity.giveExperienceLevels(level - entity.experienceLevel);
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		SPacketDimensionTeleport.teleportPlayer(entity, x, y, z, entity.level.dimension);
	}
	
	@Override
	public void setPos(IPos pos) {
		SPacketDimensionTeleport.teleportPlayer(entity, pos.getX(), pos.getY(), pos.getZ(), entity.level.dimension);
	}

	@Override
	public int getType() {
		return EntitiesType.PLAYER;
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.PLAYER?true:super.typeOf(type);
	}

	@Override
	public boolean hasPermission(String permission){
		return CustomNpcsPermissions.hasPermissionString(entity, permission);
	}

	@Override
	public IPixelmonPlayerData getPixelmonData(){
		if(!PixelmonHelper.Enabled) {
			throw new CustomNPCsException("Pixelmon isnt installed");
		}
		
		return new IPixelmonPlayerData() {

			@Override
			public Object getParty() {
				if(pixelmonPartyStorage == null) {
					pixelmonPartyStorage = PixelmonHelper.getParty(entity);
				}
				return pixelmonPartyStorage;
			}

			@Override
			public Object getPC() {
				if(pixelmonPCStorage == null) {
					pixelmonPCStorage = PixelmonHelper.getPc(entity);
				}
				return pixelmonPCStorage;
			}

		};
	}
	
	private PlayerData data;
	private PlayerData getData(){
		if(data == null){
			data = PlayerData.get(entity);
		}
		return data;
	}

	@Override
	public ITimers getTimers() {
		return getData().timers;
	}

	@Override
	public void removeDialog(int id) {
		PlayerData data = getData();
		data.dialogData.dialogsRead.remove(id);
		data.updateClient = true;
	}

	@Override
	public void addDialog(int id) {
		PlayerData data = getData();
		data.dialogData.dialogsRead.add(id);
		data.updateClient = true;
	}

	@Override
	public void closeGui() {
		entity.closeContainer();
		Packets.send(this.entity, new PacketGuiClose(new CompoundNBT()));
	}

	@Override
	public int factionStatus(int factionId) {
		Faction faction = FactionController.instance.getFaction(factionId);
		if(faction == null)
			throw new CustomNPCsException("Unknown faction: " + factionId);
		return faction.playerStatus(this);
	}

	@Override
	public void kick(String message) {
        entity.connection.disconnect(new TranslationTextComponent(message));
	}

	@Override
	public boolean canQuestBeAccepted(int questId){
		return PlayerQuestController.canQuestBeAccepted(entity, questId);
	}

	@Override
	public void showCustomGui(ICustomGui gui) {
		NoppesUtilServer.openContainerGui((ServerPlayerEntity)this.getMCEntity(), EnumGuiType.CustomGui, (buf) -> {
			buf.writeInt(gui.getSlots().size());
		});
		((ContainerCustomGui)((ServerPlayerEntity)this.getMCEntity()).containerMenu).setGui((CustomGuiWrapper)gui, entity);
		Packets.sendDelayed((ServerPlayerEntity)this.getMCEntity(), new PacketGuiData(((CustomGuiWrapper)gui).toNBT()), 100);
	}

	@Override
	public ICustomGui getCustomGui() {
		if(entity.containerMenu instanceof ContainerCustomGui) {
			return ((ContainerCustomGui)entity.containerMenu).customGui;
		}
		return null;
	}

	@Override
	public void clearData() {
        PlayerData data = getData();
    	data.setNBT(new CompoundNBT());
        data.save(true);
	}

	@Override
	public IContainer getOpenContainer() {
		return NpcAPI.Instance().getIContainer(entity.containerMenu);
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		BlockPos pos = entity.blockPosition();
		Packets.send(entity, new PacketPlaySound(sound, pos, volume, pitch));
	}

	@Override
	public void playMusic(String sound, boolean background, boolean loops) {
		Packets.send(entity, new PacketPlayMusic(sound, !background, loops));
	}

	@Override
	public void sendMail(IPlayerMail mail) {
        PlayerData data = getData();
        data.mailData.playermail.add(((PlayerMail) mail).copy());
		data.save(false);
	}

	@Override
	public void trigger(int id, Object... arguments) {
		EventHooks.onScriptTriggerEvent(PlayerData.get(entity).scriptData, id, getWorld(), getPos(), null, arguments);
	}
}
