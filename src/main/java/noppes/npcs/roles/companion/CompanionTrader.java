package noppes.npcs.roles.companion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumGuiType;

public class CompanionTrader extends CompanionJobInterface{

	@Override
	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		return compound;
	}

	@Override
	public void setNBT(CompoundNBT compound) {
		
	}
	
	public void interact(PlayerEntity player){
		NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionTrader, npc);
	}

	@Override
	public EnumCompanionJobs getType() {
		return EnumCompanionJobs.SHOP;
	}
}
