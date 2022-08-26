package noppes.npcs.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class ItemNpcBlock extends BlockItem {
	public final Block block;
	
	public ItemNpcBlock(Block block, Properties builder) {
		super(block, builder);
		this.block = block;
	}
	
}
