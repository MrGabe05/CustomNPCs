package noppes.npcs.roles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.*;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.role.IRoleFollower;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;

import java.util.HashMap;
import java.util.UUID;


public class RoleFollower extends RoleInterface implements IRoleFollower{
	
	private String ownerUUID;
	public boolean isFollowing = true;
	public HashMap<Integer,Integer> rates;
	public NpcMiscInventory inventory;
	public String dialogHire = "";
	public String dialogFarewell = "";
	public int daysHired;
	public long hiredTime;
	public boolean disableGui = false;
	public boolean infiniteDays = false;
	public boolean refuseSoulStone = false;
	
	public PlayerEntity owner = null;
	
	public RoleFollower(EntityNPCInterface npc) {
		super(npc);
		inventory = new NpcMiscInventory(3);
		rates = new HashMap<Integer, Integer>();
	}

	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("MercenaryDaysHired", daysHired);
		nbttagcompound.putLong("MercenaryHiredTime", hiredTime);
		nbttagcompound.putString("MercenaryDialogHired", dialogHire);
		nbttagcompound.putString("MercenaryDialogFarewell", dialogFarewell);
		if(hasOwner())
			nbttagcompound.putString("MercenaryOwner", ownerUUID);
    	nbttagcompound.put("MercenaryDayRates", NBTTags.nbtIntegerIntegerMap(rates));
    	nbttagcompound.put("MercenaryInv", inventory.getToNBT());
    	nbttagcompound.putBoolean("MercenaryIsFollowing", isFollowing);
    	nbttagcompound.putBoolean("MercenaryDisableGui", disableGui);
    	nbttagcompound.putBoolean("MercenaryInfiniteDays", infiniteDays);
    	nbttagcompound.putBoolean("MercenaryRefuseSoulstone", refuseSoulStone);
    	return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		ownerUUID = nbttagcompound.getString("MercenaryOwner");
		daysHired = nbttagcompound.getInt("MercenaryDaysHired");
		hiredTime = nbttagcompound.getLong("MercenaryHiredTime");
		dialogHire = nbttagcompound.getString("MercenaryDialogHired");
		dialogFarewell = nbttagcompound.getString("MercenaryDialogFarewell");
		rates = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("MercenaryDayRates", 10));
		inventory.setFromNBT(nbttagcompound.getCompound("MercenaryInv"));
		isFollowing = nbttagcompound.getBoolean("MercenaryIsFollowing");
		disableGui = nbttagcompound.getBoolean("MercenaryDisableGui");
		infiniteDays = nbttagcompound.getBoolean("MercenaryInfiniteDays");
		refuseSoulStone = nbttagcompound.getBoolean("MercenaryRefuseSoulstone");
	}

	@Override
	public boolean aiShouldExecute() {
		owner = getOwner();
		if(!infiniteDays && owner != null && getDays() <= 0){
			RoleEvent.FollowerFinishedEvent event = new RoleEvent.FollowerFinishedEvent(owner, npc.wrappedNPC);
			EventHooks.onNPCRole(npc, event);
			npc.say(owner, new Line(NoppesStringUtils.formatText(dialogFarewell, owner, npc)));
			killed();
		}
		return false;
	}
	
	public PlayerEntity getOwner(){
		if(npc.level.isClientSide)
			return null;
		if(ownerUUID == null || ownerUUID.isEmpty())
			return null;
		try{
	        UUID uuid = UUID.fromString(ownerUUID);
	        if(uuid != null)
	        	return npc.level.getPlayerByUUID(uuid);
		}
		catch(IllegalArgumentException ex){
			
		}
        
		return ((ServerWorld)npc.level).players.stream().filter(t -> t.getName().getString().equals(ownerUUID)).findFirst().orElse(null);
	}
	
	public boolean hasOwner(){
		if(!infiniteDays && daysHired <= 0)
			return false;
		return ownerUUID != null && !ownerUUID.isEmpty();
	}
	
	@Override
	public void killed() {
		ownerUUID = null;
		daysHired = 0;
		hiredTime = 0;
		isFollowing = true;
	}
	
	@Override
	public void reset() {
		killed();
	}
	
	@Override
	public void interact(PlayerEntity player) {
		if(ownerUUID == null || ownerUUID.isEmpty()){
			npc.say(player, npc.advanced.getInteractLine());
			NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollowerHire, npc);
		}
		else if(player == owner && !disableGui){
			NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerFollower, npc);
		}
	}
	
	@Override
	public boolean defendOwner() {
		return isFollowing() && npc.job.getType() == JobType.GUARD;
	}

	@Override
	public void delete() {
		
	}
	
	@Override
	public boolean isFollowing(){
		return owner != null && isFollowing && getDays() > 0;
	}

	public void setOwner(PlayerEntity player) {
		UUID id = player.getUUID();
		if(ownerUUID == null || id == null || !ownerUUID.equals(id.toString()))
			killed();
		ownerUUID = id.toString();
	}
	
	@Override
	public int getDays(){
		if(infiniteDays)
			return 100;
		if(daysHired <= 0)
			return 0;
		int days = (int) ((npc.level.getGameTime() - hiredTime) / 24000);
		return daysHired - days;
	}

	@Override
	public void addDays(int days) {
		daysHired = days + getDays();
		hiredTime = npc.level.getGameTime();
	}

	@Override
	public boolean getInfinite(){
		return infiniteDays;
	}

	@Override
	public void setInfinite(boolean infinite){
		this.infiniteDays = infinite;
	}
	
	@Override
	public boolean getGuiDisabled(){
		return disableGui;
	}

	@Override
	public void setGuiDisabled(boolean disabled){
		disableGui = disabled;
	}
	
	@Override
	public boolean getRefuseSoulstone(){
		return refuseSoulStone;
	}

	@Override
	public void setRefuseSoulstone(boolean refuse){
		refuseSoulStone = refuse;
	}

	@Override
	public IPlayer getFollowing() {
		PlayerEntity owner = getOwner();
		if(owner != null)
			return (IPlayer) NpcAPI.Instance().getIEntity(owner);
		return null;
	}

	@Override
	public void setFollowing(IPlayer player) {
		if(player == null)
			setOwner(null);
		else
			setOwner(player.getMCEntity());
	}

	@Override
	public int getType() {
		return RoleType.FOLLOWER;
	}
	
}
