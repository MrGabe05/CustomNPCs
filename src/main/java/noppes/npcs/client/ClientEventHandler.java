package noppes.npcs.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerSoundPlays;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.controllers.data.MarkData.Mark;
import noppes.npcs.schematics.SchematicWrapper;

public class ClientEventHandler {
	
	//private int displayList = -1;
	private final VertexBuffer cache = null;

	@SubscribeEvent
	public void onRenderTick(RenderWorldLastEvent event){
		MatrixStack matrixStack = event.getMatrixStack();
		IRenderTypeBuffer buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(TileBuilder.DrawPos == null || TileBuilder.DrawPos.distSqr(player.blockPosition()) > 1000000)
			return;
		TileEntity te = player.level.getBlockEntity(TileBuilder.DrawPos);
		if(te == null || !(te instanceof TileBuilder))
			return;
		TileBuilder tile = (TileBuilder) te;
        SchematicWrapper schem = tile.getSchematic();
        if(schem == null)
        	return;
		matrixStack.pushPose();
        //RenderHelper.enableStandardItemLighting();
		//matrixStack.translate(-1, 0, -1);
		Vector3d cpos = TileEntityRendererDispatcher.instance.camera.getPosition();
		matrixStack.translate(TileBuilder.DrawPos.getX() - cpos.x(), TileBuilder.DrawPos.getY() - cpos.y() + 0.01, TileBuilder.DrawPos.getZ() - cpos.z());
		matrixStack.translate(1, tile.yOffest, 1);
		if(tile.rotation % 2 == 0)
			drawSelectionBox(matrixStack, buffer, new BlockPos(schem.schema.getWidth(), schem.schema.getHeight(), schem.schema.getLength()));
		else
			drawSelectionBox(matrixStack, buffer, new BlockPos(schem.schema.getLength(), schem.schema.getHeight(), schem.schema.getWidth()));

		if(TileBuilder.Compiled){
			cache.draw(matrixStack.last().pose(), 7);
    	}
    	else{
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			//cache = new VertexBuffer(DefaultVertexFormats.BLOCK);
            try{
				//MatrixStack stack = new MatrixStack();
                for(int i = 0; i < schem.size && i < 25000; i++){

                	BlockState state = schem.schema.getBlockState(i);
                	if(state.getRenderShape() == BlockRenderType.INVISIBLE || state.getRenderShape() != BlockRenderType.MODEL)
                		continue;
					int posX = i % schem.schema.getWidth();
					int posZ = ((i - posX)/schem.schema.getWidth()) % schem.schema.getLength();
					int posY = (((i - posX)/schem.schema.getWidth()) - posZ) / schem.schema.getLength();
        			BlockPos pos = schem.rotatePos(posX, posY, posZ, tile.rotation);
					matrixStack.pushPose();
					//matrixStack.enableRescaleNormal();
					matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                    //Minecraft.getInstance().getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
					//matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
                    
        			state = schem.rotationState(state, tile.rotation);
        			try{
						IBakedModel ibakedmodel = dispatcher.getBlockModel(state);
						BufferBuilder builder = (BufferBuilder)buffer.getBuffer(RenderTypeLookup.getRenderType(state, false));
						dispatcher.getModelRenderer().renderModel(matrixStack.last(), builder, state, ibakedmodel, 1, 1, 1, 10000, OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
						//cache.upload(builder);
        			}
        			catch(Exception e){
						e.printStackTrace();
        			}
        			finally{
						matrixStack.popPose();
						//TileBuilder.Compiled = true;
        			}
        			
                }
            }
            catch(Exception e){
            	LogWriter.error("Error preview builder block", e);
            }
            finally{
				//RenderSystem.endList();
//                if(GL11.glGetError() == 0) {
//					TileBuilder.Compiled = true;
//				}
            }
    	
        }
        //RenderHelper.disableStandardItemLighting();
        //RenderSystem.translatef(-1, 0, -1);
		matrixStack.popPose();
	}

	@SubscribeEvent
	public void post(RenderLivingEvent.Post event){
		MarkData data = MarkData.get(event.getEntity());
		PlayerEntity player = Minecraft.getInstance().player;
		for(Mark m : data.marks){
			if(m.getType() != MarkType.NONE && m.availability.isAvailable(player)){
				MarkRenderer.render(event, m);
				break;
			}
		}
	}

	@SubscribeEvent
	public void playSound(PlaySoundEvent event){
		Minecraft mc = Minecraft.getInstance();
		if(mc == null || mc.level == null || mc.getConnection() == null){
			return;
		}
		ISound sound = event.getSound();
		Packets.sendServer(new SPacketPlayerSoundPlays(sound.getLocation().toString(), sound.getSource().getName(), sound.isLooping()));
	}

	public void drawSelectionBox(MatrixStack matrixStack, IRenderTypeBuffer buffer, BlockPos pos){
		matrixStack.pushPose();
		AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ZERO, pos);
		matrixStack.translate(0.001f, 0.001f, 0.001f);
		WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), bb, 1, 0, 0, 1);
		matrixStack.popPose();
    }
}
