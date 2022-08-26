package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncUpdate extends PacketBasic {
    private final int id;
	private final int type;
    private final CompoundNBT data;

    public PacketSyncUpdate(int id, int type, CompoundNBT data) {
        this.id = id;
    	this.type = type;
    	this.data = data;
    }

    public static void encode(PacketSyncUpdate msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.type);
    	buf.writeNbt(msg.data);
    }

    public static PacketSyncUpdate decode(PacketBuffer buf) {
        return new PacketSyncUpdate(buf.readInt(), buf.readInt(), buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        if(type == SyncType.FACTION){
            Faction faction = new Faction();
            faction.readNBT(data);
            FactionController.instance.factions.put(faction.id, faction);
        }
        else if(type == SyncType.DIALOG){
            DialogCategory category = DialogController.instance.categories.get(id);
            Dialog dialog = new Dialog(category);
            dialog.readNBT(data);
            DialogController.instance.dialogs.put(dialog.id, dialog);
            category.dialogs.put(dialog.id, dialog);
        }
        else if(type == SyncType.DIALOG_CATEGORY){
            DialogCategory category = new DialogCategory();
            category.readNBT(data);
            DialogController.instance.categories.put(category.id, category);
        }
        else if(type == SyncType.QUEST){
            QuestCategory category = QuestController.instance.categories.get(id);
            Quest quest = new Quest(category);
            quest.readNBT(data);
            QuestController.instance.quests.put(quest.id, quest);
            category.quests.put(quest.id, quest);
        }
        else if(type == SyncType.QUEST_CATEGORY){
            QuestCategory category = new QuestCategory();
            category.readNBT(data);
            QuestController.instance.categories.put(category.id, category);
        }
//		else if(synctype == SyncType.RECIPE_NORMAL){
//    		RecipeCarpentry recipe = RecipeCarpentry.load(compound);
//    		RecipeController.instance.globalRecipes.put(recipe.id, recipe);
//			RecipeController.instance.reloadGlobalRecipes();
//		}
//		else if(synctype == SyncType.RECIPE_CARPENTRY){
//    		RecipeCarpentry recipe = RecipeCarpentry.load(compound);
//    		RecipeController.instance.anvilRecipes.put(recipe.id, recipe);
//		}
	}

    public void clientSync(boolean syncEnd) {
    }
}