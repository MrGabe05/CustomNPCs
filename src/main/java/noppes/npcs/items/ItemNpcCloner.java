package noppes.npcs.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class ItemNpcCloner extends Item{
	
    public ItemNpcCloner(){
        super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }

	@Override
    public ActionResultType useOn(ItemUseContext context){
        if(!context.getLevel().isClientSide)
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.MobSpawner, null, context.getClickedPos());
        return ActionResultType.SUCCESS;
    }
}
