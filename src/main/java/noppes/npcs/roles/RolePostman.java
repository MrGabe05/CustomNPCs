package noppes.npcs.roles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;

import java.util.ArrayList;
import java.util.List;

public class RolePostman extends RoleInterface{

	public NpcMiscInventory inventory = new NpcMiscInventory(1);
	private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
	private List<PlayerEntity> toCheck;
	
	public RolePostman(EntityNPCInterface npc) {
		super(npc);
	}

	public boolean aiShouldExecute() {
		if(npc.tickCount % 20 != 0)
			return false;

		toCheck = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(10, 10, 10));
		toCheck.removeAll(recentlyChecked);

		List<PlayerEntity> listMax = npc.level.getEntitiesOfClass(PlayerEntity.class, npc.getBoundingBox().inflate(20, 20, 20));
		recentlyChecked.retainAll(listMax);
		recentlyChecked.addAll(toCheck);
		
		for(PlayerEntity player : toCheck){
			if(PlayerData.get(player).mailData.hasMail()){
				npc.say(player, new Line("mailbox.gotmail"));
			}
		}
		return false;
	}
	
	@Override
	public boolean aiContinueExecute() {
		return false;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
    	nbttagcompound.put("PostInv", inventory.getToNBT());
    	return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		inventory.setFromNBT(nbttagcompound.getCompound("PostInv"));
	}


	@Override
	public void interact(PlayerEntity player) {
		NoppesUtilServer.openContainerGui((ServerPlayerEntity) player, EnumGuiType.PlayerMailman, (buf) -> {
			buf.writeBoolean(true);
			buf.writeBoolean(true);
		});
	}

	@Override
	public int getType() {
		return RoleType.MAILMAN;
	}

}
