package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.entity.EntityNPCInterface;

public class JobItemGiver extends JobInterface{

	public int cooldownType = 0;
	public int givingMethod = 0;
	public int cooldown = 10;
	public NpcMiscInventory inventory;
	public int itemGiverId = 0;
	
	public List<String> lines = new ArrayList<String>();
	
	private int ticks = 10;
	
	private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
	private List<PlayerEntity> toCheck;
	public Availability availability = new Availability();

	public JobItemGiver(EntityNPCInterface npc) {
		super(npc);
		inventory = new NpcMiscInventory(9);
		lines.add("Have these items {player}");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("igCooldownType", cooldownType);
		nbttagcompound.putInt("igGivingMethod", givingMethod);
		nbttagcompound.putInt("igCooldown", cooldown);
		nbttagcompound.putInt("ItemGiverId", itemGiverId);
    	nbttagcompound.put("igLines", NBTTags.nbtStringList(lines));
		nbttagcompound.put("igJobInventory", inventory.getToNBT());
		nbttagcompound.put("igAvailability", availability.save(new CompoundNBT()));
		return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		itemGiverId = nbttagcompound.getInt("ItemGiverId");
		cooldownType = nbttagcompound.getInt("igCooldownType");
		givingMethod = nbttagcompound.getInt("igGivingMethod");
		cooldown = nbttagcompound.getInt("igCooldown");
    	lines = NBTTags.getStringList(nbttagcompound.getList("igLines", 10));
    	inventory.setFromNBT(nbttagcompound.getCompound("igJobInventory"));
    	
    	if(itemGiverId == 0 && GlobalDataController.instance != null)
			itemGiverId = GlobalDataController.instance.incrementItemGiverId();
    	
    	availability.load(nbttagcompound.getCompound("igAvailability"));
	}

	public ListNBT newHashMapNBTList(HashMap<String, Long> lines) {
		ListNBT nbttaglist = new ListNBT();
		HashMap<String, Long> lines2 = lines;
		for (String s : lines2.keySet()) {
			CompoundNBT nbttagcompound = new CompoundNBT();
			nbttagcompound.putString("Line", s);
			nbttagcompound.putLong("Time", lines.get(s));
			nbttaglist.add(nbttagcompound);
		}
		return nbttaglist;
	}

