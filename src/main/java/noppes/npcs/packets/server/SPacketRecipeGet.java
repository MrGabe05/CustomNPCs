package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketRecipeGet extends PacketServerBasic {
    private final int recipe;

    public SPacketRecipeGet(int recipe) {
        this.recipe = recipe;
    }

    public static void encode(SPacketRecipeGet msg, PacketBuffer buf) {
        buf.writeInt(msg.recipe);
    }

    public static SPacketRecipeGet decode(PacketBuffer buf) {
        return new SPacketRecipeGet(buf.readInt());
    }

    @Override
    protected void handle() {
        RecipeCarpentry r = RecipeController.instance.getRecipe(recipe);
        setRecipeGui(player,r);
    }

    public static void setRecipeGui(ServerPlayerEntity player, RecipeCarpentry recipe){
        if(recipe == null)
            return;
        if(!(player.containerMenu instanceof ContainerManageRecipes))
            return;

        ContainerManageRecipes container = (ContainerManageRecipes) player.containerMenu;
        container.setRecipe(recipe);

        Packets.send(player, new PacketGuiData(recipe.writeNBT()));
    }
}