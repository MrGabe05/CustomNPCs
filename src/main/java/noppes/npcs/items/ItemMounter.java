package noppes.npcs.items;

import net.minecraft.item.Item;
import noppes.npcs.CustomTabs;


public class ItemMounter extends Item{
    public ItemMounter(){
        super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }
}
