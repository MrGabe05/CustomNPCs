package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.HashMap;

public final class NpcBlockHelper {
	public static Item getCrop(CropsBlock crops) {
		return crops.getBaseSeedId().asItem();
	}
	public static void setDefaultState(Block block, BlockState state){
		block.registerDefaultState(state);
	}
}
