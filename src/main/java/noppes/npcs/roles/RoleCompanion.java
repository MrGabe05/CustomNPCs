package noppes.npcs.roles;

import com.google.common.collect.Multimap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.*;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.companion.*;

import java.util.*;
import java.util.Map.Entry;

public class RoleCompanion extends RoleInterface {
	private static final CompanionJobInterface NONE = new CompanionJobInterface(){

		@Override
		public CompoundNBT getNBT() {
			return null;
		}

		@Override
		public void setNBT(CompoundNBT compound) {

		}

		@Override
		public EnumCompanionJobs getType() {
			return EnumCompanionJobs.NONE;
		}
	};


	public NpcMiscInventory inventory;
	public String uuid = "";
	public String ownerName = "";
	public Map<EnumCompanionTalent, Integer> talents = new TreeMap<EnumCompanionTalent, Integer>();

	public boolean canAge = true;
	public long ticksActive = 0;
	public EnumCompanionStage stage = EnumCompanionStage.FULLGROWN;

	public PlayerEntity owner = null;
	public int companionID;

	public CompanionJobInterface companionJobInterface = NONE;

	public boolean hasInv = true;
	public boolean defendOwner = true;

	public CompanionFoodStats foodstats = new CompanionFoodStats();
	private int eatingTicks = 20;
	private IItemStack eating = null;
	private int eatingDelay = 00;

	public int currentExp = 0;

	public RoleCompanion(EntityNPCInterface npc) {
		super(npc);
		inventory = new NpcMiscInventory(12);
	}

	@Override
	public boolean aiShouldExecute() {
		PlayerEntity prev = owner;
		owner = getOwner();
		if(companionJobInterface.isSelfSufficient())
			return true;

		if(owner == null && !uuid.isEmpty())
			npc.removed = true;
		else if(prev != owner && owner != null){
			ownerName = owner.getDisplayName().getString();
			PlayerData data = PlayerData.get(owner);
			if(data.companionID != companionID)
				npc.removed = true;
		}
		return owner != null;
	}

	@Override
	public void aiUpdateTask() {
		if(owner != null && (!companionJobInterface.isSelfSufficient()))
			foodstats.onUpdate(npc);
		if(foodstats.getFoodLevel() >= 18){
			npc.stats.healthRegen = 0;
			npc.stats.combatRegen = 0;
		}
		if(foodstats.needFood() && isSitting()){
    		if(eatingDelay > 0){
    			eatingDelay--;
    			return;
    		}

			IItemStack prev = eating;
			eating = getFood();

			if(prev != null && eating == null)
				npc.setRoleData("");

			if(prev == null && eating != null){
				npc.setRoleData("eating");
				eatingTicks = 20;
			}

			if(isEating()){
				doEating();
			}

		}
		else if(eating != null && !isSitting()){
			eating = null;
			eatingDelay = 20;
			npc.setRoleData("");
		}

		ticksActive++;
		if(canAge && stage != EnumCompanionStage.FULLGROWN){
			if(stage == EnumCompanionStage.BABY && ticksActive > EnumCompanionStage.CHILD.matureAge){
				matureTo(EnumCompanionStage.CHILD);
			}
			else if(stage == EnumCompanionStage.CHILD && ticksActive > EnumCompanionStage.TEEN.matureAge){
				matureTo(EnumCompanionStage.TEEN);
			}
			else if(stage == EnumCompanionStage.TEEN && ticksActive > EnumCompanionStage.ADULT.matureAge){
				matureTo(EnumCompanionStage.ADULT);
			}
			else if(stage == EnumCompanionStage.ADULT && ticksActive > EnumCompanionStage.FULLGROWN.matureAge){
				matureTo(EnumCompanionStage.FULLGROWN);
			}
		}
	}

