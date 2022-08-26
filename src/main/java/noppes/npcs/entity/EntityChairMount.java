package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomEntities;

public class EntityChairMount extends Entity{

	public EntityChairMount(EntityType type, World world) {
		super(type, world);
	}

	@Override
    public double getPassengersRidingOffset(){
        return 0.5f;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void baseTick(){
    	super.baseTick();
    	if(this.level != null && !this.level.isClientSide && getPassengers().isEmpty())
    		removed = true;
    }
    
    @Override
    public boolean isInvulnerableTo(DamageSource source){
        return true;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    public boolean isInvisible(){
        return true;
    }

    @Override
    public void move(MoverType type, Vector3d vec) {
    }

    @Override
    public void load(CompoundNBT tagCompound) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    public CompoundNBT saveWithoutId(CompoundNBT tagCompound) {
	    return tagCompound;
    }

	@Override
    public boolean canBeCollidedWith(){
        return false;
    }

	@Override
    public boolean isPushable(){
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_, boolean bo){
        this.setPos(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRot(p_70056_7_, p_70056_8_);
    }
}
