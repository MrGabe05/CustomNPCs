package noppes.npcs.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class ImageDownloadAlt extends SimpleTexture
{
    private static final Logger logger = LogManager.getLogger();
    public final File cacheFile;
    private final String imageUrl;
    private CompletableFuture<?> future;
    private boolean textureUploaded;
    private final boolean fix64;
    private final Runnable r;

    public ImageDownloadAlt(File file, String url, ResourceLocation resource, boolean fix64, Runnable r) {
        super(resource);
        this.cacheFile = file;
        this.imageUrl = url;

        this.fix64 = fix64;
        this.r = r;
    }

    public void setImage(NativeImage image) {
        Minecraft.getInstance().execute(() -> {
            this.textureUploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> {
                    this.upload(image);
                });
            } else {
                this.upload(image);
            }
            r.run();
        });
    }

    private void upload(NativeImage imageIn) {
        TextureUtil.prepareImage(this.getId(), imageIn.getWidth(), imageIn.getHeight());
        imageIn.upload(0, 0, 0, true);
    }

    @Override
    public void load(IResourceManager resourceManager) throws IOException {
        if (!this.textureUploaded) {
            synchronized(this) {
                super.load(resourceManager);
                this.textureUploaded = true;

                if (this.future == null) {
                    if (this.cacheFile != null && this.cacheFile.isFile()) {
                        logger.debug("Loading http texture from local cache ({})", new Object[] {this.cacheFile});
                        NativeImage image = null;
                        try {
                            image = NativeImage.read(new FileInputStream(this.cacheFile));

                            this.setImage(parseUserSkin(image));
                        }
                        catch (IOException ioexception)
                        {
                            logger.error("Couldn't load skin " + this.cacheFile, ioexception);
                            this.loadTextureFromServer();
                        }
                    }
                    else {
                        this.loadTextureFromServer();
                    }
                }
            }
        }
    }

    protected void loadTextureFromServer()
    {
        this.future = CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            ImageDownloadAlt.logger.debug("Downloading http texture from {} to {}", new Object[] {ImageDownloadAlt.this.imageUrl, ImageDownloadAlt.this.cacheFile});

            try
            {
                connection = (HttpURLConnection)(new URL(ImageDownloadAlt.this.imageUrl)).openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
                connection.connect();
                String type = connection.getContentType();
                long size = connection.getContentLengthLong();

                if (connection.getResponseCode() / 100 != 2 || !type.equals("image/png") || size > 2000000 && !Minecraft.getInstance().hasSingleplayerServer())
                {
                    return;
                }
                InputStream inputstream;
                if (ImageDownloadAlt.this.cacheFile != null) {
                    FileUtils.copyInputStreamToFile(connection.getInputStream(), ImageDownloadAlt.this.cacheFile);
                    inputstream = new FileInputStream(ImageDownloadAlt.this.cacheFile);
                } else {
                    inputstream = connection.getInputStream();
                }

                try {
                    final NativeImage nativeimage = NativeImage.read(inputstream);
                    Minecraft.getInstance().execute(() -> this.setImage(parseUserSkin(nativeimage)));
                } catch (IOException ioexception) {
                }
                return;
            }
            catch (Exception exception)
            {
                ImageDownloadAlt.logger.error("Couldn't download http texture", exception);
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
        }, Util.backgroundExecutor());
    }

    public NativeImage parseUserSkin(NativeImage image){
        if(image.getHeight() != image.getWidth() && image.getWidth() / 2 != image.getHeight())
            throw new IllegalArgumentException("Invalid texture size: " + image.getWidth() + "x" + image.getHeight());
        int scale = image.getWidth() / 64;
        boolean lvt_2_1_ = image.getHeight() != image.getWidth();
        if (lvt_2_1_ && fix64) {
            NativeImage nativeImage = new NativeImage(64 * scale, 64 * scale, true);
            nativeImage.copyFrom(image);
            image.close();
            image = nativeImage;
            nativeImage.fillRect(0, 32 * scale, 64 * scale, 32 * scale, 0);
            nativeImage.copyRect(4 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(8 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(0, 20 * scale, 24 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(4 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(8 * scale, 20 * scale, 8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(12 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(48 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(40 * scale, 20 * scale, 0, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(48 * scale, 20 * scale, -16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(52 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
        }
        if (lvt_2_1_ && fix64) {
            setAreaTransparent(image, 32 * scale, 0, 64 * scale, 32 * scale);
        }

        //setAreaOpaque(image, 0, 16 * scale, 64 * scale, 32 * scale);
        //setAreaOpaque(image, 16 * scale, 48 * scale, 48 * scale, 64 * scale);
        return image;
    }

    private static void setAreaTransparent(NativeImage image, int x, int y, int width, int height) {
        for(int i = x; i < width; ++i) {
            for(int j = y; j < height; ++j) {
                int k = image.getPixelRGBA(i, j);
                if ((k >> 24 & 255) < 128) {
                    return;
                }
            }
        }

        for(int l = x; l < width; ++l) {
            for(int i1 = y; i1 < height; ++i1) {
                image.setPixelRGBA(l, i1, image.getPixelRGBA(l, i1) & 16777215);
            }
        }

    }

    private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height) {
        for(int i = x; i < width; ++i) {
            for(int j = y; j < height; ++j) {
                image.setPixelRGBA(i, j, image.getPixelRGBA(i, j) | -16777216);
            }
        }

    }
}