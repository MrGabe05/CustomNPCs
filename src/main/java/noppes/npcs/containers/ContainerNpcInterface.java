package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.wrapper.ContainerWrapper;

public class ContainerNpcInterface extends Container{
	private int posX, posZ;
	public PlayerEntity player;
	public IContainer scriptContainer;

	public ContainerNpcInterface(ContainerType type, int containerId, PlayerInventory playerInventory){
		super(type, containerId);
		this.player = playerInventory.player;
    	posX = MathHelper.floor(player.getX());
    	posZ = MathHelper.floor(player.getZ());
    	player.setDeltaMovement(Vector3d.ZERO);
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
        return !player.removed && posX == MathHelper.floor(player.getX()) && posZ == MathHelper.floor(player.getZ());
	}

	public static IContainer getOrCreateIContainer(ContainerNpcInterface container) {
		if(container.scriptContainer != null)
			return container.scriptContainer;
		return container.scriptContainer = new ContainerWrapper(container);
	}

}
