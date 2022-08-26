package noppes.npcs.client.model;

import net.minecraft.client.renderer.entity.model.PlayerModel;

public class ModelPlayer64x32 extends PlayerModel {
    public ModelPlayer64x32() {
        super(1, false);

        this.texWidth = 64;
        this.texHeight = 32;
    }
}
