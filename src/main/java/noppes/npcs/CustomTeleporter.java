package noppes.npcs;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.Function;

public class CustomTeleporter extends Teleporter{

	private final float yRot;
	private final float xRot;
	private final Vector3d pos;

	public CustomTeleporter(ServerWorld par1ServerWorld, Vector3d pos, float yRot, float xRot) {
		super(par1ServerWorld);
		this.pos = pos;
		this.yRot = yRot;
		this.xRot = xRot;
	}

	@Override
	public Optional<TeleportationRepositioner.Result> findPortalAround(BlockPos pos, boolean isNether) {
		return Optional.empty();
	}

	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
		return new PortalInfo(pos, Vector3d.ZERO, yRot, xRot);
	}
}
