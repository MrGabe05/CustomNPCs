package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;

public class EntityNpcDragon extends EntityNPCInterface{
    private EntitySize size = new EntitySize(1.8f, 1.4f, false);
	public EntityNpcDragon(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        field_40162_d = new double[64][3];
        field_40164_e = -1;
        prevAnimTime = 0.0F;
        animTime = 0.0F;
        field_40178_aA = 0;
		scaleX = 0.4f;
		scaleY = 0.4f;
		scaleZ = 0.4f;
		display.setSkinTexture("customnpcs:textures/entity/dragon/BlackDragon.png");
	}

    public double field_40162_d[][];
    public int field_40164_e;
    public float prevAnimTime;
    public float animTime;
    public int field_40178_aA;
    
    public boolean isFlying = false;
    
    @Override
    public double getPassengersRidingOffset(){
    	return 1.1;
    }

    public double[] getMovementOffsets(int i, float f)
    {
        f = 1.0F - f;
        int j = field_40164_e - i * 1 & 0x3f;
        int k = field_40164_e - i * 1 - 1 & 0x3f;
        double ad[] = new double[3];
        double d = field_40162_d[j][0];
        double d1;
        for(d1 = field_40162_d[k][0] - d; d1 < -180D; d1 += 360D) { }
        for(; d1 >= 180D; d1 -= 360D) { }
        ad[0] = d + d1 * (double)f;
        d = field_40162_d[j][1];
        d1 = field_40162_d[k][1] - d;
        ad[1] = d + d1 * (double)f;
        ad[2] = field_40162_d[j][2] + (field_40162_d[k][2] - field_40162_d[j][2]) * (double)f;
        return ad;
    }

    @Override
    public void tick() {
    	removed = true;
    	setNoAi(true);

    	if(!level.isClientSide){
	    	CompoundNBT compound = new CompoundNBT();
	    	
	    	addAdditionalSaveData(compound);
	    	EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
	    	npc.readAdditionalSaveData(compound);
	    	ModelData data = npc.modelData;
            data.setEntity(CustomEntities.entityNpcDragon.toString());

            level.addFreshEntity(npc);
    	}
        super.tick();
    }

    private boolean exploded = false;
    @Override
    public void aiStep()
    {
        prevAnimTime = animTime;
        if(level.isClientSide && getHealth() <= 0)
        {
        	if(!exploded){
	        	exploded = true;
	            float f = (random.nextFloat() - 0.5F) * 8F;
	            float f2 = (random.nextFloat() - 0.5F) * 4F;
	            float f4 = (random.nextFloat() - 0.5F) * 8F;
	            level.addParticle(ParticleTypes.EXPLOSION, getX() + (double)f, getY() + 2D + (double)f2, getZ() + (double)f4, 0.0D, 0.0D, 0.0D);
        	}
        }
        else{
	        exploded = false;
	        //func_41011_ay();
	        float f1 = 0.045f;
	        f1 *= (float)Math.pow(2D, getDeltaMovement().y);
	        animTime += f1 * 0.5F;
        }
        super.aiStep();
    }

//    public float ticks = 0.0f;
//    public void updateRiderPosition()
//    {
//        if (riddenByEntity == null)
//        {
//        	super.updateRiderPosition();
//            return;
//        }
//        else
//        {	
//            float f6 = field_40173_aw + (field_40172_ax - field_40173_aw) * ticks;
//            float f7 = (float)(Math.sin(f6 * 3.141593F * 2.0F - 1.0F) + 1.0D);
//            riddenByEntity.setPos(posX , posY + getPassengersRidingOffset() + riddenByEntity.getYOffset() - f7 / 12, posZ);
//            return;
//        }
//    }
    @Override
	public EntitySize getDimensions(Pose pos) {
		return size;
	}

}
