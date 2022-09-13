package noppes.npcs.roles.companion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;

public class CompanionGuard extends CompanionJobInterface{
	public boolean isStanding = false;
	@Override
	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putBoolean("CompanionGuardStanding", isStanding);
		return compound;
	}

	@Override
	public void setNBT(CompoundNBT compound) {
		isStanding = compound.getBoolean("CompanionGuardStanding");
	}
	
	public boolean isEntityApplicable(Entity entity) {
		
    	if(entity instanceof PlayerEntity || entity instanceof EntityNPCInterface)
    		return false;

    	else if (entity instanceof CreeperEntity) {
			return false;
    	}
    	else return entity instanceof IMob;
    }

	public boolean isSelfSufficient(){
		return isStanding;
	}

	@Override
	public EnumCompanionJobs getType() {
		return EnumCompanionJobs.GUARD;
	}
}
