package noppes.npcs.shared.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.renderer.CTextureUtil;
import noppes.npcs.shared.common.util.LogWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class TextureCache extends SimpleTexture{
	private final ResourceLocation original;

	public TextureCache(ResourceLocation location, ResourceLocation original) {
		super(location);
		this.original = original;
	}

	@Override
	public void load(IResourceManager p_195413_1_) throws IOException {
		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		try(IResource r = manager.getResource(original)){
			BufferedImage bufferedimage = ImageIO.read(r.getInputStream());
			int i = bufferedimage.getWidth();
			int j = bufferedimage.getHeight();

			BufferedImage bufferedImage = new BufferedImage(i * 4, j * 2, BufferedImage.TYPE_INT_RGB);
			Graphics g = bufferedImage.getGraphics();
			g.drawImage(bufferedimage, 0, 0, null);
			g.drawImage(bufferedimage, i, 0, null);
			g.drawImage(bufferedimage, i * 2, 0, null);
			g.drawImage(bufferedimage, i * 3, 0, null);
			g.drawImage(bufferedimage, 0, i, null);
			g.drawImage(bufferedimage, i, j, null);
			g.drawImage(bufferedimage, i * 2, j, null);
			g.drawImage(bufferedimage, i * 3, j, null);

			CTextureUtil.uploadTextureImage(super.getId(), bufferedImage);
			//textureUploaded = false;
		}
		catch(Exception e){
			LogWriter.error("Failed caching texture: " + location, e);
		}
	}
}
