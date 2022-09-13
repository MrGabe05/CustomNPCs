package noppes.npcs;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import noppes.npcs.entity.EntityNPCInterface;

public interface IChatMessages {

	void addMessage(String message, EntityNPCInterface npc);
	void renderMessages(MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, float scale, boolean inRange, int lightmapUV);
	
}
