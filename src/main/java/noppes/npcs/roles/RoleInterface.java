package noppes.npcs.roles;

import java.util.HashMap;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.entity.EntityNPCInterface;


public abstract class RoleInterface implements INPCRole {
	public static final RoleInterface NONE = new RoleInterface(null) {
		@Override
		public CompoundNBT save(CompoundNBT compound) {
			return compound;
		}

		@Override
		public void load(CompoundNBT compound) {

		}

		@Override
		public void interact(PlayerEntity player) {

		}

		@Override
		public int getType() {
			return RoleType.NONE;
		}
	};

	public EntityNPCInterface npc;
	public HashMap<String,String> dataString = new HashMap<String,String>();
	public RoleInterface(EntityNPCInterface npc){
		this.npc = npc;
	}
	public abstract CompoundNBT save(CompoundNBT compound);
	public abstract void load(CompoundNBT compound);
	public abstract void interact(PlayerEntity player);
	public void killed(){};
	public void delete(){};
	
	public boolean aiShouldExecute() {
		return false;
	}
	
	public boolean aiContinueExecute() {
		return false;
	}
	public void aiStartExecuting() {}
	public void aiUpdateTask() {}
	public boolean defendOwner() {
		return false;
	}

	public boolean isFollowing() {
		return false;
	}
	
	public void clientUpdate() {
		
	}
}
