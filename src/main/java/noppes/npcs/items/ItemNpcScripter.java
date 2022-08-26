package noppes.npcs.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;

public class ItemNpcScripter extends Item{
	
    public ItemNpcScripter(){
		super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }
    

	@Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
    	ItemStack itemstack = player.getItemInHand(hand);
		if(!level.isClientSide || hand != Hand.MAIN_HAND)
			return new ActionResult(ActionResultType.SUCCESS, itemstack);
		CustomNpcs.proxy.openGui(player, EnumGuiType.ScriptPlayers);
		return new ActionResult(ActionResultType.SUCCESS, itemstack);
    }
}