	public HashMap<String, Long> getNBTLines(ListNBT tagList) {
		HashMap<String, Long> map = new HashMap<String, Long>();
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT nbttagcompound = tagList.getCompound(i);
			String line = nbttagcompound.getString("Line");
			long time = nbttagcompound.getLong("Time");
			map.put(line, time);

		}
		return map;
	}
    
	private boolean giveItems(PlayerEntity player){		
		PlayerItemGiverData data = PlayerData.get(player).itemgiverData;
		if(!canPlayerInteract(data)){
			return false;
		}
		
		Vector<ItemStack> items = new Vector<ItemStack>();
		Vector<ItemStack> toGive = new Vector<ItemStack>();
		
		for(ItemStack is : inventory.items)
			if(!is.isEmpty())
				items.add(is.copy());
		if(items.isEmpty())
			return false;
		if(isAllGiver()){
			toGive = items;
		}
		else if(isRemainingGiver()){
			for(ItemStack is : items){
				if(!playerHasItem(player,is.getItem()))
					toGive.add(is);
			}
		}
		else if(isRandomGiver()){
			toGive.add(items.get(npc.level.random.nextInt(items.size())).copy());
		}
		else if(isGiverWhenNotOwnedAny()){
			boolean ownsItems = false;
			for(ItemStack is : items){
				if(playerHasItem(player,is.getItem())){
					ownsItems = true;
					break;
				}
			}
			if(!ownsItems){
				toGive = items;
			}
			else
				return false;
		}
		else if(isChainedGiver()){
			int itemIndex = data.getItemIndex(this);
			int i = 0;
			for(ItemStack item : inventory.items){
				if(i == itemIndex){
					toGive.add(item);
					break;
				}
				i++;
			}
		}
		if(toGive.isEmpty())
			return false;
		if(givePlayerItems(player,toGive)){
			if(!lines.isEmpty()){
				npc.say(player, new Line(lines.get(npc.getRandom().nextInt(lines.size()))));
			}
			if(isDaily())
				data.setTime(this,getDay());
			else
				data.setTime(this,System.currentTimeMillis());
			if(isChainedGiver())
				data.setItemIndex(this, (data.getItemIndex(this) + 1) % inventory.items.size());
			return true;
		}
		return false;
	}
	private int getDay(){
		return (int) (npc.level.getGameTime() / 24000L);
	}
	private boolean canPlayerInteract(PlayerItemGiverData data) {
		if(inventory.items.isEmpty())
			return false;
		if(isOnTimer()){
			if(!data.hasInteractedBefore(this))
				return true;
			return data.getTime(this) + (cooldown*1000) < System.currentTimeMillis();
		}
		else if(isGiveOnce()){
			return !data.hasInteractedBefore(this);
		}
		else if(isDaily()){
			if(!data.hasInteractedBefore(this))
				return true;
			return getDay() > data.getTime(this);
		}
		return false;
	}

	private boolean givePlayerItems(PlayerEntity player,
			Vector<ItemStack> toGive) {
		if(toGive.isEmpty())
			return false;
		if(freeInventorySlots(player) < toGive.size())
			return false;
		for(ItemStack is : toGive){
			npc.givePlayerItem(player, is);
		}
		return true;
	}
	private boolean playerHasItem(PlayerEntity player, Item item){
		for(ItemStack is : player.inventory.items){
			if(!is.isEmpty() && is.getItem() == item)
				return true;
		}
		for(ItemStack is : player.inventory.armor){
			if(!is.isEmpty() && is.getItem() == item)
				return true;
		}
		return false;
	}
	private int freeInventorySlots(PlayerEntity player){
		int i = 0;
		for(ItemStack is : player.inventory.items)
			if(NoppesUtilServer.IsItemStackNull(is))
				i++;
		return i;
	}
	private boolean isRandomGiver(){
		return givingMethod == 0;
	}
	private boolean isAllGiver(){
		return givingMethod == 1;
	}
	private boolean isRemainingGiver(){
		return givingMethod == 2;
	}
	private boolean isGiverWhenNotOwnedAny(){
		return givingMethod == 3;
	}
	private boolean isChainedGiver(){
		return givingMethod == 4;
	}
	public boolean isOnTimer(){
		return cooldownType == 0;
	}
	private boolean isGiveOnce(){
		return cooldownType == 1;
	}
	private boolean isDaily(){
		return cooldownType == 2;
	}
	
	public boolean aiShouldExecute() {
		if(npc.isAttacking())
			return false;
		ticks--;
		if(ticks > 0)
			return false;
		ticks = 10;
		
		toCheck = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(3, 3, 3));
		toCheck.removeAll(recentlyChecked);

		List<PlayerEntity> listMax = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(10, 10, 10));
		recentlyChecked.retainAll(listMax);
		recentlyChecked.addAll(toCheck);
		return toCheck.size() > 0;
	}
	
	@Override
	public boolean aiContinueExecute() {
		return false;
	}

	public void aiStartExecuting() {
		for(PlayerEntity player : toCheck){
			if(npc.canNpcSee(player) && availability.isAvailable(player)){
				recentlyChecked.add(player);
				interact(player);
			}
		}
	}
	
	@Override
	public void killed() {
		// TODO Auto-generated method stub
		
	}

	private boolean interact(PlayerEntity player) {
		if(!giveItems(player))
			npc.say(player, npc.advanced.getInteractLine());
		return true;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType() {
		return JobType.ITEMGIVER;
	}
}
