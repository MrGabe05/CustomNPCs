package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import noppes.npcs.CustomNpcs;

public abstract class BlockInterface extends ContainerBlock {

	protected BlockInterface(Block.Properties properties) {
		super(properties);
	}

    public Block register(String name){
    	return setRegistryName(CustomNpcs.MODID, name);
    }
}
