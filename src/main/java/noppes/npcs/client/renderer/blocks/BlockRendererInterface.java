package noppes.npcs.client.renderer.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public abstract class BlockRendererInterface<T extends TileEntity> extends TileEntityRenderer<T> {
	protected static final ResourceLocation Stone = new ResourceLocation("customnpcs","textures/cache/stone.png");
	protected static final ResourceLocation Iron = new ResourceLocation("customnpcs","textures/cache/iron_block.png");
	protected static final ResourceLocation Gold = new ResourceLocation("customnpcs","textures/cache/gold_block.png");
	protected static final ResourceLocation Diamond = new ResourceLocation("customnpcs","textures/cache/diamond_block.png");

	protected static final ResourceLocation PlanksOak = new ResourceLocation("customnpcs","textures/cache/oak_planks.png");
	protected static final ResourceLocation PlanksBigOak = new ResourceLocation("customnpcs","textures/cache/dark_oak_planks.png");
	protected static final ResourceLocation PlanksSpruce = new ResourceLocation("customnpcs","textures/cache/spruce_planks.png");
	protected static final ResourceLocation PlanksBirch = new ResourceLocation("customnpcs","textures/cache/birch_planks.png");
	protected static final ResourceLocation PlanksAcacia = new ResourceLocation("customnpcs","textures/cache/acacia_planks.png");
	protected static final ResourceLocation PlanksJungle = new ResourceLocation("customnpcs","textures/cache/jungle_planks.png");

	//protected static final ResourceLocation Steel = new ResourceLocation("customnpcs","textures/models/Steel.png");
	
    public static float colorTable[][] = {
        {
            1.0F, 1.0F, 1.0F
        }, {
            0.95F, 0.7F, 0.2F
        }, {
            0.9F, 0.5F, 0.85F
        }, {
            0.6F, 0.7F, 0.95F
        }, {
            0.9F, 0.9F, 0.2F
        }, {
            0.5F, 0.8F, 0.1F
        }, {
            0.95F, 0.7F, 0.8F
        }, {
            0.3F, 0.3F, 0.3F
        }, {
            0.6F, 0.6F, 0.6F
        }, {
            0.3F, 0.6F, 0.7F
        }, {
            0.7F, 0.4F, 0.9F
        }, {
            0.2F, 0.4F, 0.8F
        }, {
            0.5F, 0.4F, 0.3F
        }, {
            0.4F, 0.5F, 0.2F
        }, {
            0.8F, 0.3F, 0.3F
        }, {
            0.1F, 0.1F, 0.1F
        }
    };

    public BlockRendererInterface(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public boolean playerTooFar(TileEntity tile){
		Minecraft mc = Minecraft.getInstance();
        double d6 = mc.getCameraEntity().getX() - tile.getBlockPos().getX();
        double d7 = mc.getCameraEntity().getY() - tile.getBlockPos().getY();
        double d8 = mc.getCameraEntity().getZ() - tile.getBlockPos().getZ();

        return d6 * d6 + d7 * d7 + d8 * d8 > specialRenderDistance() * specialRenderDistance();
	}
	
	public int specialRenderDistance(){
		return 20;
	}
    public void setWoodTexture(int meta){
    	TextureManager manager = Minecraft.getInstance().getTextureManager();
        if(meta == 1)
        	manager.bind(PlanksSpruce);
        else if(meta == 2)
        	manager.bind(PlanksBirch);
        else if(meta == 3)
        	manager.bind(PlanksJungle);
        else if(meta == 4)
        	manager.bind(PlanksAcacia);
        else if(meta == 5)
        	manager.bind(PlanksBigOak);
        else
        	manager.bind(PlanksOak);
    }

    public static void setMaterialTexture(int meta){
    	TextureManager manager = Minecraft.getInstance().getTextureManager();
        if(meta == 1)
        	manager.bind(Stone);
        else if(meta == 2)
        	manager.bind(Iron);
        else if(meta == 3)
        	manager.bind(Gold);
        else if(meta == 4)
        	manager.bind(Diamond);
        else
        	manager.bind(PlanksOak);
    }
}
