package noppes.npcs.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import noppes.npcs.*;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

public class ItemSoulstoneEmpty extends Item {

	public ItemSoulstoneEmpty(){
		super(new Item.Properties().stacksTo(64).tab(CustomTabs.tab));
	}
    
	public boolean store(LivingEntity entity, ItemStack stack, PlayerEntity player) {
		if(!hasPermission(entity, player) || entity instanceof PlayerEntity)
			return false;
		ItemStack stone = new ItemStack(CustomItems.soulstoneFull);
		CompoundNBT compound = new CompoundNBT();
		if(!entity.saveAsPassenger(compound))
			return false;
		ServerCloneController.Instance.cleanTags(compound);
		stone.addTagElement("Entity", compound);
		
        String name = entity.getEncodeId();
        if (name == null)
        	name = "generic";
		stone.addTagElement("Name", StringNBT.valueOf(name));
        if(entity instanceof EntityNPCInterface){
        	EntityNPCInterface npc = (EntityNPCInterface) entity;
    		stone.addTagElement("DisplayName", StringNBT.valueOf(entity.getName().getString()));
    		if(npc.role.getType() == RoleType.COMPANION){
    			RoleCompanion role = (RoleCompanion) npc.role;
        		stone.addTagElement("ExtraText", StringNBT.valueOf("companion.stage,: ," + role.stage.name));
    		}
        }
        else if(entity.hasCustomName())
    		stone.addTagElement("DisplayName", StringNBT.valueOf(entity.getCustomName().getString()));
		NoppesUtilServer.GivePlayerItem(player, player, stone);
		
		if(!player.abilities.instabuild){
			stack.split(1);
			if(stack.getCount() <= 0)
				player.inventory.removeItem(stack);
		}
		
		entity.removed = true;
		return true;
	}
	
	public boolean hasPermission(LivingEntity entity, PlayerEntity player){
		if(NoppesUtilServer.isOp(player))
			return true;
		if(CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.SOULSTONE_ALL))
			return true;
		if(entity instanceof EntityNPCInterface){
			EntityNPCInterface npc = (EntityNPCInterface) entity;
			if(npc.role.getType() == RoleType.COMPANION){
				RoleCompanion role = (RoleCompanion) npc.role;
				if(role.getOwner() == player)
					return true;
			}
			if(npc.role.getType() == RoleType.FOLLOWER){
				RoleFollower role = (RoleFollower) npc.role;
				if(role.getOwner() == player)
					return !role.refuseSoulStone;
			}
			return CustomNpcs.SoulStoneNPCs;
		}
		if(entity instanceof AnimalEntity)
			return CustomNpcs.SoulStoneAnimals;
		
		return false;
	}
}
