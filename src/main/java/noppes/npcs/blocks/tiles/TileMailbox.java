package noppes.npcs.blocks.tiles;

import net.minecraft.tileentity.TileEntity;
import noppes.npcs.CustomBlocks;

public class TileMailbox extends TileNpcEntity {
    private int type = 0;
    public TileMailbox(){
        super(CustomBlocks.tile_mailbox);
    }

    public TileMailbox setModel(int type){
        this.type = type;
        return this;
    }

    public int getModel(){
        return type;
    }
}
