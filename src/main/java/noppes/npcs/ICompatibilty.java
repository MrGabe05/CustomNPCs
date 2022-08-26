package noppes.npcs;

import net.minecraft.nbt.CompoundNBT;

public interface ICompatibilty {
	public int getVersion();
	public void setVersion(int version);
	public CompoundNBT save(CompoundNBT compound);
}
