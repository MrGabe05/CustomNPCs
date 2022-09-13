package noppes.npcs.entity.data;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IPos;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobFarmer;

import java.util.ArrayList;
import java.util.List;

public class DataAI implements INPCAi {
	private final EntityNPCInterface npc;
	
	public int onAttack = 0; //0:fight 1:panic 2:retreat 3:nothing
	public int doorInteract = 2; //0:break 1:open 2:nothing
	public int findShelter = 2;
	public boolean canSwim = true;
	public boolean reactsToFire = false;
	public boolean avoidsWater = false;
	public boolean avoidsSun = false;
	public boolean returnToStart = true;
	public boolean directLOS = true;
	public boolean canLeap = false;
	public boolean canSprint = false;
	public boolean stopAndInteract = true;
	public boolean attackInvisible = false;
	
	public int movementType = 0; //0:ground, 1:flying, 2:swimming
		
	public int animationType = AnimationType.NORMAL;
	private int standingType = 0;
	private int movingType = 0;
	public boolean npcInteracting = true;

	public int orientation = 0;
	public float bodyOffsetX = 5, bodyOffsetY = 5, bodyOffsetZ = 5;
	public int walkingRange = 10;
	private int moveSpeed = 5;

	private List<int[]> movingPath = new ArrayList<int[]>();
	private BlockPos startPos = BlockPos.ZERO;
	public int movingPos = 0;
	public int movingPattern = 0; // 0:Looping 1:Backtracking

	public boolean movingPause = true;
	
	public DataAI(EntityNPCInterface npc){
		this.npc = npc;
	}

	public void readToNBT(CompoundNBT compound) {		
		canSwim = compound.getBoolean("CanSwim");
		reactsToFire = compound.getBoolean("ReactsToFire");
		setAvoidsWater(compound.getBoolean("AvoidsWater"));
		avoidsSun = compound.getBoolean("AvoidsSun");
		returnToStart = compound.getBoolean("ReturnToStart");
		onAttack = compound.getInt("OnAttack");
		doorInteract = compound.getInt("DoorInteract");
		findShelter = compound.getInt("FindShelter");
		directLOS = compound.getBoolean("DirectLOS");
		canLeap = compound.getBoolean("CanLeap");
		canSprint = compound.getBoolean("CanSprint");
		movingPause = compound.getBoolean("MovingPause");
		npcInteracting = compound.getBoolean("npcInteracting");
		stopAndInteract = compound.getBoolean("stopAndInteract");

		movementType = compound.getInt("MovementType");

		animationType = compound.getInt("MoveState");
		standingType = compound.getInt("StandingState");
		movingType = compound.getInt("MovingState");
	
		orientation = compound.getInt("Orientation");
		bodyOffsetY = compound.getFloat("PositionOffsetY");
		bodyOffsetZ = compound.getFloat("PositionOffsetZ");
		bodyOffsetX = compound.getFloat("PositionOffsetX");
		walkingRange = compound.getInt("WalkingRange");
		setWalkingSpeed(compound.getInt("MoveSpeed"));
		
		setMovingPath(NBTTags.getIntegerArraySet(compound.getList("MovingPathNew",10)));
		movingPos = compound.getInt("MovingPos");
		movingPattern = compound.getInt("MovingPatern");

		attackInvisible = compound.getBoolean("AttackInvisible");		
		
		if(compound.contains("StartPosNew")){
			int[] startPos = compound.getIntArray("StartPosNew");
			this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
		}
	}

	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("CanSwim", canSwim);
		compound.putBoolean("ReactsToFire", reactsToFire);
		compound.putBoolean("AvoidsWater", avoidsWater );
		compound.putBoolean("AvoidsSun", avoidsSun);
		compound.putBoolean("ReturnToStart", returnToStart);
		compound.putInt("OnAttack", onAttack);
		compound.putInt("DoorInteract", doorInteract);
		compound.putInt("FindShelter", findShelter);
		compound.putBoolean("DirectLOS", directLOS);
		compound.putBoolean("CanLeap", canLeap);
		compound.putBoolean("CanSprint", canSprint);
		compound.putBoolean("MovingPause", movingPause);
		compound.putBoolean("npcInteracting", npcInteracting);
		compound.putBoolean("stopAndInteract", stopAndInteract);

		compound.putInt("MoveState", animationType);
		compound.putInt("StandingState", standingType);
		compound.putInt("MovingState", movingType);

		compound.putInt("MovementType", movementType);
		
