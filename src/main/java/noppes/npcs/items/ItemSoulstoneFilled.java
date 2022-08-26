package noppes.npcs.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

import java.util.List;

public class ItemSoulstoneFilled extends Item {
	public ItemSoulstoneFilled(){
		super(new Item.Properties().stacksTo(1));
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, World level, List<ITextComponent> list, ITooltipFlag flag) {
    	CompoundNBT compound = stack.getTag();
    	if(compound == null || !compound.contains("Entity", 10)){
    		list.add(new StringTextComponent(TextFormatting.RED + "Error"));
    		return;
    	}    	
    	ITextComponent name = new TranslationTextComponent(compound.getString("Name"));
    	if(compound.contains("DisplayName"))
    		name = new TranslationTextComponent(compound.getString("DisplayName")).append(" (").append(name).append(")");
    	list.add(new StringTextComponent(TextFormatting.BLUE + "").append(name));

    	if(stack.getTag().contains("ExtraText")){
			StringTextComponent text = new StringTextComponent("");
    		String[] split = compound.getString("ExtraText").split(",");
    		for(String s : split)
    			text.append(new TranslationTextComponent(s));
    		list.add(text);
    	}
    }
    
    @Override
	public ActionResultType useOn(ItemUseContext context){
    	if(context.getLevel().isClientSide)
    		return ActionResultType.SUCCESS;
		ItemStack stack = context.getItemInHand();
    	if(Spawn(context.getPlayer(), stack, context.getLevel(), context.getClickedPos()) == null)
    		return ActionResultType.FAIL;
		
		if(!context.getPlayer().abilities.instabuild)
			stack.split(1);
    	return ActionResultType.SUCCESS;
    }
    
    public static Entity Spawn(PlayerEntity player, ItemStack stack, World level, BlockPos pos){
        if(level.isClientSide)
        	return null;
    	if(stack.getTag() == null || !stack.getTag().contains("Entity", 10))
    		return null;
    	CompoundNBT compound = stack.getTag().getCompound("Entity");
    	Entity entity = EntityType.create(compound, level).orElse(null);
    	if(entity == null)
    		return null;
    	entity.setPos(pos.getX() + 0.5, pos.getY() + 1 + 0.2F, pos.getZ() + 0.5);
    	if(entity instanceof EntityNPCInterface){
    		EntityNPCInterface npc = (EntityNPCInterface) entity;
    		npc.ais.setStartPos(pos);
    		npc.setHealth(npc.getMaxHealth());
    		npc.setPos((float)pos.getX() + 0.5F, npc.getStartYPos(), (float)pos.getZ() + 0.5F);
    		
    		if(npc.role.getType() == RoleType.COMPANION && player != null){
    			PlayerData data = PlayerData.get(player);
    			if(data.hasCompanion())
    				return null;
    			((RoleCompanion)npc.role).setOwner(player);
    			data.setCompanion(npc);
    		}
    		if(npc.role.getType() == RoleType.FOLLOWER && player != null){
    			((RoleFollower)npc.role).setOwner(player);
    		}
    	}
		if(!level.addFreshEntity(entity)) {
			player.sendMessage(new TranslationTextComponent("error.failedToSpawn"), Util.NIL_UUID);
			return null;
		}
		return entity;
    }
}
