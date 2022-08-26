package noppes.npcs.entity;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noppes.npcs.*;
import noppes.npcs.ai.*;
import noppes.npcs.ai.selector.NPCAttackSelector;
import noppes.npcs.ai.target.EntityAIClearTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtByTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtTarget;
import noppes.npcs.ai.target.NpcNearestAttackableTargetGoal;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.LinkedNpcController.LinkedData;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.data.*;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.*;
import noppes.npcs.roles.*;
import noppes.npcs.util.GameProfileAlt;

import java.util.*;

public abstract class EntityNPCInterface extends CreatureEntity implements IEntityAdditionalSpawnData, IRangedAttackMob{
    public static final DataParameter<Boolean> Attacking = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> Animation = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.INT);
    private static final DataParameter<String> RoleData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.STRING);
    private static final DataParameter<String> JobData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.STRING);
    private static final DataParameter<Integer> FactionData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.INT);
    private static final DataParameter<Boolean> Walking = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Interacting = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IsDead = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);

    public static final GameProfileAlt CommandProfile = new GameProfileAlt();
    public static final GameProfileAlt ChatEventProfile = new GameProfileAlt();
    public static final GameProfileAlt GenericProfile = new GameProfileAlt();
	public static FakePlayer ChatEventPlayer;
	public static FakePlayer CommandPlayer;
	public static FakePlayer GenericPlayer;
	
	public ICustomNpc wrappedNPC;

	public final DataAbilities abilities = new DataAbilities(this);
	public DataDisplay display = new DataDisplay(this);
	public DataStats stats = new DataStats(this);
	public DataInventory inventory = new DataInventory(this);
	public final DataAI ais = new DataAI(this);
	public final DataAdvanced advanced = new DataAdvanced(this);
	public final DataScript script = new DataScript(this);
	public final DataTransform transform = new DataTransform(this);
	public final DataTimers timers = new DataTimers(this);
	
	public CombatHandler combatHandler = new CombatHandler(this);
	
	public String linkedName = "";
	public long linkedLast = 0;
	public LinkedData linkedData;

	public EntitySize baseSize = new EntitySize(0.6f, 1.8f, false);
	private static final EntitySize sizeSleep = new EntitySize(0.8f, 0.4f, false);


	public float scaleX, scaleY, scaleZ;
	private boolean wasKilled = false;
	
	public RoleInterface role = RoleInterface.NONE;
	public JobInterface job = JobInterface.NONE;
	public HashMap<Integer, DialogOption> dialogs;
	public boolean hasDied = false;
	public long killedtime = 0;
	public long totalTicksAlive = 0;
	private int taskCount = 1;
	public int lastInteract = 0;
	public Faction faction; //should only be used server side
	
	private EntityAIRangedAttack aiRange;
	private Goal aiAttackTarget;
	public EntityAILook lookAi;
	public EntityAIAnimation animateAi;
		
	public List<LivingEntity> interactingEntities = new ArrayList<LivingEntity>();

	public ResourceLocation textureLocation = null;
	public ResourceLocation textureGlowLocation = null;
	public ResourceLocation textureCloakLocation = null;
	
	public int currentAnimation = AnimationType.NORMAL;
	public int animationStart = 0;
	
	public int npcVersion = VersionCompatibility.ModRev;
	public IChatMessages messages;
	
	public boolean updateClient = false;
	public boolean updateAI = false;

	public final ServerBossInfo bossInfo;
	public final HashSet<Integer> tracking = new HashSet<Integer>();

	public EntityNPCInterface(EntityType<? extends CreatureEntity> type, World world) {
		super(type, world);
		if(!isClientSide())
			wrappedNPC = new NPCWrapper(this);
		registerBaseAttributes();
		dialogs = new HashMap<Integer, DialogOption>();
		if(!CustomNpcs.DefaultInteractLine.isEmpty())
			advanced.interactLines.lines.put(0, new Line(CustomNpcs.DefaultInteractLine));

		xpReward = 0;
		scaleX = scaleY = scaleZ = 0.9375f;
        
		faction = getFaction();
		setFaction(faction.id);
		updateAI = true;
		bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		bossInfo.setVisible(false);
	}

	@Override
    public boolean canBreatheUnderwater(){
        return ais.movementType == 2;
    }

	@Override
    public boolean isPushedByFluid(){
        return ais.movementType != 2;
    }

    private void registerBaseAttributes(){
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(stats.maxHealth);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CustomNpcs.NpcNavRange);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getSpeed());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(stats.melee.getStrength());
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.getSpeed() * 2);
    }

	public static AttributeModifierMap.MutableAttribute createMobAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED).add(Attributes.FOLLOW_RANGE);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
        this.entityData.define(RoleData, String.valueOf(""));
        this.entityData.define(JobData, String.valueOf(""));
        this.entityData.define(FactionData, 0);
        this.entityData.define(Animation, Integer.valueOf(0));

        this.entityData.define(Walking, false);
        this.entityData.define(Interacting, false);
        this.entityData.define(IsDead, false);
        this.entityData.define(Attacking, false);
	}

    @Override
    public boolean isAlive(){
    	return super.isAlive() && !isKilled();
    }
    
	@Override
	public void tick(){
		super.tick();
		if(tickCount % 10 == 0){
			this.startYPos = calculateStartYPos(ais.startPos()) + 1;
			if(startYPos < 0 && !isClientSide())
				remove();
			EventHooks.onNPCTick(this);
		}
		timers.update();
		
		if(level.isClientSide) {
			if(wasKilled != isKilled() && wasKilled){
				this.deathTime = 0;
				refreshDimensions();
			}
		}
		wasKilled = isKilled();


        if(currentAnimation == AnimationType.DEATH){
    		deathTime = 19;
        }
	}

    @Override
    public boolean doHurtTarget(Entity par1Entity){
        //float f = (float)this.getAttribute(Attributes.attackDamage).getValue();
    	float f = stats.melee.getStrength();
    	if (stats.melee.getDelay() < 10){
        	par1Entity.invulnerableTime = 0;
        }
    	if(par1Entity instanceof LivingEntity){
    		NpcEvent.MeleeAttackEvent event = new NpcEvent.MeleeAttackEvent(wrappedNPC, (LivingEntity)par1Entity, f);
    		if(EventHooks.onNPCAttacksMelee(this, event))
    			return false;
			f = event.damage;
    	}
    	
        boolean var4 = par1Entity.hurt(new NpcDamageSource("mob", this), f);

        if (var4){
        	if(getOwner() instanceof PlayerEntity)
        		EntityUtil.setRecentlyHit((LivingEntity)par1Entity);
            if (stats.melee.getKnockback() > 0){
                par1Entity.push((-MathHelper.sin(this.yRot* (float)Math.PI / 180.0F) * stats.melee.getKnockback() * 0.5F), 0.1D, (MathHelper.cos(this.yRot * (float)Math.PI / 180.0F) * stats.melee.getKnockback() * 0.5F));
				Vector3d motion = this.getDeltaMovement();
                setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1, 0.6));
            }
            if(role.getType() == RoleType.COMPANION){
            	((RoleCompanion)role).attackedEntity(par1Entity);
            }
        }

        if (stats.melee.getEffectType() != PotionEffectType.NONE){
        	if (stats.melee.getEffectType() != PotionEffectType.FIRE)
        		((LivingEntity)par1Entity).addEffect(new EffectInstance(PotionEffectType.getMCType(stats.melee.getEffectType()), stats.melee.getEffectTime() * 20, stats.melee.getEffectStrength()));
        	else
        		par1Entity.setRemainingFireTicks(stats.melee.getEffectTime() * 20);
        }
        return var4;
    }

    @Override
    public void aiStep(){
    	if(CustomNpcs.FreezeNPCs)
    		return;
    	if(this.isNoAi()) {
            super.aiStep();
            return;
    	}
    	totalTicksAlive++;
        this.updateSwingTime();
        if(this.tickCount % 20 == 0)
			faction = getFaction();
		if(!level.isClientSide){
	    	if(!isKilled() && this.tickCount % 20 == 0){
				advanced.scenes.update();
	    		if(this.getHealth() < this.getMaxHealth()){
	    			if(stats.healthRegen > 0 && !isAttacking())
	    				heal(stats.healthRegen);
	    			if(stats.combatRegen > 0 && isAttacking())
	    				heal(stats.combatRegen);
	    		}
	    		if(faction.getsAttacked && !isAttacking()){
	    			List<MonsterEntity> list = this.level.getEntitiesOfClass(MonsterEntity.class, this.getBoundingBox().inflate(16, 16, 16));
	    			for(MonsterEntity mob : list){
	    				if(mob.getTarget() == null && this.canNpcSee(mob)){
	    					mob.setTarget(this);
	    				}
	    			}
	    		}
	    		if(linkedData != null && linkedData.time > linkedLast){
	    			LinkedNpcController.Instance.loadNpcData(this);
	    		}
	    		if(updateClient){
	    			updateClient();
	    		}
	    		if(updateAI){
	    			updateTasks();
	    			updateAI = false;
	    		}
	    	}
			if(getHealth() <= 0 && !isKilled()){
				removeAllEffects();
				entityData.set(IsDead, true);
    			updateTasks();
    			refreshDimensions();
			}
			if(display.getBossbar() == 2)
				bossInfo.setVisible(this.getTarget() != null);
			entityData.set(Walking, !getNavigation().isDone());
			entityData.set(Interacting, isInteracting());
			
			combatHandler.update();
			onCollide();
		}
		
		if(wasKilled != isKilled() && wasKilled){
			reset();
		}
		
		if (this.level.isDay() && !this.level.isClientSide && this.stats.burnInSun){
            float f = this.getBrightness();

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.level.canSeeSky(this.blockPosition())){
                this.setRemainingFireTicks(8 * 20);
            }
        }
		
        super.aiStep();
        
        if (level.isClientSide){
			role.clientUpdate();
        	
        	if(textureCloakLocation != null)
        		cloakUpdate();
			if(currentAnimation != entityData.get(Animation)){
				currentAnimation = entityData.get(Animation);
				animationStart = this.tickCount;
				refreshDimensions();
			}
			if(job.getType() == JobType.BARD)
				((JobBard)job).aiStep();
			
        }
        
        if(display.getBossbar() > 0)
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
    
    public void updateClient() {
		Packets.sendNearby(this, new PacketNpcUpdate(getId(), writeSpawnData()));
		updateClient = false;
    }

	@Override //processInteract
	protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		if(level.isClientSide)
			return isAttacking() ? ActionResultType.SUCCESS : ActionResultType.FAIL;

		if(hand != Hand.MAIN_HAND)
			return ActionResultType.PASS;
		ItemStack stack = player.getItemInHand(hand);
		if (stack != null) {
			Item item = stack.getItem();
			if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
				setTarget(null);
				setLastHurtByMob(null);
				return ActionResultType.SUCCESS;
			}
			if (item == CustomItems.moving) {
				setTarget(null);
				stack.addTagElement("NPCID", IntNBT.valueOf(getId()));
				player.sendMessage(new TranslationTextComponent("message.pather.register", this.getName()), getUUID());
				return ActionResultType.SUCCESS;
			}
		}
		if(EventHooks.onNPCInteract(this, player))
			return ActionResultType.FAIL;

		if(getFaction().isAggressiveToPlayer(player))
			return ActionResultType.FAIL;

		addInteract(player);

		Dialog dialog = getDialog(player);
		QuestData data = PlayerData.get(player).questData.getQuestCompletion(player, this);
		if (data != null){
			Packets.send((ServerPlayerEntity)player, new PacketQuestCompletion(data.quest.id));
		}
		else if (dialog != null){
			NoppesUtilServer.openDialog(player, this, dialog);
		}
		else if(role.getType() != RoleType.NONE)
			role.interact(player);
		else
			say(player, advanced.getInteractLine());

		return ActionResultType.PASS;
	}
	
	public void addInteract(LivingEntity entity){
		if( !ais.stopAndInteract || isAttacking() || !entity.isAlive() || this.isNoAi())
			return;
		if((tickCount - lastInteract)  < 180)
			interactingEntities.clear();
		getNavigation().stop();
		lastInteract = tickCount;
		if(!interactingEntities.contains(entity))
			interactingEntities.add(entity);
	}
	
	public boolean isInteracting(){
		if((tickCount - lastInteract) < 40 || isClientSide() && entityData.get(Interacting))
			return true;
		return ais.stopAndInteract && !interactingEntities.isEmpty() && (tickCount - lastInteract)  < 180;
	}

	private Dialog getDialog(PlayerEntity player) {
		for (DialogOption option : dialogs.values()) {
			if (option == null)
				continue;
			if (!option.hasDialog())
				continue;
			Dialog dialog = option.getDialog();
			if (dialog.availability.isAvailable(player)){
				return dialog;
			}
		}
		return null;
	}

	@Override
	public boolean hurt(DamageSource damagesource, float i) {
        if (this.level.isClientSide || CustomNpcs.FreezeNPCs || damagesource.msgId.equals("inWall")){
            return false;
        }
        if(damagesource.msgId.equals("outOfWorld") && isKilled()){
        	reset();
        }
        i = stats.resistances.applyResistance(damagesource, i);
        
        if((float)this.invulnerableTime > (float)this.invulnerableDuration / 2.0F && i <= this.lastHurt)
        	return false;
        
		Entity entity = NoppesUtilServer.GetDamageSourcee(damagesource);
		LivingEntity attackingEntity = null;
		
		if (entity instanceof LivingEntity)
			attackingEntity = (LivingEntity) entity;
		
		if(attackingEntity != null && attackingEntity == getOwner())
			return false;
		else if (attackingEntity instanceof EntityNPCInterface){
			EntityNPCInterface npc = (EntityNPCInterface) attackingEntity;
			if(npc.faction.id == faction.id)
				return false;
			if(npc.getOwner() instanceof PlayerEntity)
				this.hurtTime = 100;
		}
		else if (attackingEntity instanceof PlayerEntity && faction.isFriendlyToPlayer((PlayerEntity) attackingEntity)) {
			net.minecraftforge.common.ForgeHooks.onLivingAttack(this, damagesource, i);
			return false;
		}
		
		NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(wrappedNPC, entity, i, damagesource);
		if(EventHooks.onNPCDamaged(this, event)) {
			net.minecraftforge.common.ForgeHooks.onLivingAttack(this, damagesource, i);
			return false;
		}
		i = event.damage;

		if(isKilled())
			return false;
		
		if(attackingEntity == null)
			return super.hurt(damagesource, i);
		
		try{
			if (isAttacking()){
				if(getTarget() != null && this.distanceToSqr(getTarget()) > this.distanceToSqr(attackingEntity)){
					setTarget(attackingEntity);
				}
				return super.hurt(damagesource, i);
			}
			
			if (i > 0) {
				List<EntityNPCInterface> inRange = level.getEntitiesOfClass(EntityNPCInterface.class, this.getBoundingBox().inflate(32D, 16D, 32D));
				for (EntityNPCInterface npc : inRange) {
					if (npc.isKilled() || !npc.advanced.defendFaction || npc.faction.id != faction.id)
						continue;
					
					if (npc.canNpcSee(this) || npc.ais.directLOS || npc.canNpcSee(attackingEntity))
						npc.onAttack(attackingEntity);
				}
				setTarget(attackingEntity);
			}
			return super.hurt(damagesource, i);
		}
		finally{
			if(event.clearTarget){
				setTarget(null);
				setLastHurtByMob(null);
			}
		}
	}

	@Override
	protected void actuallyHurt(DamageSource damageSrc, float damageAmount){
		super.actuallyHurt(damageSrc, damageAmount);
		combatHandler.damage(damageSrc, damageAmount);
	}

	public void onAttack(LivingEntity entity) {
		if (entity == null || entity == this || isAttacking() || ais.onAttack == 3 || entity == getOwner())
			return;
		super.setTarget(entity);
	}
	
	@Override
    public void setTarget(LivingEntity entity){
    	if(entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable ||
    			entity != null && entity == getOwner() || getTarget() == entity)
    		return;
    	if(entity != null){
    		NpcEvent.TargetEvent event = new NpcEvent.TargetEvent(wrappedNPC, entity);
    		if(EventHooks.onNPCTarget(this, event))
    			return;
    		if(event.entity == null)
    			entity = null;
    		else
    			entity = event.entity.getMCEntity();
    	}
    	else{
    		for(PrioritizedGoal en : targetSelector.availableGoals){
    			en.stop();
    		}
    		if(EventHooks.onNPCTargetLost(this, getTarget()))
    			return;
    	}
    	
		if (entity != null && entity != this && ais.onAttack != 3 && !isAttacking() && !isClientSide()){
			Line line = advanced.getAttackLine();
			if(line != null)
				saySurrounding(Line.formatTarget(line, entity));
		}
		
		super.setTarget(entity);
    }

	@Override
	public void performRangedAttack(LivingEntity entity, float f) {
        final ItemStack proj = ItemStackWrapper.MCItem(inventory.getProjectile());
        if(proj == null){
    		updateAI = true;
        	return;
        }
        
        NpcEvent.RangedLaunchedEvent event = new NpcEvent.RangedLaunchedEvent(wrappedNPC, entity, stats.ranged.getStrength());        
		for(int i = 0; i < this.stats.ranged.getShotCount(); i++){
			EntityProjectile projectile = shoot(entity, stats.ranged.getAccuracy(), proj, f == 1);
			projectile.damage = event.damage;
            projectile.callback = (projectile1, pos, entity1) -> {
                if (proj.getItem() == CustomItems.soulstoneFull) {
                    Entity e = ItemSoulstoneFilled.Spawn(null, proj, EntityNPCInterface.this.level, pos);
                    if (e instanceof LivingEntity && entity1 instanceof LivingEntity) {
                        if (e instanceof MobEntity)
                            ((MobEntity) e).setTarget((LivingEntity) entity1);
                        else
                            ((LivingEntity) e).setLastHurtByMob((LivingEntity) entity1);
                    }
                }
                projectile1.playSound(stats.ranged.getSoundEvent(entity1 != null ? 1 : 2), 1.0F, 1.2F / (getRandom().nextFloat() * 0.2F + 0.9F));
                return false;
            };
            this.playSound(this.stats.ranged.getSoundEvent(0), 2.0F, 1.0f);
			event.projectiles.add((IProjectile) NpcAPI.Instance().getIEntity(projectile));
		}
        EventHooks.onNPCRangedLaunched(this, event);
    }
	
	public EntityProjectile shoot(LivingEntity entity, int accuracy, ItemStack proj, boolean indirect){
		return shoot(entity.getX(), entity.getBoundingBox().minY + (double)(entity.getBbHeight() / 2.0F), entity.getZ(), accuracy, proj, indirect);
	}
	
	public EntityProjectile shoot(double x, double y, double z, int accuracy, ItemStack proj, boolean indirect){
        EntityProjectile projectile = new EntityProjectile(this.level, this, proj.copy(), true);
        double varX = x - this.getX();
		double varY = y - (this.getY() + this.getEyeHeight());
		double varZ = z - this.getZ();
		float varF = projectile.hasGravity() ? MathHelper.sqrt(varX * varX + varZ * varZ) : 0.0F;
		float angle = projectile.getAngleForXYZ(varX, varY, varZ, varF, indirect);
		float acc = 20.0F - MathHelper.floor(accuracy / 5.0F);
        projectile.shoot(varX, varY, varZ, angle, acc);
        level.addFreshEntity(projectile);
        return projectile;
	}
	
	private void clearTasks(GoalSelector tasks){
        //Iterator iterator = tasks.availableGoals.iterator();
        List<PrioritizedGoal> list = new ArrayList(tasks.availableGoals);
        for (PrioritizedGoal entityaitaskentry : list)
        {
            tasks.removeGoal(entityaitaskentry);
        }
		tasks.availableGoals.clear();
		tasks.lockedFlags.clear();
		tasks.disabledFlags.clear();
	}
	
	private void updateTasks() {
		if (level == null || level.isClientSide || !(level instanceof ServerWorld))
			return;
		ServerWorld sWorld = (ServerWorld) level;
		clearTasks(this.goalSelector);
		clearTasks(this.targetSelector);
		if(isKilled())
			return;

		this.targetSelector.addGoal(0, new EntityAIClearTarget(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NpcNearestAttackableTargetGoal<>(this, LivingEntity.class, 4, this.ais.directLOS, false, new NPCAttackSelector(this)));
        this.targetSelector.addGoal(3, new EntityAIOwnerHurtByTarget(this));
        this.targetSelector.addGoal(4, new EntityAIOwnerHurtTarget(this));

		sWorld.navigations.remove(this.getNavigation());
		if(ais.movementType == 1){
	        this.moveControl = new FlyingMoveHelper(this);
	        this.navigation = new FlyingPathNavigator(this, level);
		}
		else if(ais.movementType == 2){
	        this.moveControl = new FlyingMoveHelper(this);
	        this.navigation = new SwimmerPathNavigator(this, level);
		}
		else{
	        this.moveControl = new MovementController(this);
	        this.navigation = new GroundPathNavigator(this, level);
			this.goalSelector.addGoal(0, new EntityAIWaterNav(this));
		}
		sWorld.navigations.add(this.getNavigation());
		
		this.taskCount = 1;
		this.addRegularEntries();
		this.doorInteractType();
		this.seekShelter();
		this.setResponse();
		this.setMoveType();
	}
	
	private void setResponse(){	
		aiAttackTarget = aiRange = null;
		if (this.ais.canSprint)
			this.goalSelector.addGoal(this.taskCount++, new EntityAISprintToTarget(this));
		
        if (this.ais.onAttack == 1){ 
        	this.goalSelector.addGoal(this.taskCount++, new EntityAIPanic(this, 1.2F));
        }
        else if (this.ais.onAttack == 2)  {
        	this.goalSelector.addGoal(this.taskCount++, new EntityAIAvoidTarget(this));
        }        
        else if (this.ais.onAttack == 0) {
    		if (this.ais.canLeap)
    			this.goalSelector.addGoal(this.taskCount++, new EntityAIPounceTarget(this));
    		

        	this.goalSelector.addGoal(this.taskCount, aiAttackTarget = new EntityAIAttackTarget(this));
        	
        	if(this.inventory.getProjectile() != null){
        		this.goalSelector.addGoal(this.taskCount++, aiRange = new EntityAIRangedAttack(this));
        	}
        }
        else if (this.ais.onAttack == 3) {
        	//do nothing
        }
    }

	public boolean canFly(){
		return this.navigation instanceof FlyingPathNavigator;
	}

	/*
	 * Branch task function for setting if an NPC wanders or not
	 */
	public void setMoveType(){	
		if (ais.getMovingType() == 1){
			this.goalSelector.addGoal(this.taskCount++, new EntityAIWander(this));
		}
		if (ais.getMovingType() == 2){
			this.goalSelector.addGoal(this.taskCount++, new EntityAIMovingPath(this));
		}
	}
	
	public void doorInteractType(){
		if(navigation instanceof GroundPathNavigator){//currently opening doors is only supported by ground navigation
			Goal aiDoor = null;
			if (this.ais.doorInteract == 1) {
				this.goalSelector.addGoal(this.taskCount++, aiDoor = new OpenDoorGoal(this, true));
			}
			else if (this.ais.doorInteract == 0)
			{
				this.goalSelector.addGoal(this.taskCount++, aiDoor = new EntityAIBustDoor(this));
			}
			((GroundPathNavigator)navigation).setCanOpenDoors(aiDoor != null);
		}
	}
	
	/*
	 * Branch task function for finding shelter under the appropriate conditions
	 */
	public void seekShelter() {
		if (this.ais.findShelter == 0)
		{
			this.goalSelector.addGoal(this.taskCount++, new EntityAIMoveIndoors(this));
		}
		else if (this.ais.findShelter == 1)
		{
			if(!canFly())//doesnt work when flying
				this.goalSelector.addGoal(this.taskCount++, new RestrictSunGoal(this));
			this.goalSelector.addGoal(this.taskCount++, new EntityAIFindShade(this));
		}
	}
			
	/*
	 * Add immutable task entries.
	 */
	public void addRegularEntries() {
		this.goalSelector.addGoal(this.taskCount++, new EntityAIReturn(this));
		this.goalSelector.addGoal(this.taskCount++, new EntityAIFollow(this));
		if (this.ais.getStandingType() != 1 && this.ais.getStandingType() != 3)
			this.goalSelector.addGoal(this.taskCount++, new EntityAIWatchClosest(this, LivingEntity.class, 5.0F));
		this.goalSelector.addGoal(this.taskCount++, lookAi = new EntityAILook(this));
		this.goalSelector.addGoal(this.taskCount++, new EntityAIWorldLines(this));
		this.goalSelector.addGoal(this.taskCount++, new EntityAIJob(this));
		this.goalSelector.addGoal(this.taskCount++, new EntityAIRole(this));
		this.goalSelector.addGoal(this.taskCount++, animateAi = new EntityAIAnimation(this));
		if(transform.isValid())
			this.goalSelector.addGoal(this.taskCount++, new EntityAITransform(this));
	}
	
	public float getSpeed() {
		return (float)ais.getWalkingSpeed() / 20.0F;
	}

    @Override
	public float getWalkTargetValue(BlockPos pos){
    	if(ais.movementType == 2) {
            return this.level.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F : 0;
    	}
		float weight = this.level.getLightEmission(pos) - 0.5F;
    	if(level.getBlockState(pos).isSolidRender(level, pos))
    		weight += 10;
    	return weight;
    }

    @Override
	protected int decreaseAirSupply(int par1){
		if (!this.stats.canDrown)
			return par1;
        return super.decreaseAirSupply(par1);
    }

    @Override
	public CreatureAttribute getMobType(){
        return this.stats == null ? null : this.stats.creatureType;
    }

	@Override
    public int getAmbientSoundInterval(){
        return 160;
    }

	@Override
    public void playAmbientSound(){
		if (!this.isAlive())
			return;
        this.advanced.playSound(this.getTarget() != null ? 1 : 0, getSoundVolume(), getVoicePitch());
    }
    
    @Override
    protected void playHurtSound(DamageSource source){
        this.advanced.playSound(2, getSoundVolume(), getVoicePitch());
    }

    @Override
    public SoundEvent getDeathSound(){
        return null;
    }
	
	@Override
    protected float getVoicePitch(){
		if(this.advanced.disablePitch)
			return 1;
    	return super.getVoicePitch();
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState state){
    	if (this.advanced.getSound(4) != null) {
    		this.advanced.playSound(4, 0.15F, 1.0F);
    	}
    	else {
    		super.playStepSound(pos, state);
    	}
    }
    
    public ServerPlayerEntity getFakeChatPlayer(){
    	if(level.isClientSide)
    		return null;
		EntityUtil.Copy(this, ChatEventPlayer);
		ChatEventProfile.npc = this;
		ChatEventPlayer.setLevel(level);
		ChatEventPlayer.setPos(getX(), getY(), getZ());
		return ChatEventPlayer;
    }

	public void saySurrounding(Line line) {
		if (line == null)
			return;
		if(line.getShowText() && !line.getText().isEmpty()){
			ServerChatEvent event = new ServerChatEvent(getFakeChatPlayer(), line.getText(), new TranslationTextComponent(line.getText().replace("%", "%%")));
	        if (CustomNpcs.NpcSpeachTriggersChatEvent && (MinecraftForge.EVENT_BUS.post(event) || event.getComponent() == null)){
	            return;
	        }
			line.setText(event.getComponent().getString().replace("%%", "%"));
		}
		
		List<PlayerEntity> inRange = level.getEntitiesOfClass(
				PlayerEntity.class, this.getBoundingBox().inflate(20D, 20D, 20D));
		for (PlayerEntity player : inRange)
			say(player, line);
	}

	public void say(PlayerEntity player, Line line) {
		if (line == null || !this.canNpcSee(player))
			return;		
		
		if(!line.getSound().isEmpty()){
			BlockPos pos = this.blockPosition();
			Packets.send((ServerPlayerEntity)player, new PacketPlaySound(line.getSound(), pos, this.getSoundVolume(), this.getVoicePitch()));
		}
		if(!line.getText().isEmpty()) {
			Packets.send((ServerPlayerEntity)player, new PacketChatBubble(this.getId(), new TranslationTextComponent(line.getText()), line.getShowText()));
		}
	}
	
	@Override
    public boolean shouldShowName(){
    	return true;
    }

	@Override
	public void push(double d, double d1, double d2) {
		if (isWalking() && !isKilled())
			super.push(d, d1, d2);
	}


	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		npcVersion = compound.getInt("ModRev");
		VersionCompatibility.CheckNpcCompatibility(this, compound);
		
		display.readToNBT(compound);
		stats.readToNBT(compound);
		ais.readToNBT(compound);
		script.load(compound);
		timers.load(compound);
		
		advanced.readToNBT(compound);
		role.load(compound);
		job.load(compound);
        
		inventory.load(compound);
		transform.readToNBT(compound);
		
		killedtime = compound.getLong("KilledTime");	
		totalTicksAlive = compound.getLong("TotalTicksAlive");
		
		linkedName = compound.getString("LinkedNpcName");
		if(!isClientSide())
			LinkedNpcController.Instance.loadNpcData(this);
		
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(CustomNpcs.NpcNavRange);

		updateAI = true;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		display.save(compound);
		stats.save(compound);
		ais.save(compound);
		script.save(compound);
		timers.save(compound);
		
		advanced.save(compound);
		role.save(compound);
		job.save(compound);
        
		inventory.save(compound);
		transform.save(compound);

		compound.putLong("KilledTime", killedtime);
		compound.putLong("TotalTicksAlive", totalTicksAlive);
		compound.putInt("ModRev", npcVersion);
		compound.putString("LinkedNpcName", linkedName);
	}

	@Override
	public EntitySize getDimensions(Pose poseIn) {
		EntitySize size = baseSize;
		if(currentAnimation == AnimationType.SLEEP || currentAnimation == AnimationType.CRAWL || deathTime > 0){
			size = sizeSleep;
		}
		else if (isPassenger() || currentAnimation == AnimationType.SIT){
			size = baseSize.scale(1, 0.77f);
		}

		size = size.scale(display.getSize() * 0.2f);

		if(display.getHitboxState() == 1 || isKilled() && stats.hideKilledBody) {
			size = size.scalable(0.00001f, size.height);
		}
		if(size.width / 2 > level.getMaxEntityRadius()) {
			level.increaseMaxEntityRadius(size.width / 2);
		}
		return size;
	}

	@Override
	public void tickDeath(){
		if(stats.spawnCycle == 3 || stats.spawnCycle == 4){
			super.tickDeath();
			return;
		}
		
		++this.deathTime;
		if(level.isClientSide)
			return;
		if(!hasDied){
			remove();
		}
		if (killedtime < System.currentTimeMillis()) {
			if (stats.spawnCycle == 0 || (this.level.isDay() && stats.spawnCycle == 1) || (!this.level.isDay() && stats.spawnCycle == 2)) {
				reset();
			}
		}
	}
	
	public void reset() {
		hasDied = false;
		removed = false;
		dead = false;
		revive();
		wasKilled = false;
		setSprinting(false);
		setHealth(getMaxHealth());
		entityData.set(Animation, 0);
		entityData.set(Walking, false);
		entityData.set(IsDead, false);
		entityData.set(Interacting, false);
		interactingEntities.clear();
		
		combatHandler.reset();
		this.setTarget(null);
		this.setLastHurtByMob(null);

		this.deathTime = 0;
		if(ais.returnToStart && !hasOwner() && !isClientSide() && !isPassenger()) {
			moveTo(getStartXPos(), getStartYPos(), getStartZPos(), yRot, xRot);
		}
		killedtime = 0;
		clearFire();
		this.removeAllEffects();
		travel(Vector3d.ZERO);
		this.walkDistO = this.walkDist = 0;
		getNavigation().stop();
		currentAnimation = AnimationType.NORMAL;
		refreshDimensions();
		updateAI = true;
		ais.movingPos = 0;
		if(getOwner() != null){
			getOwner().setLastHurtMob(null);
		}
		bossInfo.setVisible(display.getBossbar() == 1);

		job.reset();
		
		EventHooks.onNPCInit(this);
	}

    public void onCollide() {	
    	if(!isAlive() || tickCount % 4 != 0 || level.isClientSide)
    		return;
    	
        AxisAlignedBB axisalignedbb = null;

        if (this.getVehicle() != null && this.getVehicle().isAlive()){
            axisalignedbb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
        }
        else{
            axisalignedbb = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
        }

        List list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if(list == null)
        	return;

        for (int i = 0; i < list.size(); ++i){
            Entity entity = (Entity)list.get(i);
            if (entity != this && entity.isAlive())
            	EventHooks.onNPCCollide(this, entity);
        }
        
    }
    
	@Override
    public void handleInsidePortal(BlockPos pos){
		//prevent npcs from walking into portals
	}

	public double prevChasingPosX;
	public double prevChasingPosY;
	public double prevChasingPosZ;
	public double chasingPosX;
	public double chasingPosY;
	public double chasingPosZ;

	public void cloakUpdate() {
		this.prevChasingPosX = this.chasingPosX;
		this.prevChasingPosY = this.chasingPosY;
		this.prevChasingPosZ = this.chasingPosZ;
		double d0 = this.getX() - this.chasingPosX;
		double d1 = this.getY() - this.chasingPosY;
		double d2 = this.getZ() - this.chasingPosZ;
		double d3 = 10.0D;
		if (d0 > 10.0D) {
			this.chasingPosX = this.getX();
			this.prevChasingPosX = this.chasingPosX;
		}

		if (d2 > 10.0D) {
			this.chasingPosZ = this.getZ();
			this.prevChasingPosZ = this.chasingPosZ;
		}

		if (d1 > 10.0D) {
			this.chasingPosY = this.getY();
			this.prevChasingPosY = this.chasingPosY;
		}

		if (d0 < -10.0D) {
			this.chasingPosX = this.getX();
			this.prevChasingPosX = this.chasingPosX;
		}

		if (d2 < -10.0D) {
			this.chasingPosZ = this.getZ();
			this.prevChasingPosZ = this.chasingPosZ;
		}

		if (d1 < -10.0D) {
			this.chasingPosY = this.getY();
			this.prevChasingPosY = this.chasingPosY;
		}

		this.chasingPosX += d0 * 0.25D;
		this.chasingPosZ += d2 * 0.25D;
		this.chasingPosY += d1 * 0.25D;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToPlayer) {
		return stats != null &&  stats.spawnCycle == 4;
	}

	@Override
	public ItemStack getMainHandItem() {
		IItemStack item = null;
		if (isAttacking())
			item = inventory.getRightHand();
		else if(role.getType() == RoleType.COMPANION)
			item = ((RoleCompanion)role).getItemInHand();
		else if (job.overrideMainHand)
			item = job.getMainhand();
		else
			item = inventory.getRightHand();
		
		return ItemStackWrapper.MCItem(item);
	}

	@Override
	public ItemStack getOffhandItem() {
		IItemStack item = null;
		if (isAttacking())
			item = inventory.getLeftHand();
		else if (job.overrideOffHand)
			item = job.getOffhand();
		else
			item = inventory.getLeftHand();
		return ItemStackWrapper.MCItem(item);
	}

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slot){
    	if(slot == EquipmentSlotType.MAINHAND)
    		return getMainHandItem();
    	if(slot == EquipmentSlotType.OFFHAND)
    		return getOffhandItem();
        return ItemStackWrapper.MCItem(inventory.getArmor(3 - slot.getIndex()));
    }

    @Override
    public void setItemSlot(EquipmentSlotType slot, ItemStack item){
    	if(slot == EquipmentSlotType.MAINHAND)
    		inventory.weapons.put(0, NpcAPI.Instance().getIItemStack(item));
    	else if(slot == EquipmentSlotType.OFFHAND)
    		inventory.weapons.put(2, NpcAPI.Instance().getIItemStack(item));
    	else{
    		inventory.armor.put(3 - slot.getIndex(), NpcAPI.Instance().getIItemStack(item));
    	}
    }

    @Override
    public Iterable<ItemStack> getArmorSlots(){
    	ArrayList<ItemStack> list = new ArrayList<ItemStack>();
    	for(int i = 0; i < 4; i++){
    		list.add(ItemStackWrapper.MCItem(inventory.armor.get(3 - i)));
    	}
    	return list;
    }

    @Override
    public Iterable<ItemStack> getAllSlots(){
    	ArrayList list = new ArrayList();
    	list.add(ItemStackWrapper.MCItem(inventory.weapons.get(0)));
    	list.add(ItemStackWrapper.MCItem(inventory.weapons.get(2)));
        return list;
    }

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {

	}

	@Override
	protected void dropFromLootTable(DamageSource damageSourceIn, boolean attackedRecently) {

	}

	@Override
	public void die(DamageSource damagesource){
		setSprinting(false);
		getNavigation().stop();
		clearFire();
		removeAllEffects();
		
		if(!isClientSide()){
			this.advanced.playSound(3, this.getSoundVolume(), this.getVoicePitch());
			Entity attackingEntity = NoppesUtilServer.GetDamageSourcee(damagesource);
			NpcEvent.DiedEvent event = new NpcEvent.DiedEvent(this.wrappedNPC, damagesource, attackingEntity);
			event.droppedItems = inventory.getItemsRNG();
			event.expDropped = inventory.getExpRNG();
			event.line = advanced.getKilledLine();
			EventHooks.onNPCDied(this, event);
			
			bossInfo.setVisible(false);
			inventory.dropStuff(event, attackingEntity, damagesource);
			if(event.line != null)
				saySurrounding(Line.formatTarget((Line)event.line, attackingEntity instanceof LivingEntity?(LivingEntity)attackingEntity:null));
		}
		super.die(damagesource);
	}

	@Override
    public void startSeenByPlayer(ServerPlayerEntity player){
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

	@Override
    public void stopSeenByPlayer(ServerPlayerEntity player){
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }
	
	@Override
	public void remove() {
		hasDied = true;

		ejectPassengers();
		stopRiding();
		
		if(level.isClientSide || stats.spawnCycle == 3 || stats.spawnCycle == 4){
			//this.spawnExplosionParticle();
			delete();
		}
		else {			
			setHealth(-1);
			setSprinting(false);
			getNavigation().stop();

	    	setCurrentAnimation(AnimationType.SLEEP);
	    	refreshDimensions();
	    	
			if(killedtime <= 0)
				killedtime = stats.respawnTime * 1000 + System.currentTimeMillis();

			role.killed();
			job.killed();
		}
	}

	public void delete() {
		role.delete();
		job.delete();
		super.remove();
	}
	
	public float getStartXPos(){
		return ais.startPos().getX() + ais.bodyOffsetX / 10;
	}
	
	public float getStartZPos(){
		return ais.startPos().getZ() + ais.bodyOffsetZ / 10;
	}

	public boolean isVeryNearAssignedPlace() {
		double xx = getX() - getStartXPos();
		double zz = getZ() - getStartZPos();
		if (xx < -0.2 || xx > 0.2)
			return false;
        return !(zz < -0.2) && !(zz > 0.2);
    }

//	@Override
//	public IIcon getItemIcon(ItemStack par1ItemStack, int limbSwingAmount){
//        if (par1ItemStack.getItem() == Items.bow){
//            return Items.bow.getIcon(par1ItemStack, limbSwingAmount);
//        }
//		PlayerEntity player = CustomNpcs.proxy.getPlayer();
//		if(player == null)
//			return super.getItemIcon(par1ItemStack, limbSwingAmount);
//		return player.getItemIcon(par1ItemStack, limbSwingAmount);
//    }
	private double startYPos = -1;
	public double getStartYPos() {
		if(startYPos < 0)
			return calculateStartYPos(ais.startPos());
		return startYPos;
	}
	private double calculateStartYPos(BlockPos pos) {
		BlockPos startPos = ais.startPos();
		while(pos.getY() > 0){
			BlockState state = level.getBlockState(pos);
			VoxelShape shape = state.getShape(level, pos);
			if(shape.isEmpty()){
				pos = pos.below();
				continue;
			}
			AxisAlignedBB bb = shape.bounds().move(pos);
			if(ais.movementType == 2 && startPos.getY() <= pos.getY() && state.getMaterial() == Material.WATER) {
				pos = pos.below();
				continue;
			}
			return bb.maxY;
		}
		return 0;
	}
	
	private BlockPos calculateTopPos(BlockPos pos) {
		BlockPos check = pos;
		while(check.getY() > 0){
			BlockState state = level.getBlockState(pos);
			VoxelShape shape = state.getShape(level, pos);
			if(!shape.isEmpty()){
				AxisAlignedBB bb = shape.bounds().move(pos);
				if (bb != null){
					return check;
				}
			}
			check = check.below();
		}
		return pos;
	}
	
	public boolean isInRange(Entity entity, double range){
		return this.isInRange(entity.getX(), entity.getY(), entity.getZ(), range);
	}
	
	public boolean isInRange(double posX, double posY, double posZ, double range){
		double y = Math.abs(this.getY() - posY);
		if(posY >= 0 && y > range)
			return false;
		
		double x = Math.abs(this.getX() - posX);
		double z = Math.abs(this.getZ() - posZ);
		
		return x <= range && z <= range;
	}

	public void givePlayerItem(PlayerEntity player, ItemStack item) {
		if (level.isClientSide) {
			return;
		}
		item = item.copy();
		float f = 0.7F;
		double d = (double) (level.random.nextFloat() * f)
				+ (double) (1.0F - f);
		double d1 = (double) (level.random.nextFloat() * f)
				+ (double) (1.0F - f);
		double d2 = (double) (level.random.nextFloat() * f)
				+ (double) (1.0F - f);
		ItemEntity entityitem = new ItemEntity(level, getX() + d, getY() + d1,
				getZ() + d2, item);
		entityitem.setPickUpDelay(2);
		level.addFreshEntity(entityitem);

		int i = item.getCount();

		if (player.inventory.add(item)) {
            this.level.playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			player.take(entityitem, i);

			if (item.getCount() <= 0) {
				entityitem.remove();
			}
		}
	}
	
	@Override
	public boolean isSleeping() {
		return currentAnimation == AnimationType.SLEEP && !isAttacking();
	}
//
//	@Override
//	public boolean isPassenger() {
//		return currentAnimation == AnimationType.SITTING && !isAttacking() || getVehicle() != null;
//	}

	public boolean isWalking() {
		return ais.getMovingType() != 0 || isAttacking() || isFollower() || entityData.get(Walking);
	}

	@Override
	public boolean isCrouching() {
		return currentAnimation == AnimationType.SNEAK;
	}

	@Override
    public void knockback(float strength, double ratioX, double ratioZ){
		super.knockback(strength * (2 - stats.resistances.knockback), ratioX, ratioZ);
    }
    
	public Faction getFaction() {
		Faction fac = FactionController.instance.getFaction(entityData.get(FactionData));
		if (fac == null) {
			return FactionController.instance.getFaction(FactionController.instance.getFirstFactionId());
		}
		return fac;
	}
	
	public boolean isClientSide(){
		return level == null || level.isClientSide;
	}
	
	public void setFaction(int id) {
		if(id < 0|| isClientSide())
			return;
		entityData.set(FactionData, id);
	}
	
	@Override
	public boolean canBeAffected(EffectInstance effect){
		if(stats.potionImmune)
			return false;
		if(getMobType() == CreatureAttribute.ARTHROPOD && effect.getEffect() == Effects.POISON)
			return false;
        return super.canBeAffected(effect);
    }

	public boolean isAttacking() {
		return entityData.get(Attacking);
	}

	public boolean isKilled() {
		return removed || entityData.get(IsDead);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeNbt(writeSpawnData());
	}

	public CompoundNBT writeSpawnData() {
		CompoundNBT compound = new CompoundNBT();
		display.save(compound);
		compound.putInt("MaxHealth", stats.maxHealth);
		compound.put("Armor", NBTTags.nbtIItemStackMap(inventory.armor));
		compound.put("Weapons", NBTTags.nbtIItemStackMap(inventory.weapons));
		compound.putInt("Speed", ais.getWalkingSpeed());
		compound.putBoolean("DeadBody", stats.hideKilledBody);
		compound.putInt("StandingState", ais.getStandingType());
		compound.putInt("MovingState", ais.getMovingType());
		compound.putInt("Orientation", ais.orientation);
		compound.putFloat("PositionXOffset", ais.bodyOffsetX);
		compound.putFloat("PositionYOffset", ais.bodyOffsetY);
		compound.putFloat("PositionZOffset", ais.bodyOffsetZ);
		compound.putInt("Role", role.getType());
		compound.putInt("Job", job.getType());
		if(job.getType() == JobType.BARD){
			CompoundNBT bard = new CompoundNBT();
			job.save(bard);
			compound.put("Bard", bard);
		}
		if(job.getType() == JobType.PUPPET){
			CompoundNBT bard = new CompoundNBT();
			job.save(bard);
			compound.put("Puppet", bard);
		}
		if(role.getType() == RoleType.COMPANION){
			CompoundNBT bard = new CompoundNBT();
			role.save(bard);
			compound.put("Companion", bard);
		}
		
		if(this instanceof EntityCustomNpc){
			compound.put("ModelData", ((EntityCustomNpc)this).modelData.save());
		}
		return compound;
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		readSpawnData(buf.readNbt());
	}

	public void readSpawnData(CompoundNBT compound) {
		stats.setMaxHealth(compound.getInt("MaxHealth"));
		ais.setWalkingSpeed(compound.getInt("Speed"));
		stats.hideKilledBody = compound.getBoolean("DeadBody");
		ais.setStandingType(compound.getInt("StandingState"));
		ais.setMovingType(compound.getInt("MovingState"));
		ais.orientation = compound.getInt("Orientation");
		ais.bodyOffsetX = compound.getFloat("PositionXOffset");
		ais.bodyOffsetY = compound.getFloat("PositionYOffset");
		ais.bodyOffsetZ = compound.getFloat("PositionZOffset");
		
		inventory.armor = NBTTags.getIItemStackMap(compound.getList("Armor", 10));
		inventory.weapons = NBTTags.getIItemStackMap(compound.getList("Weapons", 10));
		advanced.setRole(compound.getInt("Role"));
		advanced.setJob(compound.getInt("Job"));
		if(job.getType() == JobType.BARD){
			CompoundNBT bard = compound.getCompound("Bard");
			job.load(bard);
		}		
		if(job.getType() == JobType.PUPPET){
			CompoundNBT puppet = compound.getCompound("Puppet");
			job.load(puppet);
		}	
		if(role.getType() == RoleType.COMPANION){
			CompoundNBT puppet = compound.getCompound("Companion");
			role.load(puppet);
		}		
		if(this instanceof EntityCustomNpc){
			((EntityCustomNpc)this).modelData.load(compound.getCompound("ModelData"));
		}
		display.readToNBT(compound);
		refreshDimensions();
	}

	@Override
    public CommandSource createCommandSourceStack(){
    	if(level.isClientSide)
    		return super.createCommandSourceStack();
		EntityUtil.Copy(this, CommandPlayer);
		CommandPlayer.setLevel(level);
		CommandPlayer.setPos(getX(), getY(), getZ());
		return new CommandSource(this, this.position(), this.getRotationVector(), this.level instanceof ServerWorld ? (ServerWorld)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
    }
	
	@Override
	public ITextComponent getName() {
		return new TranslationTextComponent(display.getName());
	}

//	@Override
//	public boolean canAttack(EntityType type){
//        return EntityType.BAT != type;
//    }

	public void setImmuneToFire(boolean immuneToFire) {
		stats.immuneToFire = immuneToFire;
	}

	@Override
	public boolean fireImmune() {
		return stats.immuneToFire;
	}
	
	@Override
	public boolean causeFallDamage(float distance, float modifier) {
		if (!this.stats.noFallDamage) {
			return super.causeFallDamage(distance, modifier);
		}
		return false;
	}
	
	@Override
	public void makeStuckInBlock(BlockState state, Vector3d motionMultiplierIn) {
		if (state != null && !state.is(Blocks.COBWEB) || !stats.ignoreCobweb) {
			super.makeStuckInBlock(state, motionMultiplierIn);
		}
    }
	
	@Override
	public boolean canBeCollidedWith(){
		return !isKilled() && display.getHitboxState() == 2;
	}

	@Override
	protected void pushEntities() {
		if(display.getHitboxState() != 0)
			return;
		super.pushEntities();
	}

	@Override
    public boolean isPushable(){
		return isWalking() && !isKilled();
    }

	@Override
    public PushReaction getPistonPushReaction(){
        return display.getHitboxState() == 0 ? super.getPistonPushReaction() : PushReaction.IGNORE;
    }
	
	public EntityAIRangedAttack getRangedTask(){
		return this.aiRange;
	}

	public String getRoleData(){
		return entityData.get(RoleData);
	}
	
	public void setRoleData(String s){
		entityData.set(RoleData, s);
	}

	public String getJobData(){
		return entityData.get(RoleData);
	}
	
	public void setJobData(String s){
		entityData.set(RoleData, s);
	}
	
	@Override
	public World getCommandSenderWorld() {
		return level;
	}


	@Override
	public boolean isInvisibleTo(PlayerEntity player){
		return display.getVisible() == 1 && player.getMainHandItem().getItem() != CustomItems.wand
				&& !display.availability.hasOptions();
	}

	@Override
	public boolean isInvisible(){
		return display.getVisible() != 0 && !display.availability.hasOptions();
	}


	public void setInvisible(ServerPlayerEntity playerMP){
		if(tracking.contains(playerMP.getId())) {
			tracking.remove(playerMP.getId());
			Packets.send(playerMP, new PacketNpcVisibleFalse(this.getId()));
		}
	}

	public void setVisible(ServerPlayerEntity playerMP){
		if(!tracking.contains(playerMP.getId())) {
			tracking.add(playerMP.getId());
			Packets.send(playerMP, new PacketNpcVisibleTrue(this));
		}
		//fix for data not syncing properly in 1.16
		Packets.send(playerMP, new PacketNpcUpdate(getId(), writeSpawnData()));
	}

	@Override
	public void sendMessage(ITextComponent var1, UUID sender) { }
	
	public void setCurrentAnimation(int animation) {
    	currentAnimation = animation;
    	entityData.set(Animation, animation);
	}

	public boolean canNpcSee(Entity entity){
		return this.getSensing().canSee(entity);
	}
	
	public boolean isFollower() {
		if(advanced.scenes.getOwner() != null)
			return true;
		return role.isFollowing() || job.isFollowing();
	}
		
	public LivingEntity getOwner(){
		if(advanced.scenes.getOwner() != null)
			return advanced.scenes.getOwner();
		if(role.getType() == RoleType.FOLLOWER && role instanceof RoleFollower)
			return ((RoleFollower)role).owner;
		
		if(role.getType() == RoleType.COMPANION && role instanceof RoleCompanion)
			return ((RoleCompanion)role).owner;

		if(job.getType() == JobType.FOLLOWER && job instanceof JobFollower)
			return ((JobFollower)job).following;
		
		return null;
	}
	
	public boolean hasOwner(){
		if(advanced.scenes.getOwner() != null)
			return true;
		return role.getType() == RoleType.FOLLOWER && ((RoleFollower) role).hasOwner() || 
				role.getType() == RoleType.COMPANION && ((RoleCompanion) role).hasOwner() || 
				job.getType() == JobType.FOLLOWER && ((JobFollower)job).hasOwner();
	}
	
	public int followRange() {
		if(advanced.scenes.getOwner() != null)
			return 4;
        if (role.getType() == RoleType.FOLLOWER && role.isFollowing())
			return 6;
        if (role.getType() == RoleType.COMPANION && role.isFollowing())
			return 4;
        if (job.getType() == JobType.FOLLOWER && job.isFollowing())
			return 4;
		
		return 15;
	}

	@Override
	public void restrictTo(BlockPos pos, int range){
		super.restrictTo(pos, range);
		ais.setStartPos(pos);
	}

	@Override
    protected float getDamageAfterArmorAbsorb(DamageSource source, float damage){
		if(role.getType() == RoleType.COMPANION)
			damage = ((RoleCompanion)role).getDamageAfterArmorAbsorb(source, damage);
    	return damage;
    }

	@Override
    public boolean isAlliedTo(Entity entity){
		if(!isClientSide()){
			if(entity instanceof PlayerEntity && getFaction().isFriendlyToPlayer((PlayerEntity)entity))
				return true;
			if(entity == getOwner())
				return true;
			if(entity instanceof EntityNPCInterface && ((EntityNPCInterface)entity).faction.id == faction.id)
				return true;
		}
        return super.isAlliedTo(entity);
    }
	
	public void setDataWatcher(EntityDataManager entityData) {
		this.entityData.assignValues(entityData.getAll());
	}

	@Override
    public void travel(Vector3d travelVector){
        BlockPos pos = blockPosition();
    	super.travel(travelVector);
    	if(role.getType() == RoleType.COMPANION && !isClientSide()) {
			BlockPos delta = blockPosition().subtract(pos);
			((RoleCompanion) role).addMovementStat(delta.getX(), delta.getY(), delta.getZ());
		}
    }
	
	@Override
	public boolean canBeLeashed(PlayerEntity player){
    	return false;
    }
    
    @Override
    public boolean isLeashed(){
        return false;
    }
	
	public boolean nearPosition(BlockPos pos) {
		BlockPos npcpos = blockPosition();
		float x = npcpos.getX() - pos.getX();
		float z = npcpos.getZ() - pos.getZ();
		float y = npcpos.getY() - pos.getY();
		float height = MathHelper.ceil(this.getBbHeight() + 1) * MathHelper.ceil(this.getBbHeight() + 1);
		return x * x + z * z < 2.5 && y * y < height + 2.5;
	}
	
	public void tpTo(LivingEntity owner) {
		if(owner == null)
			return;
		Direction facing = owner.getDirection().getOpposite();
		BlockPos pos = new BlockPos(owner.getX(), owner.getBoundingBox().minY, owner.getZ());
		pos = pos.offset(facing.getStepX(), 0, facing.getStepZ());
		pos = calculateTopPos(pos);
		
		for(int i = -1; i < 2; i++){
			for(int j = 0; j < 3; j++){
				BlockPos check;
				if(facing.getStepX() == 0){
					check = pos.offset(i, 0, j * facing.getStepZ());
				}
				else{
					check = pos.offset(j * facing.getStepX(), 0, i);
				}
				check = calculateTopPos(check);
				if(!level.getBlockState(check).isSolidRender(level, check) && !level.getBlockState(check.above()).isSolidRender(level, check.above())){
			        moveTo(check.getX() + 0.5F, check.getY(), check.getZ() + 0.5F, yRot, xRot);
			        this.getNavigation().stop();
					break;
				}
			}
		}
	}

//	@Override
//	public boolean checkSpawnRules(IWorld level, SpawnReason spawnReasonIn) {
//        return this.getWalkTargetValue(new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ())) >= 0.0F;
//    }
	
	@Override
    public boolean canBeRiddenInWater(Entity rider){
        return false;
    }


	@Override
	public void onSyncedDataUpdated(DataParameter<?> para) {
		super.onSyncedDataUpdated(para);
		if (Animation.equals(para)) {
			this.refreshDimensions();
		}
	}
}
