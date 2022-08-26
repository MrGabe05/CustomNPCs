package noppes.npcs.client.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.model.ModelPonyArmor;
import noppes.npcs.entity.EntityNpcPony;

public class RenderNPCPony<T extends EntityNpcPony, M extends ModelPony<T>> extends RenderNPCInterface<T, M> {

    private ModelPony modelBipedMain;
    private ModelPonyArmor modelArmorChestplate;
    private ModelPonyArmor modelArmor;

    public RenderNPCPony(EntityRendererManager manager, M model) {
        super(manager, model, 0.5F);
        modelBipedMain = (ModelPony)model;
        modelArmorChestplate = new ModelPonyArmor(1.0F);
        modelArmor = new ModelPonyArmor(0.5F);
    }


	@Override
	public ResourceLocation getTextureLocation(T pony) {
		boolean check = pony.textureLocation == null || pony.textureLocation != pony.checked;
		ResourceLocation loc = super.getTextureLocation(pony);
    	if(check){
    		try (IResource resource = Minecraft.getInstance().getResourceManager().getResource(loc)){
				BufferedImage bufferedimage = ImageIO.read(resource.getInputStream());

				pony.isPegasus = false;
				pony.isUnicorn = false;
		        Color color = new Color(bufferedimage.getRGB(0, 0), true);
		        Color color1 = new Color(249, 177, 49, 255);
		        Color color2 = new Color(136, 202, 240, 255);
		        Color color3 = new Color(209, 159, 228, 255);
		        Color color4 = new Color(254, 249, 252, 255);
		        if(color.equals(color1))
		        {
		        }
		        if(color.equals(color2))
		        {
		        	pony.isPegasus = true;
		        }
		        if(color.equals(color3))
		        {
		        	pony.isUnicorn = true;
		        }
		        if(color.equals(color4))
		        {
		        	pony.isPegasus = true;
		        	pony.isUnicorn = true;
		        }
		        pony.checked = loc;
    		
    		} catch (IOException e) {
				
			}
    	}
		return loc;
	}

//    @Override
//    protected void rotatePlayer(EntityNPCInterface entityplayer, float f, float f1, float f2)
//    {
//        if(entityplayer.isAlive() && entityplayer.isSleeping())
//        {
//            RenderSystem.rotatef(entityplayer.orientation, 0.0F, 1.0F, 0.0F);
//            RenderSystem.translatef(-1.25F, -0.875F, 0.0F);
//            RenderSystem.rotatef(90F, 0.0F, 1.0F, 0.0F);
//        } else
//        {
//            RenderSystem.rotatef(180F - f1, 0.0F, 1.0F, 0.0F);
//            if(entityplayer.deathTime > 0)
//            {
//                float f3 = ((((float)entityplayer.deathTime + f2) - 1.0F) / 20F) * 1.6F;
//                f3 = MathHelper.sqrt(f3);
//                if(f3 > 1.0F)
//                {
//                    f3 = 1.0F;
//                }
//                RenderSystem.rotatef(f3 * getDeathMayRotation(entityplayer), 0.0F, 0.0F, 1.0F);
//            }
//        }
//    }
//    protected void renderSpecials(EntityNpcPony entityplayer, float f)
//    {
//        super.renderEquippedItems(entityplayer, f);
//        if(!entityplayer.isSleeping())
//        {
//            if(entityplayer.isUnicorn)
//            {
//                renderDrop(this.entityRenderDispatcher, entityplayer, modelBipedMain.unicornarm, 1.0F, 0.35F, 0.5375F, -0.45F);
//            } else
//            {
//                renderDrop(this.entityRenderDispatcher, entityplayer, modelBipedMain.RightArm, 1.0F, -0.0625F, 0.8375F, 0.0625F);
//            }
//        }
//    }
//
//
//    protected void renderDrop(EntityRendererManager rendermanager, EntityNpcPony entityplayer, ModelRenderer modelrenderer, float f, float f1, float f2, float f3)
//    {
//        ItemStack itemstack = entityplayer.getItemInHand();
//        if(itemstack == null)
//        {
//            return;
//        }
//        RenderSystem.pushMatrix();
//        if(modelrenderer != null)
//        {
//            modelrenderer.postRender(f * 0.0625F);
//        }
//        RenderSystem.translatef(f1, f2, f3);
//        if(itemstack.getItem() instanceof BlockItem && RenderBlocks.renderItemIn3d(Block.byItem(itemstack.getItem()).getRenderShape()))
//        {
//            RenderSystem.translatef(0.0F, 0.1875F, -0.3125F);
//            RenderSystem.rotatef(20F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(45F, 0.0F, 1.0F, 0.0F);
//            float f4 = 0.375F * f;
//            RenderSystem.scalef(f4, -f4, f4);
//        } else
//        if(itemstack.getItem() == Items.bow)
//        {
//            RenderSystem.translatef(0.0F, 0.125F, 0.3125F);
//            RenderSystem.rotatef(-20F, 0.0F, 1.0F, 0.0F);
//            float f5 = 0.625F * f;
//            RenderSystem.scalef(f5, -f5, f5);
//            RenderSystem.rotatef(-100F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(45F, 0.0F, 1.0F, 0.0F);
//        } else
//        if(itemstack.getItem().isFull3D())
//        {
//            if(itemstack.getItem().shouldRotateAroundWhenRendering())
//            {
//                RenderSystem.rotatef(180F, 0.0F, 0.0F, 1.0F);
//                RenderSystem.translatef(0.0F, -0.125F, 0.0F);
//            }
//            RenderSystem.translatef(0.0F, 0.1875F, 0.0F);
//            float f6 = 0.625F * f;
//            RenderSystem.scalef(f6, -f6, f6);
//            RenderSystem.rotatef(-100F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(45F, 0.0F, 1.0F, 0.0F);
//        } else
//        {
//            RenderSystem.translatef(0.25F, 0.1875F, -0.1875F);
//            float f7 = 0.375F * f;
//            RenderSystem.scalef(f7, f7, f7);
//            RenderSystem.rotatef(60F, 0.0F, 0.0F, 1.0F);
//            RenderSystem.rotatef(-90F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(20F, 0.0F, 0.0F, 1.0F);
//        }
//        if(itemstack.getItem() == Items.potionitem)
//        {
//            for (int j = 0; j <= 1; j++)
//            {
//                int k = itemstack.getItem().getColorFromItemStack(itemstack, j);
//                float f9 = (float)(k >> 16 & 0xff) / 255F;
//                float f10 = (float)(k >> 8 & 0xff) / 255F;
//                float f12 = (float)(k & 0xff) / 255F;
//                RenderSystem.color4f(f9, f10, f12, 1.0F);
//                entityRenderDispatcher.itemRendererer.renderItem(entityplayer, itemstack, j);
//            }
//        } else
//        {
//            rendermanager.itemRendererer.renderItem(entityplayer, itemstack, 0);
//        }
//        RenderSystem.popMatrix();
//    }

    @Override
	public void render(T pony, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        ItemStack itemstack = pony.getMainHandItem();
        //setRenderPassModel(modelBipedMain);
        
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = itemstack == null ? 0 : 1;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = pony.isCrouching();
        modelArmorChestplate.riding = modelArmor.riding = modelBipedMain.riding = false;
        modelArmorChestplate.isSleeping = modelArmor.isSleeping = modelBipedMain.isSleeping = pony.isSleeping();
        modelArmorChestplate.isUnicorn = modelArmor.isUnicorn = modelBipedMain.isUnicorn = pony.isUnicorn;
        modelArmorChestplate.isPegasus = modelArmor.isPegasus = modelBipedMain.isPegasus = pony.isPegasus;
        super.render(pony, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
        modelArmorChestplate.riding = modelArmor.riding = modelBipedMain.riding = false;
        modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
        modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;
    }

}
