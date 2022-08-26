package noppes.npcs.items;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class ItemScriptedDoor extends TallBlockItem {
	
    public ItemScriptedDoor(Block block){
    	super(block, new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }
	
    @Override
    public ActionResultType useOn(ItemUseContext context){
    	ActionResultType res = super.useOn(context);
    	if(res == ActionResultType.SUCCESS && !context.getLevel().isClientSide){
            PlayerData data = PlayerData.get(context.getPlayer());
            data.scriptBlockPos = context.getClickedPos();
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.ScriptDoor, null, context.getClickedPos().above());
        	return ActionResultType.SUCCESS;
    	}
    	return res;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity playerIn){
        return stack;
    }
    
//    @Override
//    public int getColorFromItemStack(ItemStack par1ItemStack, int limbSwingAmount){
//		return 0x8B4513;
//    }

}
