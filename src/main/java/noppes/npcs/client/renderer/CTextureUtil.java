package noppes.npcs.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public class CTextureUtil
{
    private static final IntBuffer DATA_BUFFER = GLAllocation.createByteBuffer(4194304 << 2).asIntBuffer();
    private static final float[] COLOR_GAMMAS;


    public static void deleteTexture(int textureId)
    {
        RenderSystem.deleteTexture(textureId);
    }

    public static int uploadTextureImage(int textureId, BufferedImage texture)
    {
        return uploadTextureImageAllocate(textureId, texture, false, false);
    }

    public static int uploadTextureImageAllocate(int textureId, BufferedImage texture, boolean blur, boolean clamp)
    {
        allocateTexture(textureId, texture.getWidth(), texture.getHeight());
        return uploadTextureImageSub(textureId, texture, 0, 0, blur, clamp);
    }

    public static void allocateTexture(int textureId, int width, int height)
    {
        allocateTextureImpl(textureId, 0, width, height);
    }

    public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        deleteTexture(glTextureId);
        bind(glTextureId);
        if (mipmapLevels >= 0)
        {
            RenderSystem.texParameter(3553, 33085, mipmapLevels);
            RenderSystem.texParameter(3553, 33082, 0);
            RenderSystem.texParameter(3553, 33083, mipmapLevels);
            GlStateManager._texParameter(3553, 34049, 0.0F);
        }

        for (int i = 0; i <= mipmapLevels; ++i)
        {
            GlStateManager._texImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_)
    {
        bind(textureId);
        uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_)
    {
        int i = p_110993_0_.getWidth();
        int j = p_110993_0_.getHeight();
        int k = 4194304 / i;
        int[] aint = new int[k * i];
        setTextureBlurred(p_110993_3_);
        setTextureClamped(p_110993_4_);

        for (int l = 0; l < i * j; l += i * k)
        {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            p_110993_0_.getRGB(0, i1, i, j1, aint, 0, i);
            copyToBuffer(aint, k1);
            GL11.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i, j1, 32993, 33639, DATA_BUFFER);
        }
    }

    private static void setTextureClamped(boolean p_110997_0_)
    {
        if (p_110997_0_)
        {
            RenderSystem.texParameter(3553, 10242, 10496);
            RenderSystem.texParameter(3553, 10243, 10496);
        }
        else
        {
            RenderSystem.texParameter(3553, 10242, 10497);
            RenderSystem.texParameter(3553, 10243, 10497);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_)
    {
        setTextureBlurMipmap(p_147951_0_, false);
    }

    private static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_)
    {
        if (p_147954_0_)
        {
            RenderSystem.texParameter(3553, 10241, p_147954_1_ ? 9987 : 9729);
            RenderSystem.texParameter(3553, 10240, 9729);
        }
        else
        {
            RenderSystem.texParameter(3553, 10241, p_147954_1_ ? 9986 : 9728);
            RenderSystem.texParameter(3553, 10240, 9728);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_)
    {
        copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_)
    {
        int[] aint = p_110994_0_;

//        if (Minecraft.getInstance().options.anaglyph)
//        {
//            aint = updateAnaglyph(p_110994_0_);
//        }

        DATA_BUFFER.clear();
        DATA_BUFFER.put(aint, p_110994_1_, p_110994_2_);
        DATA_BUFFER.position(0).limit(p_110994_2_);
    }

    static void bind(int p_94277_0_)
    {
        GlStateManager._bindTexture(p_94277_0_);
    }

    public static int[] updateAnaglyph(int[] p_110985_0_)
    {
        int[] aint = new int[p_110985_0_.length];

        for (int i = 0; i < p_110985_0_.length; ++i)
        {
            aint[i] = anaglyphColor(p_110985_0_[i]);
        }

        return aint;
    }

    public static int anaglyphColor(int p_177054_0_)
    {
        int i = p_177054_0_ >> 24 & 255;
        int j = p_177054_0_ >> 16 & 255;
        int k = p_177054_0_ >> 8 & 255;
        int l = p_177054_0_ & 255;
        int i1 = (j * 30 + k * 59 + l * 11) / 100;
        int j1 = (j * 30 + k * 70) / 100;
        int k1 = (j * 30 + l * 70) / 100;
        return i << 24 | i1 << 16 | j1 << 8 | k1;
    }


    static
    {


        COLOR_GAMMAS = new float[256];

        for (int i1 = 0; i1 < COLOR_GAMMAS.length; ++i1)
        {
            COLOR_GAMMAS[i1] = (float)Math.pow((double)((float)i1 / 255.0F), 2.2D);
        }
    }
}