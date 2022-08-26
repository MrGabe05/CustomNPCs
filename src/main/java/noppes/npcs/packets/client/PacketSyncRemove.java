package noppes.npcs.packets.client;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRemove extends PacketBasic {
    private final int id;
	private final int type;

    public PacketSyncRemove(int id, int type) {
        this.id = id;
    	this.type = type;
    }

    public static void encode(PacketSyncRemove msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.type);
    }

    public static PacketSyncRemove decode(PacketBuffer buf) {
        return new PacketSyncRemove(buf.readInt(), buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        if(type == SyncType.FACTION){
            FactionController.instance.factions.remove(id);
        }
        else if(type == SyncType.DIALOG){
            Dialog dialog = DialogController.instance.dialogs.remove(id);
            if(dialog != null) {
                dialog.category.dialogs.remove(id);
            }
        }
        else if(type == SyncType.DIALOG_CATEGORY){
            DialogCategory category = DialogController.instance.categories.remove(id);
            if(category != null) {
                DialogController.instance.dialogs.keySet().removeAll(category.dialogs.keySet());
            }
        }
        else if(type == SyncType.QUEST){
            Quest quest = QuestController.instance.quests.remove(id);
            if(quest != null) {
                quest.category.quests.remove(id);
            }
        }
        else if(type == SyncType.QUEST_CATEGORY){
            QuestCategory category = QuestController.instance.categories.remove(id);
            if(category != null) {
                QuestController.instance.quests.keySet().removeAll(category.quests.keySet());
            }
        }
//		else if(synctype == SyncType.RECIPE_NORMAL){
//			RecipeController.instance.globalRecipes.remove(id);
//			RecipeController.instance.reloadGlobalRecipes();
//		}
//		else if(synctype == SyncType.RECIPE_CARPENTRY){
//			RecipeController.instance.anvilRecipes.remove(id);
//		}
	}

    public void clientSync(boolean syncEnd) {
    }
}