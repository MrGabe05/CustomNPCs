package noppes.npcs.roles.companion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.constants.EnumCompanionJobs;

public class CompanionFarmer extends CompanionJobInterface{
	public boolean isStanding = false;
	@Override
	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putBoolean("CompanionFarmerStanding", isStanding);
		return compound;
	}

	@Override
	public void setNBT(CompoundNBT compound) {
		isStanding = compound.getBoolean("CompanionFarmerStanding");
	}

	@Override
	public EnumCompanionJobs getType() {
		return EnumCompanionJobs.FARMER;
	}

	@Override
	public boolean isSelfSufficient(){
		return isStanding;
	}

	@Override
	public void onUpdate(){
		
	}
}
