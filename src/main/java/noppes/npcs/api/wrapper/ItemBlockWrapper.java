package noppes.npcs.api.wrapper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.constants.ItemType;
import noppes.npcs.api.item.IItemBlock;

public class ItemBlockWrapper extends ItemStackWrapper implements IItemBlock{
	protected String blockName;
	protected ItemBlockWrapper(ItemStack item) {
		super(item);
		Block b = Block.byItem(item.getItem());
		blockName = ForgeRegistries.BLOCKS.getKey(b) + "";
	}

	@Override
	public int getType(){
		return ItemType.BLOCK;
	}

	@Override
	public String getBlockName() {
		return blockName;
	}
}
