package noppes.npcs.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import noppes.npcs.client.layer.LayerSlimeNpc;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.entity.EntityNpcSlime;

public class RenderNpcSlime<T extends EntityNpcSlime, M extends ModelNpcSlime<T>> extends RenderNPCInterface<T, M>{
    private final Model scaleAmount;

    public RenderNpcSlime(EntityRendererManager manager, M par1Model, Model limbSwingAmountModel, float par3){
        super(manager, par1Model, par3);
        this.scaleAmount = limbSwingAmountModel;
        this.addLayer(new LayerSlimeNpc(this));
    }

}
