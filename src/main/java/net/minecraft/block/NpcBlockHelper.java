package net.minecraft.block;

import net.minecraft.item.Item;

public final class NpcBlockHelper {

	public static Item getCrop(CropsBlock crops) {
		return crops.getBaseSeedId().asItem();
	}
	public static void setDefaultState(Block block, BlockState state){
		block.registerDefaultState(state);
	}
}
