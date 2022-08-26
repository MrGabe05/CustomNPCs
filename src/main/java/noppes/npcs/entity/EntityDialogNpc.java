package noppes.npcs.entity;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityDialogNpc extends EntityNPCInterface{

	public EntityDialogNpc(World world) {
		super(CustomEntities.entityCustomNpc, world);
	}


	@Override
    public boolean isInvisibleTo(PlayerEntity player){
        return true;
    }

	@Override
	public boolean isInvisible(){
		return true;
	}

	@Override
	public void tick(){
		
	}
    
	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		return ActionResultType.FAIL;
	}
}
