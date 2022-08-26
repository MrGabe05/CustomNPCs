package noppes.npcs.ability;

import net.minecraft.entity.LivingEntity;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilitySnare extends AbstractAbility implements IAbilityUpdate{

	public AbilitySnare(EntityNPCInterface entity) {
		super(entity);
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isType(EnumAbilityType type){
		return type == EnumAbilityType.ATTACKED || type == EnumAbilityType.UPDATE;
	}
}
