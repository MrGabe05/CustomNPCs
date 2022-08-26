package noppes.npcs.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import com.mojang.blaze3d.systems.RenderSystem;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderNpcDragon<T extends EntityNPCInterface, M extends EntityModel<T>> extends RenderNPCInterface<T, M>{

	public RenderNpcDragon(EntityRendererManager manager, M model, float f) {
		super(manager, model, f);
	}

	@Override
    protected void scale(T npc, MatrixStack matrixScale, float f){
		matrixScale.translate(0, 0, 0.6f / 5 * npc.display.getSize());
    	super.scale(npc, matrixScale, f);
    }
}
