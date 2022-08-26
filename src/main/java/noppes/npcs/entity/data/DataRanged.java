package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.constants.ParticleType;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.data.INPCRanged;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class DataRanged implements INPCRanged{
	private EntityNPCInterface npc;
	
	private int burstCount = 1;
	private int pDamage = 4;
	private int pSpeed = 10;
	private int pImpact = 0;
	private int pSize = 5;
	private int pArea = 0;
	private int pTrail = ParticleType.NONE;
	private int minDelay = 20;
	private int maxDelay = 40;
	private int rangedRange = 15;
	private int fireRate = 5;
	private int shotCount = 1;
	private int accuracy = 60;

	private int meleeDistance = 0;
	private int canFireIndirect = 0;
	
	private boolean pRender3D = true;
	private boolean pSpin = false;
	private boolean pStick = false;
	private boolean pPhysics = true;
	private boolean pXlr8 = false;
	private boolean pGlows = false;
	private boolean aimWhileShooting = false;

	private int pEffect = PotionEffectType.NONE;
	private int pDur = 5;
	private int pEffAmp = 0;
	
	private String fireSound = "minecraft:entity.arrow.shoot";	
	private String hitSound = "minecraft:entity.arrow.hit";
	private String groundSound = "minecraft:block.stone.break";
    
	public DataRanged(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	public void load(CompoundNBT compound){
		pDamage = compound.getInt("pDamage");
		pSpeed = compound.getInt("pSpeed");
		burstCount = compound.getInt("BurstCount");
		pImpact = compound.getInt("pImpact");
		pSize = compound.getInt("pSize");
		pArea = compound.getInt("pArea");
		pTrail = compound.getInt("pTrail");
		rangedRange = compound.getInt("MaxFiringRange");
		fireRate = compound.getInt("FireRate");
		minDelay = ValueUtil.CorrectInt(compound.getInt("minDelay"), 1, 9999);
		maxDelay = ValueUtil.CorrectInt(compound.getInt("maxDelay"), 1, 9999);
		shotCount = ValueUtil.CorrectInt(compound.getInt("ShotCount"), 1, 10);
		accuracy = compound.getInt("Accuracy");

		pRender3D = compound.getBoolean("pRender3D");
		pSpin = compound.getBoolean("pSpin");
		pStick = compound.getBoolean("pStick");
		pPhysics = compound.getBoolean("pPhysics");
		pXlr8 = compound.getBoolean("pXlr8");
		pGlows = compound.getBoolean("pGlows");
		aimWhileShooting = compound.getBoolean("AimWhileShooting");

		pEffect = compound.getInt("pEffect");
		pDur = compound.getInt("pDur");
		pEffAmp = compound.getInt("pEffAmp");

		fireSound = compound.getString("FiringSound");
		hitSound = compound.getString("HitSound");
		groundSound = compound.getString("GroundSound");

		canFireIndirect = compound.getInt("FireIndirect");
		meleeDistance = compound.getInt("DistanceToMelee");
	}

	public CompoundNBT save(CompoundNBT compound){
		compound.putInt("BurstCount", burstCount);
		compound.putInt("pSpeed", pSpeed);
		compound.putInt("pDamage", pDamage);
		compound.putInt("pImpact", pImpact);
		compound.putInt("pSize", pSize);
		compound.putInt("pArea", pArea);
		compound.putInt("pTrail", pTrail);

		compound.putInt("MaxFiringRange", rangedRange);
		compound.putInt("FireRate", fireRate);
		compound.putInt("minDelay", minDelay);
		compound.putInt("maxDelay", maxDelay);
		compound.putInt("ShotCount", shotCount);
		compound.putInt("Accuracy", accuracy);
		
		compound.putBoolean("pRender3D", pRender3D);
		compound.putBoolean("pSpin", pSpin);
		compound.putBoolean("pStick", pStick);
		compound.putBoolean("pPhysics", pPhysics);
		compound.putBoolean("pXlr8", pXlr8);
		compound.putBoolean("pGlows", pGlows);
		compound.putBoolean("AimWhileShooting", aimWhileShooting);

		compound.putInt("pEffect", pEffect);
		compound.putInt("pDur", pDur);
		compound.putInt("pEffAmp", pEffAmp);

		compound.putString("FiringSound", fireSound);
		compound.putString("HitSound", hitSound);
		compound.putString("GroundSound", groundSound);

		compound.putInt("FireIndirect", canFireIndirect);
		compound.putInt("DistanceToMelee", meleeDistance);
		
		return compound;
	}

	@Override
	public int getStrength(){
		return pDamage;
	}
	
	@Override
	public void setStrength(int strength){
		pDamage = strength;
	}
	
	@Override
	public int getSpeed(){
		return pSpeed;
	}
	
	@Override
	public void setSpeed(int speed){
		pSpeed = ValueUtil.CorrectInt(speed, 0, 100);
	}

	@Override
	public int getKnockback(){
		return pImpact;
	}

	@Override
	public void setKnockback(int punch){
		pImpact = punch;
	}

	@Override
	public int getSize(){
		return pSize;
	}

	@Override
	public void setSize(int size){
		pSize = size;
	}

	@Override
	public boolean getRender3D(){
		return pRender3D;
	}

	@Override
	public void setRender3D(boolean render3d){
		pRender3D = render3d;
	}

	@Override
	public boolean getSpins(){
		return pSpin;
	}

	@Override
	public void setSpins(boolean spins){
		pSpin = spins;
	}

	@Override
	public boolean getSticks(){
		return pStick;
	}

	@Override
	public void setSticks(boolean sticks){
		pStick = sticks;
	}

	@Override
	public boolean getHasGravity(){
		return pPhysics;
	}

	@Override
	public void setHasGravity(boolean hasGravity){
		pPhysics = hasGravity;
	}

	@Override
	public boolean getAccelerate(){
		return pXlr8;
	}

	@Override
	public void setAccelerate(boolean accelerate){
		pXlr8 = accelerate;
	}

	@Override
	public int getExplodeSize(){
		return pArea;
	}

	@Override
	public void setExplodeSize(int size){
		pArea = size;
	}
	
	@Override
	public int getEffectType(){
		return pEffect;
	}

	@Override
	public int getEffectTime(){
		return pDur;
	}

	@Override
	public int getEffectStrength(){
		return pEffAmp;
	}

	@Override
	public void setEffect(int type, int strength, int time){
		pEffect = type;
		pDur = time;
		pEffAmp = strength;
	}

	@Override
	public boolean getGlows(){
		return pGlows;
	}

	@Override
	public void setGlows(boolean glows){
		pGlows = glows;
	}
	
	@Override
	public int getParticle(){
		return pTrail;
	}
	
	@Override
	public void setParticle(int type){
		pTrail = type;
	}

	@Override
	public int getAccuracy(){
		return accuracy;
	}
	
	/**
	 * @param accuracy (1-100)
	 */
	@Override
	public void setAccuracy(int accuracy){
		this.accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
	}

	@Override
	public int getRange(){
		return rangedRange;
	}
	
	/**
	 * @param range Shooting range
	 */
	@Override
	public void setRange(int range){
		rangedRange = ValueUtil.CorrectInt(range, 1, 64);
	}

	@Override
	public int getDelayMin(){
		return minDelay;
	}

	@Override
	public int getDelayMax(){
		return maxDelay;
	}

	@Override
	public int getDelayRNG(){
		int delay = minDelay;
		if (maxDelay - minDelay > 0)
			delay += npc.level.random.nextInt(maxDelay - minDelay);
		return delay;
	}

	@Override
	public void setDelay(int min, int max){
		min = Math.min(min, max);
		minDelay = min;
		maxDelay = max;
	}
	
	@Override
	public int getBurst(){
		return burstCount;
	}
	
	@Override
	public void setBurst(int count){
		burstCount = count;
	}

	@Override
	public int getBurstDelay(){
		return fireRate;
	}

	@Override
	public void setBurstDelay(int delay){
		fireRate = delay;
	}
	
	@Override
	public String getSound(int type){
		String sound = null;
		if(type == 0)
			sound = fireSound;
		if(type == 1)
			sound = hitSound;
		if(type == 2)
			sound = groundSound;
		if(sound == null || sound.isEmpty())
			return null;
		return NoppesStringUtils.cleanResource(sound);
	}
	
	public SoundEvent getSoundEvent(int type){
		String sound = getSound(type);
		if(sound == null)
			return null;
		ResourceLocation res = new ResourceLocation(sound);
		SoundEvent ev = ForgeRegistries.SOUND_EVENTS.getValue(res);
		if(ev != null){
			return ev;
		}
		return new SoundEvent(res);
	}

	@Override
	public void setSound(int type, String sound){
		if(sound == null)
			sound = "";
		if(type == 0)
			fireSound = NoppesStringUtils.cleanResource(sound);
		if(type == 1)
			hitSound = NoppesStringUtils.cleanResource(sound);
		if(type == 2)
			groundSound = NoppesStringUtils.cleanResource(sound);
		
		npc.updateClient = true;
	}

	@Override
	public int getShotCount(){
		return shotCount;
	}

	@Override
	public void setShotCount(int count){
		shotCount = count;
	}

	@Override
	public boolean getHasAimAnimation(){
		return aimWhileShooting;
	}
	
	@Override
	public void setHasAimAnimation(boolean aim){
		aimWhileShooting = aim;
	}
	
	/**
	 * @return int
	 */
	@Override
	public int getFireType(){
		return canFireIndirect;
	}
	
	/**
	 * @param type
	 */
	@Override
	public void setFireType(int type){
		this.canFireIndirect = type;
	}
	
	/**
	 * @return Whether the NPC can use Ranged Attacks
	 */
	@Override
	public int getMeleeRange(){
		return meleeDistance;
	}
	
	/**
	 * @param range Whether the NPC can use Ranged Attacks
	 */
	@Override
	public void setMeleeRange(int range){
		this.meleeDistance = range;
		npc.updateAI = true;
	}
}
