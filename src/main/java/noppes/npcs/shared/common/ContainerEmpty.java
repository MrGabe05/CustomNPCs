package noppes.npcs.shared.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class ContainerEmpty extends Container{

	public ContainerEmpty() {
		super(null, 0);
	}

	@Override
	public boolean stillValid(PlayerEntity var1) {
		return false;
	}

}
