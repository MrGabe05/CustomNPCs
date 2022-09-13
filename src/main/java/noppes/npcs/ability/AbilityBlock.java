package noppes.npcs.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import noppes.npcs.api.event.NpcEvent.DamagedEvent;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilityBlock extends AbstractAbility implements IAbilityDamaged{

	public AbilityBlock(EntityNPCInterface npc) {
		super(npc);
	}

	@Override
	public boolean canRun(LivingEntity target){
		return super.canRun(target);
	}

	@Override
	public boolean isType(EnumAbilityType type){
		return type == EnumAbilityType.ATTACKED;
	}

	@Override
	public void handleEvent(DamagedEvent event) {
		ServerWorld level = (ServerWorld) npc.getCommandSenderWorld();
		level.broadcastEntityEvent(npc, (byte)29);
		event.setCanceled(true);
		endAbility();
	}
}