	public void clientUpdate() {
		if(npc.getRoleData().equals("eating")){
			eating = getFood();

			if(isEating()){
				doEating();
			}
		}
		else if(eating != null){
			eating = null;
		}

	}
	private void doEating(){
		if(eating == null || eating.isEmpty())
			return;
		ItemStack eating = this.eating.getMCItemStack();
    	if(npc.level.isClientSide){
    		Random rand = npc.getRandom();
            for (int j = 0; j < 2; ++j){
                Vector3d vec3 = new Vector3d(((double)rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                vec3.xRot(-npc.xRot * (float)Math.PI / 180.0F);
                vec3.yRot(-npc.yBodyRot * (float)Math.PI / 180.0F);
                Vector3d vec31 = new Vector3d(((double)rand.nextFloat() - 0.5D) * 0.3D, (double)(-rand.nextFloat()) * 0.6D - 0.3D, npc.getBbWidth() / 2 + 0.1);
                vec31.xRot(-npc.xRot * (float)Math.PI / 180.0F);
                vec31.yRot(-npc.yBodyRot * (float)Math.PI / 180.0F);
                vec31 = vec31.add(npc.getX(), npc.getY() + (double)npc.getBbHeight() + 0.1, npc.getZ());
                String s = "iconcrack_" + Item.getId(eating.getItem());

                npc.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, eating), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);

            }
    	}
    	else{
			eatingTicks--;

			if(eatingTicks <= 0){
				Food food = eating.getItem().getFoodProperties();
				if(inventory.removeItem(eating, 1)){
					foodstats.onFoodEaten(food, eating);
					npc.playSound(SoundEvents.PLAYER_BURP, 0.5F, npc.getRandom().nextFloat() * 0.1F + 0.9F);
				}
				eatingDelay = 20;
				npc.setRoleData("");
				this.eating = null;
			}
			else if(eatingTicks > 3 && eatingTicks % 2 == 0){
				Random rand = npc.getRandom();
	    		npc.playSound(SoundEvents.GENERIC_EAT, 0.5F + 0.5F * rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			}
    	}
	}

	public void matureTo(EnumCompanionStage stage){
		this.stage = stage;
		EntityCustomNpc npc = (EntityCustomNpc) this.npc;
		npc.ais.animationType = stage.animation;
		if(stage == EnumCompanionStage.BABY){
			npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5f, 0.5f, 0.5f);
			npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.5f, 0.5f, 0.5f);
			npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.5f, 0.5f, 0.5f);
			npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.7f, 0.7f, 0.7f);

			npc.ais.onAttack = 1;
			npc.ais.setWalkingSpeed(3);
			if(!talents.containsKey(EnumCompanionTalent.INVENTORY))
				talents.put(EnumCompanionTalent.INVENTORY, 0);
		}
		if(stage == EnumCompanionStage.CHILD){
			npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.6f, 0.6f, 0.6f);
			npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.6f, 0.6f, 0.6f);
			npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.6f, 0.6f, 0.6f);
			npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.8f, 0.8f);

			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(4);
			if(!talents.containsKey(EnumCompanionTalent.SWORD))
				talents.put(EnumCompanionTalent.SWORD, 0);
		}
		if(stage == EnumCompanionStage.TEEN){
			npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.8f, 0.8f);
			npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 0.8f, 0.8f);
			npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.8f, 0.8f, 0.8f);
			npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.9f, 0.9f, 0.9f);

			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(5);
			if(!talents.containsKey(EnumCompanionTalent.ARMOR))
				talents.put(EnumCompanionTalent.ARMOR, 0);
		}
		if(stage == EnumCompanionStage.ADULT || stage == EnumCompanionStage.FULLGROWN){
			npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(1f, 1f, 1f);
			npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(1f, 1f, 1f);
			npc.modelData.getPartConfig(EnumParts.BODY).setScale(1f, 1f, 1f);
			npc.modelData.getPartConfig(EnumParts.HEAD).setScale(1f, 1f, 1f);

			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(5);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("CompanionInventory", inventory.getToNBT());
		compound.putString("CompanionOwner", uuid);
		compound.putString("CompanionOwnerName", ownerName);
		compound.putInt("CompanionID", companionID);

		compound.putInt("CompanionStage", stage.ordinal());
		compound.putInt("CompanionExp", currentExp);
		compound.putBoolean("CompanionCanAge", canAge);
		compound.putLong("CompanionAge", ticksActive);

		compound.putBoolean("CompanionHasInv", hasInv);
		compound.putBoolean("CompanionDefendOwner", defendOwner);

		foodstats.writeNBT(compound);

		compound.putInt("CompanionJob", companionJobInterface.getType().ordinal());
		if(companionJobInterface.getType() != EnumCompanionJobs.NONE)
			compound.put("CompanionJobData", companionJobInterface.getNBT());

		ListNBT list = new ListNBT();
		for(EnumCompanionTalent talent : talents.keySet()){
			CompoundNBT c = new CompoundNBT();
			c.putInt("Talent", talent.ordinal());
			c.putInt("Exp", talents.get(talent));
			list.add(c);
		}
		compound.put("CompanionTalents", list);
		return compound;
	}

	@Override
	public void load(CompoundNBT compound) {
		inventory.setFromNBT(compound.getCompound("CompanionInventory"));
		uuid = compound.getString("CompanionOwner");
		ownerName = compound.getString("CompanionOwnerName");
		companionID = compound.getInt("CompanionID");

		stage = EnumCompanionStage.values()[compound.getInt("CompanionStage")];
		currentExp = compound.getInt("CompanionExp");
		canAge = compound.getBoolean("CompanionCanAge");
		ticksActive = compound.getLong("CompanionAge");

		hasInv = compound.getBoolean("CompanionHasInv");
		defendOwner = compound.getBoolean("CompanionDefendOwner");

		foodstats.readNBT(compound);

		ListNBT list = compound.getList("CompanionTalents", 10);
		Map<EnumCompanionTalent, Integer> talents = new TreeMap<EnumCompanionTalent, Integer>();
		for(int i = 0; i < list.size(); i++){
			CompoundNBT c = list.getCompound(i);
			EnumCompanionTalent talent = EnumCompanionTalent.values()[c.getInt("Talent")];
			talents.put(talent, c.getInt("Exp"));
		}
		this.talents = talents;

		setJob(compound.getInt("CompanionJob"));
		companionJobInterface.setNBT(compound.getCompound("CompanionJobData"));
		setStats();
	}

	private void setJob(int i) {
		EnumCompanionJobs companionJob = EnumCompanionJobs.values()[i];
		if(companionJob == EnumCompanionJobs.SHOP)
			companionJobInterface = new CompanionTrader();
		else if(companionJob == EnumCompanionJobs.FARMER)
			companionJobInterface = new CompanionFarmer();
		else if(companionJob == EnumCompanionJobs.GUARD)
			companionJobInterface = new CompanionGuard();
		else
			companionJobInterface = NONE;

		companionJobInterface.npc = npc;
	}

	@Override
	public void interact(PlayerEntity player) {
		interact(player, false);
	}
	public void interact(PlayerEntity player, boolean openGui) {
		if(player != null && companionJobInterface.getType() == EnumCompanionJobs.SHOP)
			((CompanionTrader) companionJobInterface).interact(player);
		if(player != owner || !npc.isAlive() || npc.isAttacking())
			return;
		if(player.isCrouching() || openGui){
			openGui(player);
		}
		else{
			setSitting(!isSitting());
		}
	}

	public int getTotalLevel(){
		int level = 0;
		for(EnumCompanionTalent talent : talents.keySet())
			level += this.getTalentLevel(talent);
		return level;
	}

	public int getMaxExp(){
		return 500 + getTotalLevel() * 200;
	}

	public void addExp(int exp){
		if(canAddExp(exp))
			this.currentExp += exp;
	}

	public boolean canAddExp(int exp){
		int newExp = this.currentExp + exp;
		return newExp >= 0 && newExp < getMaxExp();
	}

	public void gainExp(int chance){
		if(npc.getRandom().nextInt(chance) == 0)
			addExp(1);
	}

	private void openGui(PlayerEntity player){
		NoppesUtilServer.sendOpenGui(player, EnumGuiType.Companion, npc);
	}

	public PlayerEntity getOwner(){
		if(uuid == null || uuid.isEmpty())
			return null;
		try{
	        UUID id = UUID.fromString(uuid);
	        if(id != null)
	        	return NoppesUtilServer.getPlayer(npc.getServer(), id);
		}
		catch(IllegalArgumentException ex){

		}
    	return null;
	}


	public void setOwner(PlayerEntity player) {
		uuid = player.getUUID().toString();
	}


	public boolean hasTalent(EnumCompanionTalent talent) {
		return getTalentLevel(talent) > 0;
	}

	public int getTalentLevel(EnumCompanionTalent talent){
		if(!talents.containsKey(talent))
			return 0;

		int exp = talents.get(talent);
		if(exp >= 5000)
			return 5;
		if(exp >= 3000)
			return 4;
		if(exp >= 1700)
			return 3;
		if(exp >= 1000)
			return 2;
		if(exp >= 400)
			return 1;
		return 0;
	}

	public Integer getNextLevel(EnumCompanionTalent talent) {
		if(!talents.containsKey(talent))
			return 0;
		int exp = talents.get(talent);
		if(exp < 400)
			return 400;
		if(exp < 1000)
			return 700;
		if(exp < 1700)
			return 1700;
		if(exp < 3000)
			return 3000;
		return 5000;
	}

	public void levelSword(){
		if(!talents.containsKey(EnumCompanionTalent.SWORD))
			return;
	}
	public void levelTalent(EnumCompanionTalent talent, int exp){
		if(!talents.containsKey(EnumCompanionTalent.SWORD))
			return;
		talents.put(talent, exp + talents.get(talent));
	}

	public int getExp(EnumCompanionTalent talent) {
		if(talents.containsKey(talent))
			return talents.get(talent);
		return -1;
	}

	public void setExp(EnumCompanionTalent talent, int exp) {
		talents.put(talent, exp);
	}

	private boolean isWeapon(ItemStack item) {
		if(item == null || item.getItem() == null)
			return false;
		return item.getItem() instanceof SwordItem ||
				item.getItem() instanceof BowItem ||
				item.getItem() == Item.byBlock(Blocks.COBBLESTONE);
	}

	public boolean canWearWeapon(IItemStack stack){
		if(stack == null || stack.getMCItemStack().getItem() == null)
			return false;
		Item item = stack.getMCItemStack().getItem();
		if(item instanceof SwordItem)
			return canWearSword(stack);

		if(item instanceof BowItem)
			return this.getTalentLevel(EnumCompanionTalent.RANGED) > 2;

		if(item == Item.byBlock(Blocks.COBBLESTONE))
			return this.getTalentLevel(EnumCompanionTalent.RANGED) > 1;

		return false;
	}

	public boolean canWearArmor(ItemStack item){
		int level = getTalentLevel(EnumCompanionTalent.ARMOR);
		if(item == null || !(item.getItem() instanceof ArmorItem) || level <= 0)
			return false;

		if(level >= 5)
			return true;

		ArmorItem armor = (ArmorItem) item.getItem();
		int reduction = 1;
		if(armor.getMaterial() instanceof ArmorMaterial){
			reduction = ((ArmorMaterial)armor.getMaterial()).durabilityMultiplier;
		}
		if(reduction <= 5 && level >= 1)
			return true;
		if(reduction <= 7 && level >= 2)
			return true;
		if(reduction <= 15 && level >= 3)
			return true;
		if(reduction <= 33 && level >= 4)
			return true;
		return false;
	}
	public boolean canWearSword(IItemStack item){
		int level = getTalentLevel(EnumCompanionTalent.SWORD);
		if(item == null || !(item.getMCItemStack().getItem() instanceof SwordItem) || level <= 0)
			return false;
		if(level >= 5)
			return true;
		return getSwordDamage(item) - level < 4;
	}

	private double getSwordDamage(IItemStack item){
		if(item == null || !(item.getMCItemStack().getItem() instanceof SwordItem))
			return 0;
		Multimap<Attribute, AttributeModifier> map = item.getMCItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND);
		for (Entry<Attribute, AttributeModifier> entry : map.entries()) {
			if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
				AttributeModifier mod = entry.getValue();
				return mod.getAmount();
			}
		}
		return 0;
	}

	public void setStats(){
		IItemStack weapon = npc.inventory.getRightHand();
		npc.stats.melee.setStrength((int) (1 + getSwordDamage(weapon)));
		npc.stats.healthRegen = 0;
		npc.stats.combatRegen = 0;
		int ranged = getTalentLevel(EnumCompanionTalent.RANGED);
		if(ranged > 0 && weapon != null){
			Item item = weapon.getMCItemStack().getItem();
			if(ranged > 0 && item == Item.byBlock(Blocks.COBBLESTONE)){
				npc.inventory.setProjectile(weapon);
			}
			if(ranged > 0 && item instanceof BowItem){
				npc.inventory.setProjectile(NpcAPI.Instance().getIItemStack(new ItemStack(Items.ARROW)));
			}
		}

		inventory.setSize(2 + getTalentLevel(EnumCompanionTalent.INVENTORY) * 2);
	}

	public void setSelfsuficient(boolean bo){
		if(owner == null || bo == companionJobInterface.isSelfSufficient())
			return;
		PlayerData data = PlayerData.get(owner);
		if(!bo && data.hasCompanion())
			return;
		data.setCompanion(bo?null:npc);
		if(companionJobInterface.getType() == EnumCompanionJobs.GUARD)
			((CompanionGuard) companionJobInterface).isStanding = bo;
		else if(companionJobInterface.getType() == EnumCompanionJobs.FARMER)
			((CompanionFarmer) companionJobInterface).isStanding = bo;

	}

	public void setSitting(boolean sit){
		if(sit){
			npc.ais.animationType = AnimationType.SIT;
			npc.ais.onAttack = 3;
			npc.ais.setStartPos(npc.blockPosition());
			npc.getNavigation().stop();
			npc.teleportTo(npc.getStartXPos(), npc.getY(), npc.getStartZPos());
		}
		else{
			npc.ais.animationType = stage.animation;
			npc.ais.onAttack = 0;
		}
		npc.updateAI = true;
	}

	public boolean isSitting(){
		return npc.ais.animationType == AnimationType.SIT;
	}

	public float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
		if(!hasInv || getTalentLevel(EnumCompanionTalent.ARMOR) <=0)
			return damage;
        if (!source.isBypassArmor()){
        	damageArmor(damage);
            int i = 25 - getTotalArmorValue();
            float f1 = damage * (float)i;
            damage = f1 / 25.0F;
        }
		return damage;
	}

    private void damageArmor(float damage){
        damage /= 4.0F;

        if (damage < 1.0F){
            damage = 1.0F;
        }
        boolean hasArmor = false;
        Iterator<Entry<Integer, IItemStack>> ita = npc.inventory.armor.entrySet().iterator();
        while(ita.hasNext()){
        	Entry<Integer, IItemStack> entry = ita.next();
        	IItemStack item = entry.getValue();
        	if(item == null || !(item.getMCItemStack().getItem() instanceof ArmorItem))
        		continue;
        	hasArmor = true;
        	item.getMCItemStack().hurtAndBreak((int)damage, npc, (entity) -> {
				entity.broadcastBreakEvent(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, entry.getKey()));
			});
        	if(item.getStackSize() <= 0)
        		ita.remove();
        }
        this.gainExp(hasArmor?4:8);
    }

	public int getTotalArmorValue(){
    	int armorValue = 0;
    	for(IItemStack armor : npc.inventory.armor.values()){
    		if(armor != null && armor.getMCItemStack().getItem() instanceof ArmorItem)
    			armorValue += ((ArmorItem)armor.getMCItemStack().getItem()).getDefense();
    	}
    	return armorValue;
	}

	public boolean isFollowing(){
		if(companionJobInterface.isSelfSufficient())
			return false;
		return owner != null && !isSitting();
	}

	@Override
	public boolean defendOwner() {
		if(!defendOwner || owner == null || stage == EnumCompanionStage.BABY || companionJobInterface.isSelfSufficient())
			return false;
		return true;
	}

	public boolean hasOwner() {
		return !uuid.isEmpty();
	}

	public void addMovementStat(double x, double y, double z) {
        int i = Math.round(MathHelper.sqrt(x * x + y * y + z * z) * 100.0F);
        if(npc.isAttacking())
        	foodstats.addExhaustion(0.04F * (float)i * 0.01F);
        else
        	foodstats.addExhaustion(0.02F * (float)i * 0.01F);
	}

    private IItemStack getFood(){
		for(ItemStack item : inventory.items){
			if(!item.isEmpty() && item.getItem().getFoodProperties() != null){
				return NpcAPI.Instance().getIItemStack(item);
			}
		}
		return null;
    }

	public IItemStack getItemInHand() {
		if(eating != null && !eating.isEmpty())
			return eating;
		return npc.inventory.getRightHand();
	}

	public boolean isEating() {
		return eating != null && !eating.isEmpty();
	}

	public boolean hasInv() {
		if(!hasInv)
			return false;
		return hasTalent(EnumCompanionTalent.INVENTORY) || hasTalent(EnumCompanionTalent.ARMOR) || hasTalent(EnumCompanionTalent.SWORD);
	}

	public void attackedEntity(Entity entity) {
		IItemStack weapon = npc.inventory.getRightHand();
		gainExp(weapon == null?8:4);
		if(weapon == null)
			return;
		weapon.getMCItemStack().hurtAndBreak(1, npc, (e) -> {
			e.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
		});
		if(weapon.getMCItemStack().getCount() <= 0)
			npc.inventory.setRightHand(null);
	}

	public void addTalentExp(EnumCompanionTalent talent, int exp) {
		if(talents.containsKey(talent))
			exp += talents.get(talent);
		talents.put(talent, exp);
	}

	@Override
	public int getType() {
		return RoleType.COMPANION;
	}
}
