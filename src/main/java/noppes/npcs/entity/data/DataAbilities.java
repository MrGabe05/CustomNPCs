package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class DataAbilities{
	public List<AbstractAbility> abilities = new ArrayList<AbstractAbility>();
	public EntityNPCInterface npc;
	
	public DataAbilities(EntityNPCInterface npc) {
		this.npc = npc;
	}

	public CompoundNBT save(CompoundNBT compound) {
		
		return compound;
	}

	public void readToNBT(CompoundNBT compound) {
		
	}

	public AbstractAbility getAbility(EnumAbilityType type) {
		LivingEntity target = npc.getTarget();
		for(AbstractAbility ability : abilities){
			if(ability.isType(type) && ability.canRun(target)){
				return ability;
			}
		}
		return null;
	}
}
