package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRecipeSave extends PacketServerBasic {
    private final CompoundNBT data;

    public SPacketRecipeSave(CompoundNBT data) {
        this.data = data;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.GLOBAL_RECIPE;
    }

    public static void encode(SPacketRecipeSave msg, PacketBuffer buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketRecipeSave decode(PacketBuffer buf) {
        return new SPacketRecipeSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        RecipeCarpentry recipe = RecipeCarpentry.load(data);
        RecipeController.instance.saveRecipe(recipe);
        SPacketRecipesGet.sendRecipeData(player, recipe.isGlobal?3:4);
        SPacketRecipeGet.setRecipeGui(player,recipe);
    }
}