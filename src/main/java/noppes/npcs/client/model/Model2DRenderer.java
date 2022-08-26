package noppes.npcs.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import noppes.npcs.client.renderer.ImageDownloadAlt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Model2DRenderer extends ModelRenderer {
    private static final RenderTypeBuffers typeBuffer = new RenderTypeBuffers();
    //private VertexFormat format = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(DefaultVertexFormats.POSITION_3F)
    //        .add(DefaultVertexFormats.TEX_2F).add(DefaultVertexFormats.TEX_2S).add(DefaultVertexFormats.TEX_2SB).add(DefaultVertexFormats.NORMAL_3B).add(DefaultVertexFormats.PADDING_1B).build());;
    private VertexBuffer cache = null;
    //private VertexFormat format = DefaultVertexFormats.NEW_ENTITY;
    private final float x1, x2, y1, y2;
    private final int width, height;
    private final int xTex, yTex;
    private float rotationOffsetX, rotationOffsetY, rotationOffsetZ;

    private float scaleX = 1, scaleY = 1, thickness = 1;

    private int displayList;
    private boolean isCompiled = false;
    public static final VertexFormat POSITION_TEX_NORMAL = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(DefaultVertexFormats.ELEMENT_POSITION).add(DefaultVertexFormats.ELEMENT_UV0).add(DefaultVertexFormats.ELEMENT_NORMAL).add(DefaultVertexFormats.ELEMENT_PADDING).build());

    private TexturedQuad[] quads = new TexturedQuad[6];

    public Model2DRenderer(Model base, float x, float y, int width, int height, int texWidth, int texHeight) {
        super(base.texWidth, base.texHeight, 0, 0);
        this.width = width;
        this.height = height;
        this.xTex = (int)x;
        this.yTex = (int)y;

        setTexSize(texWidth, texHeight);

        x1 = x / texWidth;
        y1 = y / texHeight;

        x2 = (x + width) / texWidth;
        y2 = (y + height) / texHeight;

        init();
    }

    public Model2DRenderer(Model modelBase, float x, float y, int width, int height) {
        this(modelBase, x, y, width, height, modelBase.texWidth, modelBase.texHeight);
    }

    public Model2DRenderer(Model modelBase, float x, float y, int width, int height, ResourceLocation location) {
        this(modelBase, x, y, width, height, modelBase.texWidth, modelBase.texHeight);
        init(location);
    }

    public Model2DRenderer(int x, int y, int width, int height, int texWidth, int texHeight) {
        super(texWidth, texHeight, x, y);
        this.width = width;
        this.height = height;
        this.xTex = x;
        this.yTex = y;

        setTexSize(texWidth, texHeight);

        x1 = (float)x / texWidth;
        y1 = (float)y / texHeight;

        x2 = ((float)x + width) / texWidth;
        y2 = ((float)y + height) / texHeight;

        init();
    }

    public void init(ResourceLocation location){
        if(location == null){
            return;
        }
        BufferedImage image = null;
        try(IResource resource = Minecraft.getInstance().getResourceManager().getResource(location)){
            image = ImageIO.read(resource.getInputStream());
        }
        catch(Exception e){
            Texture text = Minecraft.getInstance().getTextureManager().getTexture(location);
            if(text != null && text instanceof ImageDownloadAlt){
                try(FileInputStream input = new FileInputStream(((ImageDownloadAlt)text).cacheFile)){
                    image = ImageIO.read(input);
                } catch (Exception ee) {
                }
            }
            else{
            }
        }
        if(image == null){
            return;
        }

        int width = this.width * (image.getWidth() / 64);
        int height = this.height * (image.getHeight() / 64);
        int xTex = this.xTex * (image.getWidth() / 64);
        int yTex = this.yTex * (image.getHeight() / 64);

        List<PositionTextureVertex> list = new ArrayList<PositionTextureVertex>();
        list.add(new PositionTextureVertex(0f, 0f, 0f, x1, y2));
        list.add(new PositionTextureVertex(1f, 0f, 0f, x2, y2));
        list.add(new PositionTextureVertex(1f, 1f, 0f, x2, y1));
        list.add(new PositionTextureVertex(0f, 1f, 0f, x1, y1));
        quads[0] = new TexturedQuad(0.0F, 0.0F, 1.0F, list.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
//            list.add(new PositionTextureVertex(0f, 1f, -0.0625f, x1, y1));
//            list.add(new PositionTextureVertex(1f, 1f, -0.0625f, x2, y1));
//            list.add(new PositionTextureVertex(1f, 0f, -0.0625f, x2, y2));
//            list.add(new PositionTextureVertex(0f, 0f, -0.0625f, x1, y2));
        quads[1] = new TexturedQuad(0.0F, 0.0F, -1.0F, list.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
        List<PositionTextureVertex> list2 = new ArrayList<PositionTextureVertex>();
        for (int k = 0; k < width; ++k)
        {
            float f7 = (float)k / (float)width;
            float f8 = x1 + (x2 - x1) * f7 - 0.5F * (x1 - x2) / (float)width;
            float f9 = f7 + 1.0F / (float)width;

            boolean left = false, right = false;
            for(int n = 0; n < height; n++){
                if((image.getRGB(xTex + k, yTex + n) >> 24 & 255) < 128){
                    continue;
                }
                if(k + 1 < width && (image.getRGB(xTex + k + 1, yTex + n) >> 24 & 255) < 128){
                    right = true;
                }
                else if(k + 1 == width){
                    right = true;
                }
                if(k > 0 && (image.getRGB(xTex + k - 1, yTex + n) >> 24 & 255) < 128){
                    left = true;
                }
                else if(k == 0){
                    left = true;
                }
            }
            if(left){
                list.add(new PositionTextureVertex(f7, 0f, -0.0625f, f8, y2));
                list.add(new PositionTextureVertex(f7, 0f, 0f, f8, y2));
                list.add(new PositionTextureVertex(f7, 1f, 0f, f8, y1));
                list.add(new PositionTextureVertex(f7, 1f, -0.0625f, f8, y1));
            }

            if(right){
                list2.add(new PositionTextureVertex(f9, 1f, -0.0625f, f8, y1));
                list2.add(new PositionTextureVertex(f9, 1f, 0f, f8, y1));
                list2.add(new PositionTextureVertex(f9, 0f, 0f, f8, y2));
                list2.add(new PositionTextureVertex(f9, 0f, -0.0625f, f8, y2));
            }
        }
        quads[2] = new TexturedQuad(-1.0F, 0.0F, 0.0F, list.toArray(new PositionTextureVertex[0]));
        quads[3] = new TexturedQuad(1.0F, 0.0F, 0.0F, list2.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
        list2 = new ArrayList<PositionTextureVertex>();
        for (int k = 0; k < height; ++k)
        {
            float f7 = (float)k / (float)height;
            float f8 = y2 + (y1 - y2) * f7 - 0.5F * (y2 - y1) / (float)height;
            float f9 = f7 + 1.0F / (float)height;

            boolean top = false, bottom = false;
            for(int n = 0; n < width; n++){
                int m = height - k - 1;
                if((image.getRGB(xTex + n, yTex + m) >> 24 & 255) < 128){
                    continue;
                }
                if(m > 0 && (image.getRGB(xTex + n, yTex + m - 1) >> 24 & 255) < 128){
                    top = true;
                }
                else if(m == 0){
                    top = true;
                }
                if(m + 1 < height && (image.getRGB(xTex + n, yTex + m + 1) >> 24 & 255) < 128){
                    bottom = true;
                }
                else if(m + 1 == height){
                    bottom = true;
                }
            }

            if(bottom){
                list2.add(new PositionTextureVertex(1f, f7, 0f, x2, f8));
                list2.add(new PositionTextureVertex(0f, f7, 0f, x1, f8));
                list2.add(new PositionTextureVertex(0f, f7, -0.0625f, x1, f8));
                list2.add(new PositionTextureVertex(1f, f7, -0.0625f, x2, f8));
            }

            if(top){
                list.add(new PositionTextureVertex(0f, f9, 0f, x1, f8));
                list.add(new PositionTextureVertex(1f, f9, 0f, x2, f8));
                list.add(new PositionTextureVertex(1f, f9, -0.0625f, x2, f8));
                list.add(new PositionTextureVertex(0f, f9, -0.0625f, x1, f8));
            }
        }
        quads[4] = new TexturedQuad(0.0F, 1.0F, 0.0F, list.toArray(new PositionTextureVertex[0]));
        quads[5] = new TexturedQuad(0.0F, -1.0F, 0.0F, list2.toArray(new PositionTextureVertex[0]));
    }

    private void init(){
        this.addBox(0,0,0,1,1,1);
        List<PositionTextureVertex> list = new ArrayList<PositionTextureVertex>();
        list.add(new PositionTextureVertex(0f, 0f, 0f, x1, y2));
        list.add(new PositionTextureVertex(1f, 0f, 0f, x2, y2));
        list.add(new PositionTextureVertex(1f, 1f, 0f, x2, y1));
        list.add(new PositionTextureVertex(0f, 1f, 0f, x1, y1));
        quads[0] = new TexturedQuad(0.0F, 0.0F, 1.0F, list.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
        list.add(new PositionTextureVertex(0f, 1f, -0.0625f, x1, y1));
        list.add(new PositionTextureVertex(1f, 1f, -0.0625f, x2, y1));
        list.add(new PositionTextureVertex(1f, 0f, -0.0625f, x2, y2));
        list.add(new PositionTextureVertex(0f, 0f, -0.0625f, x1, y2));
        quads[1] = new TexturedQuad(0.0F, 0.0F, -1.0F, list.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
        List<PositionTextureVertex> list2 = new ArrayList<PositionTextureVertex>();
        for (int k = 0; k < width; ++k)
        {
            float f7 = (float)k / (float)width;
            float f8 = x1 + (x2 - x1) * f7 - 0.5F * (x1 - x2) / (float)width;
            float f9 = f7 + 1.0F / (float)width;

            list.add(new PositionTextureVertex(f7, 0f, -0.0625f, f8, y2));
            list.add(new PositionTextureVertex(f7, 0f, 0f, f8, y2));
            list.add(new PositionTextureVertex(f7, 1f, 0f, f8, y1));
            list.add(new PositionTextureVertex(f7, 1f, -0.0625f, f8, y1));

            list2.add(new PositionTextureVertex(f9, 1f, -0.0625f, f8, y1));
            list2.add(new PositionTextureVertex(f9, 1f, 0f, f8, y1));
            list2.add(new PositionTextureVertex(f9, 0f, 0f, f8, y2));
            list2.add(new PositionTextureVertex(f9, 0f, -0.0625f, f8, y2));
        }
        quads[2] = new TexturedQuad(-1.0F, 0.0F, 0.0F, list.toArray(new PositionTextureVertex[0]));
        quads[3] = new TexturedQuad(1.0F, 0.0F, 0.0F, list2.toArray(new PositionTextureVertex[0]));

        list = new ArrayList<PositionTextureVertex>();
        list2 = new ArrayList<PositionTextureVertex>();
        for (int k = 0; k < height; ++k)
        {
            float f7 = (float)k / (float)height;
            float f8 = y2 + (y1 - y2) * f7 - 0.5F * (y2 - y1) / (float)height;
            float f9 = f7 + 1.0F / (float)height;

            list.add(new PositionTextureVertex(0f, f9, 0f, x1, f8));
            list.add(new PositionTextureVertex(1f, f9, 0f, x2, f8));
            list.add(new PositionTextureVertex(1f, f9, -0.0625f, x2, f8));
            list.add(new PositionTextureVertex(0f, f9, -0.0625f, x1, f8));

            list2.add(new PositionTextureVertex(1f, f7, 0f, x2, f8));
            list2.add(new PositionTextureVertex(0f, f7, 0f, x1, f8));
            list2.add(new PositionTextureVertex(0f, f7, -0.0625f, x1, f8));
            list2.add(new PositionTextureVertex(1f, f7, -0.0625f, x2, f8));
        }
        quads[4] = new TexturedQuad(0.0F, 1.0F, 0.0F, list.toArray(new PositionTextureVertex[0]));
        quads[5] = new TexturedQuad(0.0F, -1.0F, 0.0F, list2.toArray(new PositionTextureVertex[0]));
    }

    @Override
    public void render(MatrixStack mstack, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if(!visible)
            return;
        float f = 0.0625f;
        mstack.pushPose();
        this.translateAndRotate(mstack);
        mstack.translate(rotationOffsetX * f, rotationOffsetY * f, rotationOffsetZ * f);
        mstack.scale(scaleX * width / height, scaleY, thickness);
        mstack.mulPose(Vector3f.XP.rotationDegrees(180));
        if(mirror){
            mstack.translate(0, 0, -1f * f);
            mstack.mulPose(Vector3f.YP.rotationDegrees(180));
        }
        renderModel(mstack.last().normal(), mstack.last().pose(), builder, light, overlay, red, green, blue, alpha);
        mstack.popPose();
    }

    public void render(MatrixStack mstack, int light, int overlay, float red, float green, float blue, float alpha, ResourceLocation resource) {
        if(!visible)
            return;
        RenderType rType = RenderType.entitySmoothCutout(resource);

        if(cache == null){
            cache = new VertexBuffer(DefaultVertexFormats.NEW_ENTITY);
            MatrixStack mmstack = new MatrixStack();
            mmstack.pushPose();
            IRenderTypeBuffer.Impl impl = typeBuffer.bufferSource();
            BufferBuilder bufferbuilder = (BufferBuilder) impl.getBuffer(rType);
            //renderModel(mmstack.last().pose(), bufferbuilder, light, overlay, red, green, blue, alpha);
            renderModel(mmstack.last().normal(), mmstack.last().pose(), bufferbuilder, light, overlay, 1, 1, 1, 1);
            bufferbuilder.end();
            this.cache.upload(bufferbuilder);
            mmstack.popPose();
            impl.endBatch();
        }
        mstack.pushPose();
        this.translateAndRotate(mstack);

        float f = 0.0625f;
        mstack.translate(rotationOffsetX * f, rotationOffsetY * f, rotationOffsetZ * f);
        mstack.scale(scaleX * width / height, scaleY, thickness);
        mstack.mulPose(Vector3f.XP.rotationDegrees(180));
        if(mirror){
            mstack.translate(0, 0, -1f * f);
            mstack.mulPose(Vector3f.YP.rotationDegrees(180));
        }
        MatrixStack.Entry entry = mstack.last();
        Matrix4f matrix = entry.pose();
        rType.setupRenderState();

        this.cache.bind();
        DefaultVertexFormats.NEW_ENTITY.setupBufferState(0L);
        RenderSystem.color4f(1, 0, 0, 1);
        this.cache.draw(matrix, 7);
        VertexBuffer.unbind();
        DefaultVertexFormats.NEW_ENTITY.clearBufferState();
        rType.clearRenderState();

        mstack.popPose();
    }

    public void renderModel(Matrix3f matrix3f, Matrix4f matrix4f , IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
        for(TexturedQuad quad : quads){
            Vector3f vector3f = quad.normal.copy();
            vector3f.transform(matrix3f);

            float nX = vector3f.x();
            float nY = vector3f.y();
            float nZ = vector3f.z();
            for(PositionTextureVertex vec : quad.vertices){
                Vector4f vector4f = new Vector4f(vec.pos);
                vector4f.transform(matrix4f);

                builder.vertex(vector4f.x(), vector4f.y(), vector4f.z());
                builder.color(red, green, blue, alpha);
                builder.uv(vec.u, vec.v);
                builder.overlayCoords(overlay);
                builder.uv2(light);
                builder.normal(nX, nY, nZ);
                builder.endVertex();
            }
        }
    }
    private void addVertex(IVertexBuilder builder, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
        Vector4f v = new Vector4f(x, y, z, 1.0F);
        v.transform(matrix);
        builder.vertex(v.x(), v.y(), v.z());
        builder.color(red, green, blue, alpha);
        builder.uv(texU, texV);
        builder.overlayCoords(overlayUV);
        builder.uv2(lightmapUV);
        builder.normal(normalX, normalY, normalZ);
        builder.endVertex();
        //builder.addVertex(v.getX(), v.getY(), v.getZ(), red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
    }

    public void setRotationOffset(float x, float y, float z){
        rotationOffsetX = x;
        rotationOffsetY = y;
        rotationOffsetZ = z;
    }

    public void setScale(float scale){
        this.scaleX = scale;
        this.scaleY = scale;
    }
    public void setScale(float x, float y){
        this.scaleX = x;
        this.scaleY = y;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

//    private void compile(float par1)
//    {
//        GlStateManager.pushMatrix();
//        this.displayList = GL11.glGenLists(1);
//        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
//        GlStateManager.translatef(rotationOffsetX * par1, rotationOffsetY * par1, rotationOffsetZ * par1);
//        GlStateManager.scalef(scaleX * width / height, scaleY, thickness);
//        GlStateManager.rotatef(180, 1F, 0F, 0F);
//        if(mirror){
//            GlStateManager.translatef(0, 0, -1f * par1);
//            GlStateManager.rotatef(180, 0, 1F, 0F);
//        }
//
//        renderItemIn2D(Tessellator.getInstance().getBuilder(), x1, y1, x2, y2, width, height, par1);
//        GL11.glEndList();
//        this.isCompiled = true;
//        GlStateManager.popMatrix();
//    }

    //    @OnlyIn(Dist.CLIENT)
//    private void compile(float par1) {
//        MatrixStack mstack = new MatrixStack();
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferbuilder = tessellator.getBuilder();
//
//		renderItemIn2D(Tessellator.getInstance().getBuilder(), x1, y1, x2, y2, width, height, par1);
//        bufferbuilder.finishDrawing();
//        this.cache.upload(bufferbuilder);
//    }
//
//    public static void renderItemIn2D(BufferBuilder worldrenderer, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_){
//        Tessellator tessellator = Tessellator.getInstance();
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//        worldrenderer.vertex(0.0D, 0.0D, 0.0D).uv(p_78439_1_, p_78439_4_).normal(0.0F, 0.0F, 1.0F).endVertex();
//        worldrenderer.vertex(1.0D, 0.0D, 0.0D).uv(p_78439_3_, p_78439_4_).normal(0.0F, 0.0F, 1.0F).endVertex();
//        worldrenderer.vertex(1.0D, 1.0D, 0.0D).uv(p_78439_3_, p_78439_2_).normal(0.0F, 0.0F, 1.0F).endVertex();
//        worldrenderer.vertex(0.0D, 1.0D, 0.0D).uv(p_78439_1_, p_78439_2_).normal(0.0F, 0.0F, 1.0F).endVertex();
//
//        tessellator.end();
//
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//        worldrenderer.vertex(0.0D, 1.0D, 0.0F - p_78439_7_).uv(p_78439_1_, p_78439_2_).normal(0.0F, 0.0F, -1.0F).endVertex();
//        worldrenderer.vertex(1.0D, 1.0D, 0.0F - p_78439_7_).uv(p_78439_3_, p_78439_2_).normal(0.0F, 0.0F, -1.0F).endVertex();
//        worldrenderer.vertex(1.0D, 0.0D, 0.0F - p_78439_7_).uv(p_78439_3_, p_78439_4_).normal(0.0F, 0.0F, -1.0F).endVertex();
//        worldrenderer.vertex(0.0D, 0.0D, 0.0F - p_78439_7_).uv(p_78439_1_, p_78439_4_).normal(0.0F, 0.0F, -1.0F).endVertex();
//        tessellator.end();
//        float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float)p_78439_5_;
//        float f6 = 0.5F * (p_78439_4_ - p_78439_2_) / (float)p_78439_6_;
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//
//        int k;
//        float f7;
//        float f8;
//
//        for (k = 0; k < p_78439_5_; ++k)
//        {
//            f7 = (float)k / (float)p_78439_5_;
//            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
//            worldrenderer.vertex(f7, 0.0D, 0.0F - p_78439_7_).uv(f8, p_78439_4_).normal(-1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f7, 0.0D, 0.0D).uv(f8, p_78439_4_).normal(-1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f7, 1.0D, 0.0D).uv(f8, p_78439_2_).normal(-1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f7, 1.0D, 0.0F - p_78439_7_).uv(f8, p_78439_2_).normal(-1.0F, 0.0F, 0.0F).endVertex();
//        }
//
//        tessellator.end();
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//        float f9;
//
//        for (k = 0; k < p_78439_5_; ++k)
//        {
//            f7 = (float)k / (float)p_78439_5_;
//            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
//            f9 = f7 + 1.0F / (float)p_78439_5_;
//            worldrenderer.vertex(f9, 1.0D, 0.0F - p_78439_7_).uv(f8, p_78439_2_).normal(1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f9, 1.0D, 0.0D).uv(f8, p_78439_2_).normal(1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f9, 0.0D, 0.0D).uv(f8, p_78439_4_).normal(1.0F, 0.0F, 0.0F).endVertex();
//            worldrenderer.vertex(f9, 0.0D, 0.0F - p_78439_7_).uv(f8, p_78439_4_).normal(1.0F, 0.0F, 0.0F).endVertex();
//        }
//
//        tessellator.end();
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//
//        for (k = 0; k < p_78439_6_; ++k)
//        {
//            f7 = (float)k / (float)p_78439_6_;
//            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
//            f9 = f7 + 1.0F / (float)p_78439_6_;
//            worldrenderer.vertex(0.0D, f9, 0.0D).uv(p_78439_1_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(1.0D, f9, 0.0D).uv(p_78439_3_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(1.0D, f9, 0.0F - p_78439_7_).uv(p_78439_3_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(0.0D, f9, 0.0F - p_78439_7_).uv(p_78439_1_, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
//        }
//
//        tessellator.end();
//        worldrenderer.begin(7, POSITION_TEX_NORMAL);
//
//        for (k = 0; k < p_78439_6_; ++k)
//        {
//            f7 = (float)k / (float)p_78439_6_;
//            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
//            worldrenderer.vertex(1.0D, f7, 0.0D).uv(p_78439_3_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(0.0D, f7, 0.0D).uv(p_78439_1_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(0.0D, f7, 0.0F - p_78439_7_).uv(p_78439_1_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
//            worldrenderer.vertex(1.0D, f7, 0.0F - p_78439_7_).uv(p_78439_3_, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
//        }
//
//        tessellator.end();
//    }


    static class TexturedQuad {
        public final PositionTextureVertex[] vertices;
        public final Vector3f normal;

        public TexturedQuad(float x, float y, float z, PositionTextureVertex[] vertices){
            this.normal = new Vector3f(x, y, z);
            this.vertices = vertices;
        }
    }

    static class PositionTextureVertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public PositionTextureVertex(float x, float y, float z, float u, float v){
            this.pos = new Vector3f(x, y, z);
            this.u = u;
            this.v = v;
        }
    }
}
