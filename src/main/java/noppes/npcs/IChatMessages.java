package noppes.npcs;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import noppes.npcs.entity.EntityNPCInterface;

public interface IChatMessages {

	public void addMessage(String message, EntityNPCInterface npc);
	public void renderMessages(MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, float scale, boolean inRange, int lightmapUV);
	
}
