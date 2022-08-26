package noppes.npcs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.constants.EnumParts;

public class ModelScaleRenderer extends ModelRenderer {

    public boolean isCompiled;

    /** The GL display list rendered by the Tessellator for this model */
    public int displayList;
    
    public ModelPartConfig config;
        
    public EnumParts part;

	public ModelScaleRenderer(Model base, EnumParts part) {
        super(base.texWidth, base.texHeight, 0, 0);
		this.part = part;
	}
    public ModelScaleRenderer(Model par1Model, int limbSwingAmount, int par3, EnumParts part){
        this(par1Model, part);
        this.texOffs(limbSwingAmount, par3);
    }
    
	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
    @Override
    public void translateAndRotate(MatrixStack mStack){
        if(config != null)
            mStack.translate(config.transX, config.transY, config.transZ);
        super.translateAndRotate(mStack);
        if(config != null)
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
    }

}
