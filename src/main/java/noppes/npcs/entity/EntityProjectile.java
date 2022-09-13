package noppes.npcs.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomEntities;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.ParticleType;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.event.ProjectileEvent;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.data.DataRanged;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityProjectile extends ThrowableEntity {
    private static final DataParameter<Boolean> Gravity = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Arrow = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Is3d = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Glows = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Rotating = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Sticks = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.BOOLEAN);

    private static final DataParameter<ItemStack> ItemStackThrown = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> Velocity = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.INT);
    private static final DataParameter<Integer> Size = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.INT);
    private static final DataParameter<Integer> Particle = EntityDataManager.defineId(EntityProjectile.class, DataSerializers.INT);

    private BlockPos tilePos = BlockPos.ZERO;
    private BlockState inBlock;
    protected boolean inGround = false;
    public int throwableShake = 0;
    public int arrowShake = 0;
    
    public boolean canBePickedUp = false;
    public boolean destroyedOnEntityHit = true;

    /**
     * Is the entity that throws this 'thing' (snowball, ender pearl, eye of ender or potion)
     */
    private Entity thrower;
    private EntityNPCInterface npc;
    
    private String throwerName = null;
    private int ticksInGround;
    public int ticksInAir = 0;
    
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;
        
    /**
     * Properties settable by GUI
     */
    
    public float damage = 5;
    public int punch = 0;
    public boolean accelerate = false;
    public boolean explosiveDamage = true;
    public int explosiveRadius = 0;
    public int effect = PotionEffectType.NONE;
    public int duration = 5;
    public int amplify = 0;
    public int accuracy = 60;

    public IProjectileCallback callback;   
    
    public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    
    public EntityProjectile(EntityType type, World par1World) {
        super(type, par1World);
    }

    @Override
    protected void defineSynchedData() {
    	this.entityData.define(ItemStackThrown, ItemStack.EMPTY);
    	this.entityData.define(Velocity, 10);
    	this.entityData.define(Size, 10);
    	this.entityData.define(Particle, 0);

    	this.entityData.define(Gravity, false);
    	this.entityData.define(Glows, false);
    	this.entityData.define(Arrow, false);
    	this.entityData.define(Is3d, false);
    	this.entityData.define(Rotating, false);
    	this.entityData.define(Sticks, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double par1){
        double d1 = this.getBoundingBox().getSize() * 4.0D;
        d1 *= 64.0D;
        return par1 < d1 * d1;
    }
    
    public EntityProjectile(World par1World, LivingEntity limbSwingAmountEntityLiving, ItemStack item, boolean isNPC){
        super( CustomEntities.entityProjectile, par1World);
        this.thrower = limbSwingAmountEntityLiving;
        if(this.thrower != null)
        	this.throwerName = this.thrower.getUUID().toString();
        setThrownItem(item);
        this.entityData.set(Arrow, this.getItem() == Items.ARROW);
        this.moveTo(limbSwingAmountEntityLiving.getX(), limbSwingAmountEntityLiving.getY() + (double)limbSwingAmountEntityLiving.getEyeHeight(), limbSwingAmountEntityLiving.getZ(), limbSwingAmountEntityLiving.yRot, limbSwingAmountEntityLiving.xRot);
        double posX = this.getX() - (MathHelper.cos(this.yRot / 180.0F * (float)Math.PI) * 0.1F);
        double posY = this.getY() - 0.1f;
        double posZ = this.getZ() - (MathHelper.sin(this.yRot / 180.0F * (float)Math.PI) * 0.1F);
        this.setPos(posX, posY, posZ);
        
        if (isNPC) {
        	this.npc = (EntityNPCInterface) this.thrower;
        	this.getStatProperties(this.npc.stats.ranged);
			this.refreshDimensions();
        }
    }

	@Override
	public void onSyncedDataUpdated(DataParameter<?> para) {
		if (Size.equals(para)) {
			this.refreshDimensions();
		}
	}
    
    public void setThrownItem(ItemStack item){
        entityData.set(ItemStackThrown, item);
    }
    
    public int getSize(){
    	return this.entityData.get(Size);
    }

    @Override
    public EntitySize getDimensions(Pose pose){
    	return new EntitySize(getSize() / 10f , getSize() / 10f, false);
	}
    
    /**
     * Par: X, Y, Z, Angle, Accuracy
     */
    @Override
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
        float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        float f3 = MathHelper.sqrt(par1 * par1 + par5 * par5);
        float yaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        float pitch = this.hasGravity() ? par7 : (float)(Math.atan2(par3, f3) * 180.0D / Math.PI);
        this.yRotO = this.yRot = yaw;
        this.xRotO = this.xRot = pitch;
		Vector3d m = new Vector3d((MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)),
				(MathHelper.sin((pitch + 1.0F) / 180.0F * (float)Math.PI)),
				(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)))
        .add(this.random.nextGaussian() * 0.0075D * par8, this.random.nextGaussian() * 0.0075D * par8, this.random.nextGaussian() * 0.0075D * par8)
		.scale(this.getSpeed());
        setDeltaMovement(m);
        this.accelerationX = par1 / f2 * 0.1D;
        this.accelerationY = par3 / f2 * 0.1D;
        this.accelerationZ = par5 / f2 * 0.1D;
        this.ticksInGround = 0;
    }
    
    /**
     * get an angle for firing at coordinates XYZ
     * Par: X Distance, Y Distance, Z Distance, Horizontial Distance
     */
    public float getAngleForXYZ(double varX, double varY, double varZ, double horiDist, boolean arc) {
    	float g = this.getGravity();
    	float var1 = this.getSpeed() * this.getSpeed();
    	double var2 = (g * horiDist);
    	double var3 = ((g * horiDist * horiDist) + (2 * varY * var1));
    	double var4 = (var1 * var1) - (g * var3);
    	if (var4 < 0) return 30.0F;
    	float var6 = arc ? var1 + MathHelper.sqrt(var4) : var1 - MathHelper.sqrt(var4);
    	float var7 = (float) (Math.atan2(var6 , var2) * 180.0D / Math.PI);
    	return var7;
    }

    public void shoot(float speed){
        double varX = (-MathHelper.sin(this.yRot / 180.0F * (float)Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float)Math.PI));
        double varZ = (MathHelper.cos(this.yRot / 180.0F * (float)Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float)Math.PI));
        double varY = (-MathHelper.sin(this.xRot / 180.0F * (float)Math.PI));
        this.shoot(varX, varY, varZ, -xRot, speed);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double par1, double par3, double par5, float par7, float par8, int par9, boolean bo){
    	if(level.isClientSide && inGround)
    		return;
        this.setPos(par1, par3, par5);
        this.setRot(par7, par8);
    }
        
    @Override
    public void tick(){
        super.baseTick();
		if(++tickCount % 10 == 0){
			EventHooks.onProjectileTick(this);
		}
		Vector3d motion = this.getDeltaMovement();
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = MathHelper.sqrt(getHorizontalDistanceSqr(motion));
			this.yRot = (float)(MathHelper.atan2(motion.x, motion.z) * (double)(180F / (float)Math.PI));
			this.xRot = (float)(MathHelper.atan2(motion.y, f) * (double)(180F / (float)Math.PI));
			this.yRotO = this.yRot;
			this.xRotO = this.xRot;
		}

        if (this.effect == PotionEffectType.FIRE && !this.inGround) {
			//this.setRemainingFireTicks(1);
		}

        BlockState state = this.level.getBlockState(tilePos);

        if ((this.isArrow() || this.sticksToWalls()) && tilePos != BlockPos.ZERO){
			VoxelShape shape = state.getShape(this.level, tilePos);
			if(!shape.isEmpty()){
				AxisAlignedBB axisalignedbb = shape.bounds();

				if (axisalignedbb != null && axisalignedbb.contains(this.position()))
				{
					this.inGround = true;
				}
			}
        }

        if (this.arrowShake > 0){
            --this.arrowShake;
        }

        if (this.inGround){
            if (state == this.inBlock){
                ++this.ticksInGround;

                if (this.ticksInGround == 1200){
                    this.remove();
                }
            }
            else{
                this.inGround = false;
                this.setDeltaMovement(this.getDeltaMovement().multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else{
            ++this.ticksInAir;

            if (this.ticksInAir == 1200){
                this.remove();
            }
	        Vector3d pos = this.position();
	        Vector3d nextpos = pos.add(motion);
			RayTraceResult movingobjectposition = ProjectileHelper.getHitResult(this, this::canHit);

	        if (movingobjectposition != null && movingobjectposition.getType() != RayTraceResult.Type.MISS)
	        {
				this.entityData.set(Rotating, false);
				this.onHit(movingobjectposition);
	        }

			motion = this.getDeltaMovement();

	        float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(motion));
			this.yRot = (float)(MathHelper.atan2(motion.x, motion.z) * (double)(180F / (float)Math.PI));

			this.xRot = (float)(MathHelper.atan2(motion.y, f1) * (double)(180F / (float)Math.PI));
			this.xRot = lerpRotation(this.xRotO, this.xRot);
			this.yRot = lerpRotation(this.yRotO, this.yRot);
	        if (this.isRotating()) {
	        	int spin = isBlock()? 10 : 20;
     	        	this.xRot -= spin * getSpeed();
	        }
	        float f2 = this.getMotionFactor();
	        float f3 = this.getGravity();

			if (this.isInWater()) {
				for(int j = 0; j < 4; ++j) {
					float f4 = 0.25F;
					this.level.addParticle(ParticleTypes.BUBBLE, nextpos.x - motion.x * 0.25D, nextpos.y - motion.y * 0.25D, nextpos.z - motion.z * 0.25D, motion.x, motion.y, motion.z);
				}

				f2 = 0.6F;
			}
			motion = motion.scale(f2);

	        if (hasGravity()) {
				motion = motion.subtract(0, f3,0);
			}

	        if (accelerate)
	        {
				motion = motion.add(this.accelerationX, this.accelerationY,this.accelerationZ);
	        }

	        if (level.isClientSide && this.entityData.get(Particle) > 0){
	        	this.level.addParticle(ParticleType.getMCType(entityData.get(Particle)), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
	        }
	        this.setDeltaMovement(motion);
	        this.setPos(nextpos.x, nextpos.y, nextpos.z);
	        this.checkInsideBlocks();
        }
    }

	protected boolean canHit(Entity entity) {
		if (!super.canHitEntity(entity) || entity == thrower || npc != null && (entity == this.npc || npc.isAlliedTo(entity))) {
			return false;
		}
		if(entity instanceof PlayerEntity){
			PlayerEntity entityplayer = (PlayerEntity)entity;
			return !entityplayer.abilities.invulnerable &&
					(!(this.thrower instanceof PlayerEntity) || ((PlayerEntity) this.thrower).canHarmPlayer(entityplayer));
		}
		return true;
	}

    public boolean isBlock(){
    	ItemStack item = this.getItemDisplay();
    	if(item.isEmpty())
    		return false;
    	return item.getItem() instanceof BlockItem;
    }
    
    private Item getItem(){
    	ItemStack item = this.getItemDisplay();
    	if(item.isEmpty())
    		return Items.AIR;
    	return item.getItem();
    }
    
    protected float getMotionFactor()
    {
        return accelerate ? 0.95F : 1.0F;
    }

    /**
     * Called when this ThrowableEntity hits a block or entity.
     */
    @Override
    protected void onHit(RayTraceResult movingobjectposition) {
    	if(!level.isClientSide) {
    		BlockPos pos = BlockPos.ZERO;
    		ProjectileEvent.ImpactEvent event;
			Entity e = null;
    		if(movingobjectposition.getType() == RayTraceResult.Type.ENTITY) {
    			e = ((EntityRayTraceResult)movingobjectposition).getEntity();
    			pos = e.blockPosition();
    			event = new ProjectileEvent.ImpactEvent((IProjectile) NpcAPI.Instance().getIEntity(this), 0, e);
    		}
    		else {
    			pos = ((BlockRayTraceResult)movingobjectposition).getBlockPos();
				BlockState state = this.level.getBlockState(pos);
    			event = new ProjectileEvent.ImpactEvent((IProjectile) NpcAPI.Instance().getIEntity(this), 1, NpcAPI.Instance().getIBlock(level, pos));
    		}
    		
    		if(pos == BlockPos.ZERO)
    			pos = new BlockPos(movingobjectposition.getLocation());
    		if(callback != null && callback.onImpact(this, pos, e))
    			return;
    		
    		EventHooks.onProjectileImpact(this, event);
    	}
    	
    	if (movingobjectposition.getType() == RayTraceResult.Type.ENTITY)
        {
			Entity e = ((EntityRayTraceResult)movingobjectposition).getEntity();
    		float damage = this.damage;
    		if(damage == 0)
    			damage = 0.001f;

            if (e.hurt(DamageSource.thrown(this, this.getOwner()), damage))
            {
	            if (e instanceof LivingEntity)
	            {
	            	LivingEntity entityliving = (LivingEntity)e;
	
	                if (!this.level.isClientSide && (this.isArrow() || this.sticksToWalls()))
	                {
	                    entityliving.setArrowCount(entityliving.getArrowCount() + 1);
	                }

	                if (destroyedOnEntityHit && !(e instanceof EndermanEntity))
                    {
                        this.remove();
                    }

					if (this.effect != PotionEffectType.NONE){
						if (this.effect != PotionEffectType.FIRE){
							Effect p = PotionEffectType.getMCType(effect);
							entityliving.addEffect(new EffectInstance(p, this.duration * 20, this.amplify));
						}
						else
						{
							entityliving.setRemainingFireTicks(duration * 20);
						}
					}
	            }
	            
	            if (this.isBlock())
	    		{
					this.level.levelEvent(null, 2001, e.blockPosition(), Block.getId(((BlockItem)getItem()).getBlock().defaultBlockState()));
	    		}
	            else if (!this.isArrow() && !this.sticksToWalls()){
			        for (int i = 0; i < 8; ++i){
	                    level.addParticle(new ItemParticleData(ParticleTypes.ITEM, getItemDisplay()), getX(), getY(), getZ(), random.nextGaussian() * 0.15, random.nextGaussian() * 0.2, random.nextGaussian() * 0.15);
			        }
	    		}
	            
	            if (this.punch > 0)
	            {
	            	Vector3d m = getDeltaMovement();
	                float f3 = MathHelper.sqrt(getHorizontalDistanceSqr(m));
	
	                if (f3 > 0.0F)
	                {
	                    e.push(m.x() * (double)this.punch * 0.6 / (double)f3, 0.1D, m.z() * (double)this.punch * 0.6 / (double)f3);
	                }
	            }
            } 
            else if (this.hasGravity() && (this.isArrow() || this.sticksToWalls()))
            {
            	setDeltaMovement(getDeltaMovement().scale(-0.1));
                this.yRot += 180.0F;
                this.yRotO += 180.0F;
                this.ticksInAir = 0;
            }
        }
    	else
    	{
    		if (this.isArrow() || this.sticksToWalls()) {
    			this.tilePos = ((BlockRayTraceResult)movingobjectposition).getBlockPos();
    			this.inBlock = level.getBlockState(tilePos);
    			Vector3d m = movingobjectposition.getLocation().subtract(position());
    			setDeltaMovement(m);
				Vector3d vector3d1 = m.normalize().scale(0.05F);
				this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
	            this.inGround = true;
	            this.arrowShake = 7;
	            
	            if (!this.hasGravity()) {
            		this.entityData.set(Gravity, true);
            	}
	            
	            if (this.inBlock != null) {
					inBlock.entityInside(this.level, this.tilePos, this);
	            }
    		}
    		else
    		{
	            if (this.isBlock())
	    		{
	            	this.level.levelEvent(null, 2001, blockPosition(), Block.getId(((BlockItem)getItem()).getBlock().defaultBlockState()));
	    		}
	            else
	    		{
			        for (int i = 0; i < 8; ++i){
	                    level.addParticle(new ItemParticleData(ParticleTypes.ITEM, getItemDisplay()), getX(), getY(), getZ(), random.nextGaussian() * 0.15, random.nextGaussian() * 0.2, random.nextGaussian() * 0.15);
			        }
	    		}
        	}
        }
    	
    	
    	if (this.explosiveRadius > 0){
			boolean terraindamage = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && explosiveDamage;
	        level.explode(getOwner() == null?this:getOwner(), getX(), getY(), getZ(), explosiveRadius, this.effect == PotionEffectType.FIRE, terraindamage ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);

            if(this.effect != PotionEffectType.NONE){
				AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(explosiveRadius * 2, explosiveRadius * 2, explosiveRadius * 2);
				List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
				Effect p = PotionEffectType.getMCType(effect);
				for(LivingEntity entity : list1){
	            	if (this.effect != PotionEffectType.FIRE){
						entity.addEffect(new EffectInstance(p, this.duration * 20, this.amplify));
	            	}
	            	else{
	            		entity.setRemainingFireTicks(duration * 20);
	            	}
				}
				this.level.levelEvent(null, 2002, blockPosition(), this.getPotionColor(this.effect));
            }

			this.remove();
    	} 

        if (!this.level.isClientSide && !this.isArrow() && !this.sticksToWalls()){
            this.remove();
        }
    }
    
    private void blockParticles(){
    	
    }
    
    public void addAdditionalSaveData(CompoundNBT par1CompoundNBT)
    {
        par1CompoundNBT.putShort("xTile", (short)this.tilePos.getX());
        par1CompoundNBT.putShort("yTile", (short)this.tilePos.getY());
        par1CompoundNBT.putShort("zTile", (short)this.tilePos.getZ());
		if (this.inBlock != null) {
			par1CompoundNBT.put("inBlockState", NBTUtil.writeBlockState(this.inBlock));
		}
        par1CompoundNBT.putByte("shake", (byte)this.throwableShake);
        par1CompoundNBT.putBoolean("inGround", this.inGround);
        par1CompoundNBT.putBoolean("isArrow", this.isArrow());
        Vector3d m = getDeltaMovement();
        par1CompoundNBT.put("direction", this.newDoubleList(m.x, m.y, m.z));
        par1CompoundNBT.putBoolean("canBePickedUp", canBePickedUp);

        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof PlayerEntity)
        {
            this.throwerName = this.thrower.getUUID().toString();
        }

        par1CompoundNBT.putString("ownerName", this.throwerName == null ? "" : this.throwerName);
        par1CompoundNBT.put("Item", this.getItemDisplay().save(new CompoundNBT()));
        
        par1CompoundNBT.putFloat("damagev2", damage);
		par1CompoundNBT.putInt("punch", punch);
		par1CompoundNBT.putInt("size", this.entityData.get(Size));
		par1CompoundNBT.putInt("velocity", this.entityData.get(Velocity));
		par1CompoundNBT.putInt("explosiveRadius", explosiveRadius);
		par1CompoundNBT.putInt("effectDuration", duration);
		par1CompoundNBT.putBoolean("gravity", this.hasGravity());
		par1CompoundNBT.putBoolean("accelerate", this.accelerate);
		par1CompoundNBT.putBoolean("glows", this.entityData.get(Glows));
		par1CompoundNBT.putInt("PotionEffect", effect);
		par1CompoundNBT.putInt("trailenum", this.entityData.get(Particle));
		par1CompoundNBT.putBoolean("Render3D", this.entityData.get(Is3d));
		par1CompoundNBT.putBoolean("Spins", this.entityData.get(Rotating));
		par1CompoundNBT.putBoolean("Sticks", this.entityData.get(Sticks));
		par1CompoundNBT.putInt("accuracy", accuracy);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundNBT compound)
    {	
        this.tilePos = new BlockPos(compound.getShort("xTile"), compound.getShort("yTile"), compound.getShort("zTile"));
		if (compound.contains("inBlockState", 10)) {
			this.inBlock = NBTUtil.readBlockState(compound.getCompound("inBlockState"));
		}
        this.throwableShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;
        this.entityData.set(Arrow, compound.getBoolean("isArrow"));
        this.throwerName = compound.getString("ownerName");
        this.canBePickedUp = compound.getBoolean("canBePickedUp");
        
        this.damage = compound.getFloat("damagev2");
    	this.punch = compound.getInt("punch");
    	this.explosiveRadius = compound.getInt("explosiveRadius");
    	this.duration = compound.getInt("effectDuration");
    	this.accelerate = compound.getBoolean("accelerate");
    	this.effect = compound.getInt("PotionEffect");
    	this.accuracy = compound.getInt("accuracy");
    	
    	
        this.entityData.set(Particle, compound.getInt("trailenum"));
		this.entityData.set(Size, compound.getInt("size"));
		this.entityData.set(Glows, compound.getBoolean("glows"));
		this.entityData.set(Velocity, compound.getInt("velocity"));
		this.entityData.set(Gravity, compound.getBoolean("gravity"));
		this.entityData.set(Is3d, compound.getBoolean("Render3D"));
		this.entityData.set(Rotating, compound.getBoolean("Spins"));
		this.entityData.set(Sticks, compound.getBoolean("Sticks"));

        if (this.throwerName != null && this.throwerName.length() == 0)
        {
            this.throwerName = null;
        }
        if (compound.contains("direction"))
        {
            ListNBT nbttaglist = compound.getList("direction",6);
            setDeltaMovement(new Vector3d(nbttaglist.getDouble(0), nbttaglist.getDouble(1), nbttaglist.getDouble(2)));
        }
        
        CompoundNBT var2 = compound.getCompound("Item");
        ItemStack item = ItemStack.of(var2);

        if (item.isEmpty())
            this.remove();
        else
        	entityData.set(ItemStackThrown, item);
    }

    @Override
	public Entity getOwner() {
    	if(throwerName == null || throwerName.isEmpty())
    		return null;
		try{
	    	UUID uuid = UUID.fromString(throwerName);
	        if (this.thrower == null && uuid != null)
	            this.thrower = this.level.getPlayerByUUID(uuid);
		}
		catch(IllegalArgumentException ex){
			
		}

        return this.thrower;
    }
	
	private int getPotionColor(int p) {
		switch(p)
		{
		case PotionEffectType.POISON : return 32660;
		case PotionEffectType.HUNGER : return 32660;
		case PotionEffectType.WEAKNESS : return 32696;
		case PotionEffectType.SLOWNESS : return 32698;
		case PotionEffectType.NAUSEA : return 32732;
		case PotionEffectType.BLINDNESS : return 15;
		case PotionEffectType.WITHER : return 32732;
		default : return 0;
		}
	}
	
	public void getStatProperties(DataRanged stats)
	{
		this.damage = stats.getStrength();
		this.punch = stats.getKnockback();
		this.accelerate = stats.getAccelerate();
		this.explosiveRadius = stats.getExplodeSize();
		this.effect = stats.getEffectType();
		this.duration = stats.getEffectTime();
		this.amplify = stats.getEffectStrength();
		this.setParticleEffect(stats.getParticle());
		this.entityData.set(Size, stats.getSize());
		this.entityData.set(Glows, stats.getGlows());
		this.setSpeed(stats.getSpeed());
		this.setHasGravity(stats.getHasGravity());
		setIs3D(stats.getRender3D());
		this.setRotating(stats.getSpins());
		this.setStickInWall(stats.getSticks());
	}
	
	public void setParticleEffect(int type){
		this.entityData.set(Particle, type);
	}
	
	public void setHasGravity(boolean bo){
		this.entityData.set(Gravity, bo);
	}
	
	public void setIs3D(boolean bo){
		this.entityData.set(Is3d, bo);
	}
	
	public void setStickInWall(boolean bo){
		this.entityData.set(Sticks, bo);
	}
	
	public ItemStack getItemDisplay() {
		return entityData.get(ItemStackThrown);
	}
	
	@Override
	public float getBrightness(){
        return this.entityData.get(Glows)? 1.0F : super.getBrightness();
    }

//    @OnlyIn(Dist.CLIENT)
//	@Override
//    public int getBrightnessForRender(){super.get
//        return this.entityData.get(Glows)? 15728880 : super.getBrightnessForRender();
//    }
    
    public boolean hasGravity() {
    	return this.entityData.get(Gravity);
    }
    
    public void setSpeed(int speed) {
    	this.entityData.set(Velocity, speed);
    }
    
    public float getSpeed() {
    	return this.entityData.get(Velocity) / 10.0F;
    }
    
    public boolean isArrow() {
    	return this.entityData.get(Arrow);
    }

	public void setRotating(boolean bo) {
		entityData.set(Rotating, bo);
	}
	
    public boolean isRotating() {
    	return this.entityData.get(Rotating);
    }
    
    public boolean glows() {
    	return this.entityData.get(Glows);
    }
    
    public boolean is3D() {
    	return this.entityData.get(Is3d) || isBlock();
    }
    
    public boolean sticksToWalls() {
    	return this.is3D() && this.entityData.get(Sticks);
    }

    @Override
    public void playerTouch(PlayerEntity par1PlayerEntity){
        if (this.level.isClientSide || !canBePickedUp || !this.inGround || this.arrowShake > 0)
        	return;

        if (par1PlayerEntity.inventory.add(getItemDisplay())){
        	inGround = false;
            this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            par1PlayerEntity.take(this, 1);
            this.remove();
        }
    }

    @Override
    protected boolean isMovementNoisy(){
        return false;
    }

    @Override
    public ITextComponent getDisplayName(){
    	if(!getItemDisplay().isEmpty())
    		return getItemDisplay().getDisplayName();
    	return super.getDisplayName();
    }

	@Override
	public IPacket<?> getAddEntityPacket() {
		Entity entity = this.getOwner();
		return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
	}
    
    public interface IProjectileCallback {
    	boolean onImpact(EntityProjectile entityProjectile, BlockPos pos, Entity entity);
    }
}
