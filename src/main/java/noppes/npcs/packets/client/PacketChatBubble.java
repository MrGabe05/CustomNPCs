package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.client.RenderChatMessages;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;


public class PacketChatBubble extends PacketBasic {
	private final int id;
    private final ITextComponent message;
    private final boolean showMessage;

    public PacketChatBubble(int id, ITextComponent message, boolean showMessage) {
    	this.id = id;
    	this.message = message;
    	this.showMessage = showMessage;
    }

    public static void encode(PacketChatBubble msg, PacketBuffer buf) {
    	buf.writeInt(msg.id);
        buf.writeComponent(msg.message);
        buf.writeBoolean(msg.showMessage);
    }

    public static PacketChatBubble decode(PacketBuffer buf) {
        return new PacketChatBubble(buf.readInt(), buf.readComponent(), buf.readBoolean());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if(entity == null || !(entity instanceof EntityNPCInterface))
            return;
        EntityNPCInterface npc = (EntityNPCInterface) entity;
        if(npc.messages == null)
            npc.messages = new RenderChatMessages();
        String text = NoppesStringUtils.formatText(message, player, npc);
        npc.messages.addMessage(text, npc);

        if(showMessage)
            player.sendMessage(new StringTextComponent(npc.getName().getString() + ": ").append(new TranslationTextComponent(text)), Util.NIL_UUID);
	}
}