package noppes.npcs;

import net.minecraft.nbt.CompoundNBT;

public interface ICompatibilty {
	int getVersion();
	void setVersion(int version);
	CompoundNBT save(CompoundNBT compound);
}
