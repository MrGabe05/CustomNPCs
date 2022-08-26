package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class CompanionJobInterface {

	public EntityNPCInterface npc;

	public abstract CompoundNBT getNBT();

	public abstract void setNBT(CompoundNBT compound);

	public abstract EnumCompanionJobs getType();

	public void onUpdate(){}

	public boolean isSelfSufficient(){
		return false;
	}


}