		compound.putInt("Orientation", orientation);
		compound.putFloat("PositionOffsetX", bodyOffsetX);
		compound.putFloat("PositionOffsetY", bodyOffsetY);
		compound.putFloat("PositionOffsetZ", bodyOffsetZ);
		compound.putInt("WalkingRange", walkingRange);
		compound.putInt("MoveSpeed", moveSpeed);
		
		compound.put("MovingPathNew", NBTTags.nbtIntegerArraySet(movingPath));
		compound.putInt("MovingPos", movingPos);
		compound.putInt("MovingPatern", movingPattern);
		
		setAvoidsWater(avoidsWater);
		
		compound.putIntArray("StartPosNew", getStartArray());

		compound.putBoolean("AttackInvisible", attackInvisible);
		
		return compound;
	}
	
	public List<int[]> getMovingPath() {
		if(movingPath.isEmpty() && startPos != null)
			movingPath.add(getStartArray());
		return movingPath;
	}
	
	public void setMovingPath(List<int[]> list) {
		movingPath = list;
		if(!movingPath.isEmpty()){
			int[] startPos = movingPath.get(0);
			this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
		}
	}
	
	public BlockPos startPos(){
		if(startPos == null || startPos == BlockPos.ZERO)
			startPos = npc.blockPosition();
		return startPos;
	}
	
	public int[] getStartArray(){
		BlockPos pos = startPos();
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}
	
	public int[] getCurrentMovingPath(){
		List<int[]> list = getMovingPath();
		int size = list.size();
		if(size == 1){
			return list.get(0);
		}
		int pos = movingPos;
		if(movingPattern == 0){
			if(pos >= size) {
				pos = movingPos = 0;
			}
		}
		if(movingPattern == 1){
			int size2 = size * 2 - 1;

			if(pos >= size2) {
				pos = movingPos = 0;
			}
			else if(pos >= size) {
				pos = size2 - pos;
			}
		}
		
		return list.get(pos);
	}
	
	public void clearMovingPath(){
		movingPath.clear();
		movingPos = 0;
	}
	
	public void setMovingPathPos(int m_pos, int[] pos){
		if (m_pos<0)
			m_pos=0;
		else
		{
			
		}
		movingPath.set(m_pos, pos);
	}
	
	public int[] getMovingPathPos(int m_pos){
		return movingPath.get(m_pos);
	}	
	
	public void appendMovingPath(int[] pos){
		movingPath.add(pos);
	}
	
	public int getMovingPos(){
		return movingPos;
	}
	
	public void setMovingPos(int pos){
		movingPos=pos;
	}
	
	public int getMovingPathSize(){
		return movingPath.size();
	}
	
	public void incrementMovingPath(){
		List<int[]> list = getMovingPath();
		if(list.size() == 1){
			movingPos = 0;
			return;
		}
		movingPos++;
		if(movingPattern == 0){
			movingPos = movingPos % list.size();
		}
		else if(movingPattern == 1){
			int size = list.size() * 2 - 1;
			movingPos = movingPos % size;
		}
	}
	
	public void decreaseMovingPath(){
		List<int[]> list = getMovingPath();
		if(list.size() == 1){
			movingPos = 0;
			return;
		}

		movingPos--;
		if(movingPos < 0) {
			if(movingPattern == 0){
				movingPos = list.size() - 1;
			}
			else if(movingPattern == 1){
				movingPos = list.size() * 2 - 2;
			}
		}
	}
	
	public double distanceToSqrToPathPoint(){
		int[] pos = getCurrentMovingPath();
		return npc.distanceToSqr(pos[0] + 0.5, pos[1], pos[2] + 0.5);
	}

	public IPos getStartPos() {
		return new BlockPosWrapper(startPos());
	}
	
	public void setStartPos(BlockPos pos) {
		startPos = pos;
	}
	
	public void setStartPos(IPos pos) {
		startPos = pos.getMCBlockPos();
	}
	
	public void setStartPos(double x, double y, double z){
		startPos = new BlockPos(x, y, z);
	}
	
	@Override
	public void setReturnsHome(boolean bo){
		returnToStart = bo;
	}
	
	@Override
	public boolean getReturnsHome(){
		return returnToStart;
	}
	
	public boolean shouldReturnHome(){
		if(npc.job.getType() == JobType.BUILDER && ((JobBuilder)npc.job).isBuilding())
			return false;
		if(npc.job.getType() == JobType.FARMER && ((JobFarmer)npc.job).isPlucking())
			return false;
			
		return returnToStart;
	}

	@Override
	public int getAnimation(){
		return animationType;
	}

	@Override
	public int getCurrentAnimation(){
		return npc.currentAnimation;
	}

	@Override
	public void setAnimation(int type){
		animationType = type;		
	}

	@Override
	public int getRetaliateType(){
		return onAttack;
	}
	
	@Override
	public void setRetaliateType(int type){
		if(type < 0 || type > 3)
			throw new CustomNPCsException("Unknown retaliation type: " + type);
		
		onAttack = type;
		npc.updateAI = true;
	}

	@Override
	public int getMovingType(){		
		return movingType;
	}

	@Override
	public void setMovingType(int type){
		if(type < 0 || type > 2)
			throw new CustomNPCsException("Unknown moving type: " + type);
		movingType = type;
		npc.updateAI = true;
	}
	
	@Override
	public int getStandingType(){
		return standingType;
	}
	
	@Override
	public void setStandingType(int type){
		if(type < 0 || type > 3)
			throw new CustomNPCsException("Unknown standing type: " + type);
		standingType = type;
		npc.updateAI = true;
	}

	@Override
	public boolean getAttackInvisible(){
		return attackInvisible;
	}

	@Override
	public void setAttackInvisible(boolean attack){
		attackInvisible = attack;
	}

	@Override
	public int getWanderingRange(){
		return walkingRange;
	}
	
	@Override
	public void setWanderingRange(int range){
		if(range < 1 || range > 50)
			throw new CustomNPCsException("Bad wandering range: " + range);
		walkingRange = range;
	}
	
	@Override
	public boolean getInteractWithNPCs(){
		return npcInteracting;
	}
	
	@Override
	public void setInteractWithNPCs(boolean interact){
		npcInteracting = interact;
	}
	
	@Override
	public boolean getStopOnInteract(){
		return stopAndInteract;
	}
	
	@Override
	public void setStopOnInteract(boolean stopOnInteract){
		stopAndInteract = stopOnInteract;
	}

	@Override
	public int getWalkingSpeed(){
		return this.moveSpeed;
	}
	
	@Override
	public void setWalkingSpeed(int speed){
		if(speed < 0 || speed > 10)
			throw new CustomNPCsException("Wrong speed: " + speed);
		this.moveSpeed = speed;
		npc.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(npc.getSpeed());
		npc.getAttribute(Attributes.FLYING_SPEED).setBaseValue(npc.getSpeed() * 2);
	}

	@Override
	public int getMovingPathType(){
		return movingPattern;
	}
	
	@Override
	public boolean getMovingPathPauses(){
		return movingPause;
	}
	
	@Override
	public void setMovingPathType(int type, boolean pauses){
		if(type < 0 && type > 1)
			throw new CustomNPCsException("Moving path type: " + type);
		movingPattern = type;		
		movingPause = pauses;
	}

	
	/**
	 * @return 0:Break, 1:Open, 2:Disabled
	 */
	@Override
	public int getDoorInteract(){
		return doorInteract;
	}
	
	/**
	 * @param type 0:Break, 1:Open, 2:Disabled
	 */
	@Override
	public void setDoorInteract(int type){
		doorInteract = type;
		npc.updateAI = true;
	}
	
	@Override
	public boolean getCanSwim(){
		return canSwim;
	}
	
	@Override
	public void setCanSwim(boolean canSwim){
		this.canSwim = canSwim;
	}
	
	@Override
	public int getSheltersFrom(){
		return findShelter;
	}
	
	@Override
	public void setSheltersFrom(int type){
		findShelter = type;
		npc.updateAI = true;
	}
	
	@Override
	public boolean getAttackLOS(){
		return directLOS;
	}
	
	@Override
	public void setAttackLOS(boolean enabled){
		directLOS = enabled;
		npc.updateAI = true;
	}
	
	@Override
	public boolean getAvoidsWater(){
		return avoidsWater;
	}
	
	@Override
	public void setAvoidsWater(boolean enabled){
		npc.setPathfindingMalus(PathNodeType.WATER, movementType != 2 && enabled?-1.0F:0);
		this.avoidsWater = enabled;
	}
	
	@Override
	public boolean getLeapAtTarget(){
		return canLeap;
	}
	
	@Override
	public void setLeapAtTarget(boolean leap){
		this.canLeap = leap;
		npc.updateAI = true;
	}

	@Override
	public int getNavigationType() {
		return movementType;
	}

	@Override
	public void setNavigationType(int type) {
		movementType = type;
	}
}
