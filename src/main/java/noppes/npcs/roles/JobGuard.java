package noppes.npcs.roles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class JobGuard extends JobInterface{
	
	public List<String> targets = new ArrayList<String>();
	
	public JobGuard(EntityNPCInterface npc) {
		super(npc);
	}
	
	public boolean isEntityApplicable(Entity entity) {
    	if(entity instanceof PlayerEntity || entity instanceof EntityNPCInterface) return false;
        return targets.contains(entity.getType().getDescriptionId());
    }

	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.put("GuardTargets", NBTTags.nbtStringList(targets));
		return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		
		targets = NBTTags.getStringList(nbttagcompound.getList("GuardTargets", 10));
	}

	@Override
	public int getType() {
		return JobType.GUARD;
	}
}
