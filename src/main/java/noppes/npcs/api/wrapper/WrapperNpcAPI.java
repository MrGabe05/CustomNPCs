package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import nikedemos.markovnames.generators.MarkovGenerator;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LRUHashMap;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WrapperNpcAPI extends NpcAPI{
	private static final Map<DimensionType, WorldWrapper> worldCache = new LRUHashMap<DimensionType, WorldWrapper>(10);
	public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
	
	private static NpcAPI instance = null;
	
	public static void clearCache(){
		worldCache.clear();
		BlockWrapper.clearCache();
	}

	@Override
	public IEntity getIEntity(Entity entity) {
		if(entity == null || entity.level.isClientSide)
			return null;
		if(entity instanceof EntityNPCInterface)
			return ((EntityNPCInterface) entity).wrappedNPC;
		else{
			return WrapperEntityData.get(entity);
		}
	}

	@Override
	public ICustomNpc createNPC(World level){
		if(level.isClientSide)
			return null;
		EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
		return npc.wrappedNPC;
	}

	
	@Override
	public void registerPermissionNode(String permission, int defaultType) {
		if(defaultType < 0 || defaultType > 2) {
			throw new CustomNPCsException("Default type cant be smaller than 0 or larger than 2");
		}
		if(hasPermissionNode(permission)) {
			throw new CustomNPCsException("Permission already exists");
		}
		DefaultPermissionLevel level = DefaultPermissionLevel.values()[defaultType];
		PermissionAPI.registerNode(permission, level, permission);
	}

	@Override
	public boolean hasPermissionNode(String permission) {
		return PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(permission);
	}

	@Override
	public ICustomNpc spawnNPC(World level, int x, int y, int z){
		if(level.isClientSide)
			return null;
		EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        npc.absMoveTo(x + 0.5, y, z + 0.5, 0, 0);
        npc.ais.setStartPos(x, y, z);
        npc.setHealth(npc.getMaxHealth());
		level.addFreshEntity(npc);
		return npc.wrappedNPC;
	}
	
	
	public static NpcAPI Instance(){
		if(instance == null)
			instance = new WrapperNpcAPI();
		return instance;
	}

	@Override
	public IEventBus events() {
		return EVENT_BUS;
	}

	@Override
	public IBlock getIBlock(World level, BlockPos pos) {
		return BlockWrapper.createNew(level, pos, level.getBlockState(pos));
	}

	@Override
	public IItemStack getIItemStack(ItemStack itemstack) {
		if(itemstack == null || itemstack.isEmpty())
			return ItemStackWrapper.AIR;
		return itemstack.getCapability(ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, null).orElse(ItemStackWrapper.AIR);
	}

	@Override
	public IWorld getIWorld(ServerWorld level) {
		WorldWrapper w = worldCache.get(level.dimensionType());
		if(w != null) {
			w.level = level;
			return w;
		}
		worldCache.put(level.dimensionType(), w = WorldWrapper.createNew(level));
		return w;
	}

	@Override
	public IWorld getIWorld(DimensionType dimension) {
		for(ServerWorld level : CustomNpcs.Server.getAllLevels()){
			if(level.dimensionType() == dimension)
				return getIWorld(level);
		}
		throw new CustomNPCsException("Unknown dimension: " + dimension);
	}

	@Override
	public IWorld getIWorld(String dimension) {
		ResourceLocation loc = new ResourceLocation(dimension);
		for(ServerWorld level : CustomNpcs.Server.getAllLevels()){
			if(level.dimension().location().equals(loc))
				return getIWorld(level);
		}
		throw new CustomNPCsException("Unknown dimension: " + loc);
	}

	@Override
	public IContainer getIContainer(IInventory inventory) {
		return new ContainerWrapper(inventory);
	}

	@Override
	public IContainer getIContainer(Container container) {
		if(container instanceof ContainerNpcInterface) {
			return ContainerNpcInterface.getOrCreateIContainer((ContainerNpcInterface)container);
		}
		return new ContainerWrapper(container);
	}

	@Override
	public IFactionHandler getFactions() {
		checkWorld();
		return FactionController.instance;
	}

	private void checkWorld() {
		if(CustomNpcs.Server == null || CustomNpcs.Server.isStopped())
			throw new CustomNPCsException("No world is loaded right now");
	}

	@Override
	public IRecipeHandler getRecipes() {
		checkWorld();
		return RecipeController.instance;
	}

	@Override
	public IQuestHandler getQuests() {
		checkWorld();
		return QuestController.instance;
	}

	@Override
	public IWorld[] getIWorlds(){
		checkWorld();
		List<IWorld> list = new ArrayList<IWorld>();
		for(ServerWorld level : CustomNpcs.Server.getAllLevels()){
			list.add(getIWorld(level));
		}
		return list.toArray(new IWorld[list.size()]);
	}

	@Override
	public IPos getIPos(double x, double y, double z) {
		return new BlockPosWrapper(new BlockPos(x, y, z));
	}

	@Override
	public File getGlobalDir() {
		return CustomNpcs.Dir;
	}

	@Override
	public File getWorldDir() {
		return CustomNpcs.getWorldSaveDirectory();
	}

	@Override
	public INbt getINbt(CompoundNBT compound) {
		if(compound == null)
			return new NBTWrapper(new CompoundNBT());
		return new NBTWrapper(compound);
	}

	@Override
	public INbt stringToNbt(String str){
		if(str == null || str.isEmpty())
			throw new CustomNPCsException("Cant cast empty string to nbt");
		try {
			return getINbt(NBTJsonUtil.Convert(str));
		} catch (JsonException e) {
			throw new CustomNPCsException(e, "Failed converting " + str);
		}
	}

	@Override
	public IDamageSource getIDamageSource(DamageSource damagesource) {
		return new DamageSourceWrapper(damagesource);
	}

	@Override
	public IDialogHandler getDialogs() {
		return DialogController.instance;
	}

	@Override
	public ICloneHandler getClones() {
		return ServerCloneController.Instance;
	}

	@Override
	public String executeCommand(IWorld level, String command) {
		FakePlayer player = EntityNPCInterface.CommandPlayer;
		player.setLevel(level.getMCWorld());
		player.setPos(0, 0, 0);
		return NoppesUtilServer.runCommand(level.getMCWorld(), BlockPos.ZERO, "API", command, null, player);
	}

	@Override
	public INbt getRawPlayerData(String uuid) {
		return getINbt(PlayerData.loadPlayerData(uuid));
	}

	@Override
	public IPlayerMail createMail(String sender, String subject) {
		PlayerMail mail = new PlayerMail();
		mail.sender = sender;
		mail.subject = subject;
		return mail;
	}

	@Override
	public ICustomGui createCustomGui(int id, int width, int height, boolean pauseGame) {
		return new CustomGuiWrapper(id, width, height, pauseGame);
	}

	@Override
	public String getRandomName(int dictionary, int gender) {
		return MarkovGenerator.fetch(dictionary, gender);
	}
}
