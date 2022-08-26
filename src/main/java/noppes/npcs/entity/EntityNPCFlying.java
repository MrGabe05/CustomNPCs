package noppes.npcs.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class EntityNPCFlying extends EntityNPCInterface{

	public EntityNPCFlying(EntityType<? extends CreatureEntity> type, World world) {
		super(type, world);
	}

	@Override
	public boolean canFly(){
		return ais.movementType > 0;
	}
	
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
    	if(!canFly())
    		return super.causeFallDamage(distance, damageMultiplier);
    	return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!canFly()) {
            super.checkFallDamage(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void travel(Vector3d v){
    	if(!canFly()){
    		super.travel(v);
    		return;
    	}
    	Vector3d m = getDeltaMovement();
    	if (!this.isInWater() && ais.movementType == 2){
    	    m = m.subtract(0, 0.15, 0);
        }
    	
        if (this.isInWater() && ais.movementType == 1){
            this.moveRelative(0.02F, v);
            this.move(MoverType.SELF, m);
            m = getDeltaMovement().scale(0.8);
        }
        else if (this.isInLava()){
            this.moveRelative(0.02F, v);
            this.move(MoverType.SELF, m);
            m = getDeltaMovement().scale(0.5);
        }
        else{
            BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getSlipperiness(this.level, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getSlipperiness(this.level, ground, this) * 0.91F;
            }

            this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, v);
            this.move(MoverType.SELF, this.getDeltaMovement());
            m = this.getDeltaMovement().scale((double)f);
        }
        setDeltaMovement(m);


        this.calculateEntityAnimation(this, false);
    }

    @Override
    public boolean onClimbable(){
        return false;
    }
}
