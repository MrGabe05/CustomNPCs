package noppes.npcs.shared.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class ContainerEmpty extends Container{

	public ContainerEmpty() {
		super(null, 0);
	}

	@Override
	public boolean stillValid(PlayerEntity var1) {
		return false;
	}

}
