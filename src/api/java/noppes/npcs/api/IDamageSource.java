package noppes.npcs.api;

import net.minecraft.util.DamageSource;
import noppes.npcs.api.entity.IEntity;

public interface IDamageSource {

	String getType();
	
	boolean isUnblockable();
	
	boolean isProjectile();
	
	IEntity getTrueSource();
	
	IEntity getImmediateSource();
	
	DamageSource getMCDamageSource();
}
