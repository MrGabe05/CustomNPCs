package noppes.npcs.roles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class JobPuppet extends JobInterface implements IJobPuppet{
	public PartConfig head = new PartConfig();
	public PartConfig larm = new PartConfig();
	public PartConfig rarm = new PartConfig();
	public PartConfig body = new PartConfig();
	public PartConfig lleg = new PartConfig();
	public PartConfig rleg = new PartConfig();
	
	public PartConfig head2 = new PartConfig();
	public PartConfig larm2 = new PartConfig();
	public PartConfig rarm2 = new PartConfig();
	public PartConfig body2 = new PartConfig();
	public PartConfig lleg2 = new PartConfig();
	public PartConfig rleg2 = new PartConfig();
	
	public boolean whileStanding = true;
	public boolean whileAttacking = false;
	public boolean whileMoving = false;
	
	public boolean animate = false;	
	public int animationSpeed = 4;

	public JobPuppet(EntityNPCInterface npc) {
		super(npc);
	}
	
	@Override
	public IJobPuppetPart getPart(int part){
		if(part == 0){
			return head;
		}
		else if(part == 1){
			return larm;
		}
		else if(part == 2){
			return rarm;
		}
		else if(part == 3){
			return body;
		}
		else if(part == 4){
			return lleg;
		}
		else if(part == 5){
			return rleg;
		}
		else if(part == 6){
			return head2;
		}
		else if(part == 7){
			return larm2;
		}
		else if(part == 8){
			return rarm2;
		}
		else if(part == 9){
			return body2;
		}
		else if(part == 10){
			return lleg2;
		}
		else if(part == 11){
			return rleg2;
		}
		
		throw new CustomNPCsException("Unknown part " + part);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("PuppetHead", head.writeNBT());
		compound.put("PuppetLArm", larm.writeNBT());
		compound.put("PuppetRArm", rarm.writeNBT());
		compound.put("PuppetBody", body.writeNBT());
		compound.put("PuppetLLeg", lleg.writeNBT());
		compound.put("PuppetRLeg", rleg.writeNBT());
		
		compound.put("PuppetHead2", head2.writeNBT());
		compound.put("PuppetLArm2", larm2.writeNBT());
		compound.put("PuppetRArm2", rarm2.writeNBT());
		compound.put("PuppetBody2", body2.writeNBT());
		compound.put("PuppetLLeg2", lleg2.writeNBT());
		compound.put("PuppetRLeg2", rleg2.writeNBT());

		compound.putBoolean("PuppetStanding", whileStanding);
		compound.putBoolean("PuppetAttacking", whileAttacking);
		compound.putBoolean("PuppetMoving", whileMoving);
		compound.putBoolean("PuppetAnimate", animate);
		
		compound.putInt("PuppetAnimationSpeed", animationSpeed);
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		head.readNBT(compound.getCompound("PuppetHead"));
		larm.readNBT(compound.getCompound("PuppetLArm"));
		rarm.readNBT(compound.getCompound("PuppetRArm"));
		body.readNBT(compound.getCompound("PuppetBody"));
		lleg.readNBT(compound.getCompound("PuppetLLeg"));
		rleg.readNBT(compound.getCompound("PuppetRLeg"));
		
		head2.readNBT(compound.getCompound("PuppetHead2"));
		larm2.readNBT(compound.getCompound("PuppetLArm2"));
		rarm2.readNBT(compound.getCompound("PuppetRArm2"));
		body2.readNBT(compound.getCompound("PuppetBody2"));
		lleg2.readNBT(compound.getCompound("PuppetLLeg2"));
		rleg2.readNBT(compound.getCompound("PuppetRLeg2"));

		whileStanding = compound.getBoolean("PuppetStanding");
		whileAttacking = compound.getBoolean("PuppetAttacking");
		whileMoving = compound.getBoolean("PuppetMoving");
		
		setIsAnimated(compound.getBoolean("PuppetAnimate"));
		setAnimationSpeed(compound.getInt("PuppetAnimationSpeed"));
	}
	
	@Override
	public boolean aiShouldExecute() {
		return false;
	}
	
	private int prevTicks = 0;

	private int startTick = 0;
	private float val = 0;
	private float valNext = 0;
	
	private float calcRotation(float r, float r2, float partialTicks){
		if(!animate)
			return r;
		if(prevTicks != npc.tickCount){
			float speed = 0;
			if(animationSpeed == 0)
				speed = 40;
			else if(animationSpeed == 1)
				speed = 24;
			else if(animationSpeed == 2)
				speed = 13;
			else if(animationSpeed == 3)
				speed = 10;
			else if(animationSpeed == 4)
				speed = 7;
			else if(animationSpeed == 5)
				speed = 4;
			else if(animationSpeed == 6)
				speed = 3;
			else if(animationSpeed == 7)
				speed = 2;
			
			int ticks = npc.tickCount - startTick;
			val = 1 - (MathHelper.cos(ticks / speed * (float)Math.PI/2) + 1) / 2;
			valNext = 1 - (MathHelper.cos((ticks + 1) / speed * (float)Math.PI/2) + 1) / 2;			
			prevTicks = npc.tickCount;
		}
		float f = val + (valNext - val) * partialTicks;
		return r + (r2 - r) * f;
	}

	public float getRotationX(PartConfig part1, PartConfig part2, float partialTicks) {
		return calcRotation(part1.rotationX, part2.rotationX, partialTicks);
	}

	public float getRotationY(PartConfig part1, PartConfig part2, float partialTicks) {
		return calcRotation(part1.rotationY, part2.rotationY, partialTicks);
	}

	public float getRotationZ(PartConfig part1, PartConfig part2, float partialTicks) {
		return calcRotation(part1.rotationZ, part2.rotationZ, partialTicks);
	}

	@Override
	public void reset() {
		val = 0;
		valNext = 0;
		prevTicks = 0;
		startTick = npc.tickCount;
	}
	public void delete() {
	}

	public boolean isActive() {
		if(!npc.isAlive())
			return false;
		
		if(whileAttacking && npc.isAttacking() || whileMoving && npc.isWalking() || whileStanding && !npc.isWalking())
			return true;
		return false;
	}
	
	public class PartConfig implements IJobPuppetPart{
		public float rotationX = 0f;
		public float rotationY = 0f;
		public float rotationZ = 0f;
		
		public boolean disabled = false;
		
		public CompoundNBT writeNBT(){
			CompoundNBT compound = new CompoundNBT();
			compound.putFloat("RotationX", rotationX);
			compound.putFloat("RotationY", rotationY);
			compound.putFloat("RotationZ", rotationZ);
			
			compound.putBoolean("Disabled", disabled);
			return compound;
		}
		
		public void readNBT(CompoundNBT compound){
			rotationX = ValueUtil.correctFloat(compound.getFloat("RotationX"), -1f, 1f);
			rotationY = ValueUtil.correctFloat(compound.getFloat("RotationY"), -1f, 1f);
			rotationZ = ValueUtil.correctFloat(compound.getFloat("RotationZ"), -1f, 1f);
			
			disabled = compound.getBoolean("Disabled");
		}

		@Override
		public int getRotationX() {
			return (int) ((rotationX + 1) * 180);
		}

		@Override
		public int getRotationY() {
			return (int) ((rotationY + 1) * 180);
		}

		@Override
		public int getRotationZ() {
			return (int) ((rotationZ + 1) * 180);
		}

		@Override
		public void setRotation(int x, int y, int z) {
			disabled = false;
			rotationX = ValueUtil.correctFloat(x / 180f - 1, -1f, 1f);
			rotationY = ValueUtil.correctFloat(y / 180f - 1, -1f, 1f);
			rotationZ = ValueUtil.correctFloat(z / 180f - 1, -1f, 1f);
			JobPuppet.this.npc.updateClient = true;
		}
	}

	@Override
	public boolean getIsAnimated() {
		return animate;
	}

	@Override
	public void setIsAnimated(boolean bo) {
		animate = bo;
		if(!bo) {
			val = 0;
			valNext = 0;
			prevTicks = 0;
		}
		else {
			startTick = npc.tickCount;
		}
		this.npc.updateClient = true;
	}

	@Override
	public int getAnimationSpeed() {
		return animationSpeed;
	}

	@Override
	public void setAnimationSpeed(int speed) {
		animationSpeed = ValueUtil.CorrectInt(speed, 0, 7);
		this.npc.updateClient = true;
	}

	@Override
	public int getType() {
		return JobType.PUPPET;
	}
}
