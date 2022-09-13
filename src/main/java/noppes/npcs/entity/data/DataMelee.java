package noppes.npcs.entity.data;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.entity.EntityNPCInterface;

public class DataMelee implements INPCMelee{

	private final EntityNPCInterface npc;
	
	private int attackStrength = 5;
	private int attackSpeed = 20;
	private int attackRange = 2;
	private int knockback = 0;
	
	private int potionType = PotionEffectType.NONE;
	private int potionDuration = 5; //20 = 1 second
	private int potionAmp = 0;
	
	public DataMelee(EntityNPCInterface npc){
		this.npc = npc;
	}

	public void load(CompoundNBT compound){
		attackSpeed = compound.getInt("AttackSpeed");
		setStrength(compound.getInt("AttackStrenght"));
		attackRange = compound.getInt("AttackRange");
		knockback = compound.getInt("KnockBack");
		
		potionType = compound.getInt("PotionEffect");
		potionDuration = compound.getInt("PotionDuration");
		potionAmp = compound.getInt("PotionAmp");
	}

	public CompoundNBT save(CompoundNBT compound){
		compound.putInt("AttackStrenght", attackStrength);
		compound.putInt("AttackSpeed", attackSpeed);
		compound.putInt("AttackRange", attackRange);
		compound.putInt("KnockBack", knockback);
		
		compound.putInt("PotionEffect", potionType);
		compound.putInt("PotionDuration", potionDuration);
		compound.putInt("PotionAmp", potionAmp);
		
		return compound;
	}
	
	@Override
	public int getStrength(){
		return attackStrength;
	}

	@Override
	public void setStrength(int strength){
		attackStrength = strength;
		npc.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackStrength);
	}

	@Override
	public int getDelay(){
		return attackSpeed;
	}

	@Override
	public void setDelay(int speed){
		attackSpeed = speed;
	}
	
	@Override
	public int getRange(){
		return attackRange;
	}
	
	@Override
	public void setRange(int range){
		attackRange = range;
		
	}
	
	@Override
	public int getKnockback(){
		return knockback;
	}
	
	@Override
	public void setKnockback(int knockback){
		this.knockback = knockback;
	}
	
	/**
	 * @see noppes.npcs.api.constants.PotionEffectType
	 */
	@Override
	public int getEffectType(){
		return potionType;
	}
	
	@Override
	public int getEffectTime(){
		return potionDuration;
	}
		
	@Override
	public int getEffectStrength(){
		return potionAmp;
	}
	
	@Override
	public void setEffect(int type, int strength, int time){
		potionType = type;
		potionDuration = time;
		potionAmp = strength;
	}
}
