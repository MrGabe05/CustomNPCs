package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.Direction;
import noppes.npcs.constants.EnumPlanePosition;
import noppes.npcs.mixin.ModelRendererMixin;

import java.util.Arrays;
import java.util.Objects;

public class ModelPlaneNpcsRenderer extends ModelRenderer {

    private final int xTexOffs;
    private final int yTexOffs;

    public ModelPlaneNpcsRenderer(Model base, int i, int j) {
    	super(base.texWidth, base.texHeight, i, j);
        xTexOffs = i;
        yTexOffs = j;
    }

    public void addBackPlane(float f, float f1, float f2, int i, int j) {
    	addPlane(f, f1, f2, i, j, 0, 0.0F, EnumPlanePosition.BACK);
    }

    public void addSidePlane(float f, float f1, float f2, int j, int k) {
    	addPlane(f, f1, f2, 0, j, k, 0.0F,EnumPlanePosition.LEFT);
    }

    public void addTopPlane(float f, float f1, float f2, int i, int k) {
    	addPlane(f, f1, f2, i, 0, k, 0.0F,EnumPlanePosition.TOP);
    }

    public void addBackPlane(float f, float f1, float f2, int i, int j, float scale) {
    	addPlane(f, f1, f2, i, j, 0, scale,EnumPlanePosition.BACK);
    }

    public void addSidePlane(float f, float f1, float f2, int j, int k, float scale) {
    	addPlane(f, f1, f2, 0, j, k, scale,EnumPlanePosition.LEFT);
    }

    public void addTopPlane(float f, float f1, float f2, int i, int k, float scale) {
    	addPlane(f, f1, f2, i, 0, k, scale,EnumPlanePosition.TOP);
    }
    
    public void addPlane(float x, float y, float z, int dx, int dy, int dz, float f3,EnumPlanePosition pos){
        addBox(x, y, z, dx, dy, dz);

        float xx = x + dx;
        if (mirror)
        {
            float var14 = xx;
            xx = x;
            x = var14;
        }
        PositionTextureVertex lvt_18_2_ = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex lvt_19_1_ = new PositionTextureVertex(xx, y, z, 0.0F, 8.0F);
        PositionTextureVertex lvt_20_1_ = new PositionTextureVertex(xx, y + dy, z, 8.0F, 8.0F);
        PositionTextureVertex lvt_21_1_ = new PositionTextureVertex(x, y + dy, z, 8.0F, 0.0F);
        PositionTextureVertex lvt_22_1_ = new PositionTextureVertex(x, y, z + dz, 0.0F, 0.0F);
        PositionTextureVertex lvt_23_1_ = new PositionTextureVertex(xx, y, z + dz, 0.0F, 8.0F);
        PositionTextureVertex lvt_24_1_ = new PositionTextureVertex(xx, y + dy, z + dz, 8.0F, 8.0F);
        PositionTextureVertex lvt_25_1_ = new PositionTextureVertex(x, y + dy, z + dz, 8.0F, 0.0F);

        ModelBox box = this.cubes.get(this.cubes.size() - 1);
        if(pos == EnumPlanePosition.LEFT){
            box.polygons = new TexturedQuad[]{new TexturedQuad(new PositionTextureVertex[]{lvt_23_1_, lvt_19_1_, lvt_20_1_, lvt_24_1_}, xTexOffs, yTexOffs, xTexOffs + dz, yTexOffs + dy, xTexSize, yTexSize, mirror, Direction.WEST)};
        }
        if(pos == EnumPlanePosition.TOP){
            box.polygons = new TexturedQuad[]{new TexturedQuad(new PositionTextureVertex[]{lvt_23_1_, lvt_22_1_, lvt_18_2_, lvt_19_1_}, xTexOffs, yTexOffs, xTexOffs + dx, yTexOffs + dz, xTexSize, yTexSize, mirror, Direction.UP)};
        }
        if(pos == EnumPlanePosition.BACK){
            box.polygons = new TexturedQuad[]{new TexturedQuad(new PositionTextureVertex[]{lvt_19_1_, lvt_18_2_, lvt_21_1_, lvt_20_1_}, xTexOffs, yTexOffs, xTexOffs + dy, yTexOffs + dx, xTexSize, yTexSize, mirror, Direction.SOUTH)};
        }
    }

    public void render(MatrixStack p_228309_1_, IVertexBuilder p_228309_2_, int p_228309_3_, int p_228309_4_, float p_228309_5_, float p_228309_6_, float p_228309_7_, float p_228309_8_) {
        super.render(p_228309_1_, p_228309_2_, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
    }
}
