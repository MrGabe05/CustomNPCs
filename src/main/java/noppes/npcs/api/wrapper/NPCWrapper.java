package noppes.npcs.api.wrapper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.entity.data.INPCDisplay;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class NPCWrapper<T extends EntityNPCInterface> extends EntityLivingWrapper<T> implements ICustomNpc{
	
	public NPCWrapper(T npc) {
		super(npc);
	}

	@Override
	public void setMaxHealth(float health){
		if((int)health == entity.stats.maxHealth)
			return;
		super.setMaxHealth(health);
		entity.stats.maxHealth = (int) health;
		entity.updateClient = true;
	}

	@Override
	public INPCDisplay getDisplay() {
		return entity.display;
	}

	@Override
	public INPCInventory getInventory() {
		return entity.inventory;
	}

	@Override
	public INPCAi getAi() {
		return entity.ais;
	}

	@Override
	public INPCAdvanced getAdvanced() {
		return entity.advanced;
	}

	@Override
	public INPCStats getStats() {
		return entity.stats;
	}

	@Override
	public IFaction getFaction() {
		return entity.faction;
	}

	@Override
	public ITimers getTimers() {
		return entity.timers;
	}

	@Override
	public void setFaction(int id) {
		Faction faction = FactionController.instance.getFaction(id);
		if(faction == null)
			throw new CustomNPCsException("Unknown faction id: " + id);
		entity.setFaction(id);
	}

	@Override
	public INPCRole getRole(){
		return entity.role;
	}

	@Override
	public INPCJob getJob(){
		return entity.job;
	}
	
	@Override
	public int getHomeX(){
		return entity.ais.startPos().getX();
	}

	@Override
	public int getHomeY(){
		return entity.ais.startPos().getY();
	}

	@Override
	public int getHomeZ(){
		return entity.ais.startPos().getZ();
	}

	@Override
	public void setHome(int x, int y, int z){
		entity.ais.setStartPos(new BlockPos(x, y, z));
	}
	
	public int getOffsetX(){
		return (int) entity.ais.bodyOffsetX;
	}
	
	public int getOffsetY(){
		return (int) entity.ais.bodyOffsetY;
	}
	
	public int getOffsetZ(){
		return (int) entity.ais.bodyOffsetZ;
	}
	
	/**
	 * This is used to shift the npc around on a single block instead of having him stand in the middle.
	 * To make an npc stand in the middle of a block its 5,5,5. To make him stand in the corner try 0,5,0
	 */
	public void setOffset(int x, int y, int z){
		entity.ais.bodyOffsetX = ValueUtil.correctFloat(x, 0, 9);
		entity.ais.bodyOffsetY = ValueUtil.correctFloat(y, 0, 9);
		entity.ais.bodyOffsetZ = ValueUtil.correctFloat(z, 0, 9);
		entity.updateClient = true;
	}

	@Override
	public void say(String message){
		entity.saySurrounding(new Line(message));
	}

	@Override
	public void sayTo(IPlayer player, String message){
		entity.say(player.getMCEntity(), new Line(message));
	}
	
	@Override
	public void reset(){
		entity.reset();
	}

	
	@Override
	public long getAge(){
		return entity.totalTicksAlive;
	}
		
	@Override
	public IProjectile shootItem(IEntityLiving target, IItemStack item, int accuracy){
		if(item == null)
			throw new CustomNPCsException("No item was given");
		if(target == null)
			throw new CustomNPCsException("No target was given");
		
		accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
		return (IProjectile) NpcAPI.Instance().getIEntity(entity.shoot(target.getMCEntity(), accuracy, item.getMCItemStack(), false));
	}

	@Override
	public IProjectile shootItem(double x, double y, double z, IItemStack item, int accuracy) {
		if(item == null)
			throw new CustomNPCsException("No item was given");
		
		accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
		return (IProjectile) NpcAPI.Instance().getIEntity(entity.shoot(x, y, z, accuracy, item.getMCItemStack(), false));
	}

	@Override
	public void giveItem(IPlayer player, IItemStack item){
		entity.givePlayerItem(player.getMCEntity(), item.getMCItemStack());
	}
	
	@Override
	public String executeCommand(String command){
		if(!entity.getServer().isCommandBlockEnabled())
			throw new CustomNPCsException("Command blocks need to be enabled to executeCommands");
		return NoppesUtilServer.runCommand(entity, entity.getName().getString(), command, null);
	}

	@Override
	public int getType() {
		return EntitiesType.NPC;
	}

	@Override
	public String getName() {
		return entity.display.getName();
	}

	@Override
	public void setName(String name) {
		entity.display.setName(name);
	}

	@Override
	public void setRotation(float rotation){
		super.setRotation(rotation);
		int r = (int) rotation;
		if(entity.ais.orientation != r) {
			entity.ais.orientation = r;
			entity.updateClient = true;
		}
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.NPC?true:super.typeOf(type);
	}

	@Override
	public void setDialog(int slot, IDialog dialog) {
		if(slot < 0 || slot > 11)
			throw new CustomNPCsException("Slot needs to be between 0 and 11");
		if(dialog == null)
			entity.dialogs.remove(slot);
		else{
			DialogOption option = new DialogOption();
			option.dialogId = dialog.getId();
			option.title = dialog.getName();
			entity.dialogs.put(slot, option);
		}
	}

	@Override
	public IDialog getDialog(int slot) {
		if(slot < 0 || slot > 11)
			throw new CustomNPCsException("Slot needs to be between 0 and 11");
		DialogOption option = entity.dialogs.get(slot);
		if(option == null || !option.hasDialog())
			return null;
		return option.getDialog();
	}

	@Override
	public void updateClient() {
		entity.updateClient();
	}

	@Override
	public IEntityLiving getOwner() {
		LivingEntity owner = entity.getOwner();
		if(owner != null)
			return (IEntityLiving) NpcAPI.Instance().getIEntity(owner);
		return null;
	}

	@Override
	public void trigger(int id, Object... arguments) {
		EventHooks.onScriptTriggerEvent(entity.script, id, getWorld(), getPos(), null, arguments);
	}
}
