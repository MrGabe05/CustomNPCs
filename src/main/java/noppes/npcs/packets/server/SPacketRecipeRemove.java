package noppes.npcs.packets.server;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRecipeRemove extends PacketServerBasic {
    private final int recipe;

    public SPacketRecipeRemove(int recipe) {
        this.recipe = recipe;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_RECIPE;
    }

    public static void encode(SPacketRecipeRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.recipe);
    }

    public static SPacketRecipeRemove decode(PacketBuffer buf) {
        return new SPacketRecipeRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        RecipeCarpentry r = RecipeController.instance.delete(recipe);
        SPacketRecipesGet.sendRecipeData(player, r.isGlobal?3:4);
        SPacketRecipeGet.setRecipeGui(player, new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""),""));
    }
}