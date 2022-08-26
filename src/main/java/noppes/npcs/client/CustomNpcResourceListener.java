package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.model.part.head.ModelHeadwear;
import noppes.npcs.mixin.ParticleManagerMixin;
import noppes.npcs.shared.client.util.TextureCache;

public class CustomNpcResourceListener implements IResourceManagerReloadListener {

	public static int DefaultTextColor = 0x404040;
	@Override
	public void onResourceManagerReload(IResourceManager var1) {
		if(var1 instanceof SimpleReloadableResourceManager){
//
//	        SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager();
//
//	        FolderPack pack = new FolderPack(CustomNpcs.Dir);
//	        simplemanager.add(pack);
	        
			try{
				DefaultTextColor = Integer.parseInt(I18n.get("customnpcs.defaultTextColor"),16);
			}
			catch(NumberFormatException e){
				DefaultTextColor = 0x404040;
			}
		}
		GuiTextureSelection.clear();
		createTextureCache();

		EntityEnderFX.portalSprite = ((ParticleManagerMixin)Minecraft.getInstance().particleEngine).getPacks().get(Registry.PARTICLE_TYPE.getKey(ParticleTypes.PORTAL));
		ModelHeadwear.clear();

	}

	private void createTextureCache(){
		enlargeTexture("acacia_planks");
		enlargeTexture("birch_planks");
		enlargeTexture("crimson_planks");
		enlargeTexture("dark_oak_planks");
		enlargeTexture("jungle_planks");
		enlargeTexture("oak_planks");
		enlargeTexture("spruce_planks");
		enlargeTexture("warped_planks");
		enlargeTexture("iron_block");
		enlargeTexture("diamond_block");
		enlargeTexture("stone");
		enlargeTexture("gold_block");
		enlargeTexture("white_wool");
	}
	
	private void enlargeTexture(String texture){
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        ResourceLocation location = new ResourceLocation("customnpcs:textures/cache/" + texture + ".png");
        Texture ob = manager.getTexture(location);
    	if(!(ob instanceof TextureCache)){
    		ob = new TextureCache(location, new ResourceLocation("textures/block/" + texture + ".png"));
    		manager.register(location, ob);
    	}
	}

}
