package noppes.npcs;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;

public class Resistances {

	public float knockback = 1f;
	public float arrow = 1f;
	public float melee = 1f;
	public float explosion = 1f;
	
	public CompoundNBT save() {
		CompoundNBT compound = new CompoundNBT();
		compound.putFloat("Knockback", knockback);
		compound.putFloat("Arrow", arrow);
		compound.putFloat("Melee", melee);
		compound.putFloat("Explosion", explosion);
		return compound;
	}

	public void readToNBT(CompoundNBT compound) {
		knockback = compound.getFloat("Knockback");
		arrow = compound.getFloat("Arrow");
		melee = compound.getFloat("Melee");
		explosion = compound.getFloat("Explosion");
	}

	public float applyResistance(DamageSource source, float damage) {
		if(source.msgId.equals("arrow") || source.msgId.equals("thrown") || source.isProjectile()){
			damage *= (2 - arrow);
		}
		else if(source.msgId.equals("player") || source.msgId.equals("mob")){
			damage *= (2 - melee);
		}
		else if(source.msgId.equals("explosion") || source.msgId.equals("explosion.player")){
			damage *= (2 - explosion);
		}
		
		return damage;
	}

}
