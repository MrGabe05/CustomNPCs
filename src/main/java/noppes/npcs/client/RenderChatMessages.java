package noppes.npcs.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.IChatMessages;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RenderChatMessages implements IChatMessages{
	private Map<Long,TextBlockClient> messages = new TreeMap<Long,TextBlockClient>();


    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderType type = RenderType.create("chatbubble", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, RenderType.State.builder().setCullState(new RenderState.CullState(true)).setDiffuseLightingState(new RenderState.DiffuseLightingState(true)).setLightmapState(new RenderState.LightmapState(true)).createCompositeState(true));
    protected static final RenderType typeDepth = RenderType.create("chatbubbledepth", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, false, true, RenderType.State.builder().setCullState(new RenderState.CullState(true)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(new RenderState.DiffuseLightingState(true)).setLightmapState(new RenderState.LightmapState(true)).setDepthTestState(new RenderState.DepthTestState("always", 519)).setAlphaState(new RenderState.AlphaState(0.003921569F)).createCompositeState(false));


	private int boxLength = 46;
	private float scale = 0.5f;
	
	private String lastMessage = "";
	private long lastMessageTime = 0;
	
	@Override
	public void addMessage(String message, EntityNPCInterface npc){
		if(!CustomNpcs.EnableChatBubbles)
			return;
		long time = System.currentTimeMillis();
		if(message.equals(lastMessage) && lastMessageTime + 5000 > time){
			return;
		}
		Map<Long,TextBlockClient> messages = new TreeMap<Long,TextBlockClient>(this.messages);
		messages.put(time, new TextBlockClient(message, (int) (boxLength * 4), true, Minecraft.getInstance().player, npc));

		if(messages.size() > 3){
			messages.remove(messages.keySet().iterator().next());
		}
		this.messages = messages;
		lastMessage = message;
		lastMessageTime = time;
	}    

	@Override
	public void renderMessages(MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, float textscale, boolean inRange, int lightmapUV){
		Map<Long,TextBlockClient> messages = getMessages();
		if(messages.isEmpty())
			return;
		if(inRange)
			render(matrixStack, typeBuffer, typeBuffer.getBuffer(typeDepth), textscale,false, lightmapUV);
		render(matrixStack, typeBuffer, typeBuffer.getBuffer(type), textscale, true, lightmapUV);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, IVertexBuilder ivertex, float textScale, boolean depth, int lightmapUV) {
        FontRenderer font = Minecraft.getInstance().font;
        float var14 = 0.016666668F * 1.6F;
        int size = 0;
        for(TextBlockClient block : messages.values())
            size += block.lines.size();
        Minecraft mc = Minecraft.getInstance();
        int textYSize = (int) (size * font.lineHeight * scale);

        matrixStack.pushPose();
        matrixStack.translate(0, textYSize * var14, 0);
        matrixStack.scale(textScale, textScale, textScale);

        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        matrixStack.scale(-var14, -var14, var14);
        //RenderSystem.rotatef(-mc.getEntityRenderDispatcher().cameraOrientation().j(), 0.0F, 1F, 0.0F);
        //RenderSystem.rotatef(mc.getEntityRenderDispatcher().cameraOrientation().i(), 1F, 0.0F, 0.0F);
        //RenderSystem.scalef(-var14, -var14, var14);
        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

//        if(depth){
//            RenderSystem.enableDepthTest();
//        }
//        else{
//            RenderSystem.disableDepthTest();
//        }

        int black = depth?0xFF000000:0xFF000000;
        int white = depth?0xBBFFFFFF:0x44FFFFFF;
        //RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        MatrixStack.Entry entry = matrixStack.last();
        Matrix4f matrix = entry.pose();
        drawRect(ivertex, matrix, lightmapUV, -boxLength - 2, -2, boxLength + 2, textYSize + 1, white, 0.11f);

        drawRect(ivertex, matrix, lightmapUV, -boxLength - 1, -3, boxLength + 1, -2, black, 0.1f); //top
        drawRect(ivertex, matrix, lightmapUV, -boxLength - 1, textYSize + 2, -1, textYSize + 1, black, 0.1f); //bottom1
        drawRect(ivertex, matrix, lightmapUV, 3, textYSize + 2, boxLength + 1, textYSize + 1, black, 0.1f); //bottom2
        drawRect(ivertex, matrix, lightmapUV, -boxLength - 3, -1, -boxLength - 2, textYSize, black, 0.1f); //left
        drawRect(ivertex, matrix, lightmapUV, boxLength + 3, -1, boxLength + 2, textYSize, black, 0.1f); //right

        drawRect(ivertex, matrix, lightmapUV, -boxLength - 2, -2, -boxLength - 1, -1, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, boxLength + 2, -2, boxLength + 1, -1, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, -boxLength - 2, textYSize + 1, -boxLength - 1, textYSize, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, boxLength + 2, textYSize + 1, boxLength + 1, textYSize, black, 0.1f);

        drawRect(ivertex, matrix, lightmapUV, 0, textYSize + 1, 3, textYSize + 4, white, 0.11f);
        drawRect(ivertex, matrix, lightmapUV, -1, textYSize + 4, 1, textYSize + 5, white, 0.11f);

        drawRect(ivertex, matrix, lightmapUV, -1, textYSize + 1, 0, textYSize + 4, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, 3, textYSize + 1, 4, textYSize + 3, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, 2, textYSize + 3, 3, textYSize + 4, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, 1, textYSize + 4, 2, textYSize + 5, black, 0.1f);
        drawRect(ivertex, matrix, lightmapUV, -2, textYSize + 4, -1, textYSize + 5, black, 0.1f);

        drawRect(ivertex, matrix, lightmapUV, -2, textYSize + 5, 1, textYSize + 6, black, 0.1f);

        matrixStack.scale(scale, scale, scale);
        int index = 0;
        for(TextBlockClient block : messages.values()){
            for(ITextComponent chat : block.lines){
                font.drawInBatch(chat, -font.width(chat) / 2, index * font.lineHeight, black, false, matrix, typeBuffer, !depth, 0, lightmapUV);
                //font.draw(matrixStack, message, -font.width(message) / 2, index * font.lineHeight, black);
                index++;
            }
        }
        matrixStack.popPose();
    }

    public void drawRect(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float x2, float y2, int color, float z)
    {
        float j1;

        if (x < x2)
        {
            j1 = x;
            x = x2;
            x2 = j1;
        }

        if (y < y2)
        {
            j1 = y;
            y = y2;
            y2 = j1;
        }

        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        //RenderSystem.color4f(1, 1, 1, 1);
        draw(ivertex, matrix, lightmapUV, x, y, z,f1, f2, f3);
        draw(ivertex, matrix, lightmapUV, x, y2, z,f1, f2, f3);
        draw(ivertex, matrix, lightmapUV, x2, y2, z,f1, f2, f3);
        draw(ivertex, matrix, lightmapUV, x2, y, z,f1, f2, f3);
    }

    private void draw(IVertexBuilder ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float z, float red, float green, float blue){
        Vector4f v = new Vector4f(x, y, z, 1.0F);
        v.transform(matrix);
        ivertex.vertex(v.x(), v.y(), v.z()).color(red, green, blue, 1).uv2(lightmapUV).endVertex();
    }
	
	private Map<Long,TextBlockClient> getMessages(){
		Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();
		long time = System.currentTimeMillis();
		for(Entry<Long, TextBlockClient> entry : this.messages.entrySet()){
			if(time > entry.getKey() + 10000)
				continue;
			messages.put(entry.getKey(), entry.getValue());
		}
		return this.messages = messages;
	}	
}
