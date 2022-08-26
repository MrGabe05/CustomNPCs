package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;

public class PacketSync extends PacketBasic {
	private final int type;
    private final CompoundNBT data;
    private final boolean syncEnd;

    public PacketSync(int type, CompoundNBT data, boolean syncEnd) {
    	this.type = type;
    	this.data = data;
    	this.syncEnd = syncEnd;
    }

    public static void encode(PacketSync msg, PacketBuffer buf) {
    	buf.writeInt(msg.type);
    	buf.writeNbt(msg.data);
    	buf.writeBoolean(msg.syncEnd);
    }

    public static PacketSync decode(PacketBuffer buf) {
        return new PacketSync(buf.readInt(), buf.readNbt(), buf.readBoolean());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
	public void handle() {
        if(type == SyncType.FACTION){
            ListNBT list = data.getList("Data", 10);
            for(int i = 0; i < list.size(); i++)
            {
                Faction faction = new Faction();
                faction.readNBT(list.getCompound(i));
                FactionController.instance.factionsSync.put(faction.id, faction);
            }
            if(syncEnd){
                FactionController.instance.factions = FactionController.instance.factionsSync;
                FactionController.instance.factionsSync = new HashMap<Integer, Faction>();
            }
        }
        else if(type == SyncType.QUEST_CATEGORY){
            if(!data.isEmpty()){
                QuestCategory category = new QuestCategory();
                category.readNBT(data);
                QuestController.instance.categoriesSync.put(category.id, category);
            }
            if(syncEnd){
                HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
                for(QuestCategory category : QuestController.instance.categoriesSync.values()){
                    for(Quest quest : category.quests.values()){
                        quests.put(quest.id, quest);
                    }
                }
                QuestController.instance.categories = QuestController.instance.categoriesSync;
                QuestController.instance.quests = quests;
                QuestController.instance.categoriesSync = new HashMap<Integer, QuestCategory>();
            }
        }
        else if(type == SyncType.DIALOG_CATEGORY){
            if(!data.isEmpty()){
                DialogCategory category = new DialogCategory();
                category.readNBT(data);
                DialogController.instance.categoriesSync.put(category.id, category);
            }
            if(syncEnd){
                HashMap<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();
                for(DialogCategory category : DialogController.instance.categoriesSync.values()){
                    for(Dialog dialog : category.dialogs.values()){
                        dialogs.put(dialog.id, dialog);
                    }
                }
                DialogController.instance.categories = DialogController.instance.categoriesSync;
                DialogController.instance.dialogs = dialogs;
                DialogController.instance.categoriesSync = new HashMap<Integer, DialogCategory>();
            }
        }
//		else if(synctype == SyncType.RECIPE_NORMAL){
//			ListNBT list = compound.getList("Data", 10);
//            for(int i = 0; i < list.size(); i++)
//            {
//        		RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
//            	RecipeController.syncRecipes.put(recipe.id,recipe);
//            }
//	        if(syncEnd){
//				RecipeController.instance.globalRecipes = RecipeController.syncRecipes;
//	            RecipeController.instance.reloadGlobalRecipes();
//	            RecipeController.syncRecipes = new HashMap<Integer, RecipeCarpentry>();
//	        }
//		}
//		else if(synctype == SyncType.RECIPE_CARPENTRY){
//			ListNBT list = compound.getList("Data", 10);
//            for(int i = 0; i < list.size(); i++)
//            {
//        		RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
//            	RecipeController.syncRecipes.put(recipe.id,recipe);
//            }
//	        if(syncEnd){
//				RecipeController.instance.anvilRecipes = RecipeController.syncRecipes;
//	            RecipeController.syncRecipes = new HashMap<Integer, RecipeCarpentry>();
//	        }
//		}
        else if(type == SyncType.PLAYER_DATA){
            ClientProxy.playerData.setNBT(data);
        }
//            for(Map.Entry<Integer, String> entry : ItemScripted.Resources.entrySet()) {
//                ModelResourceLocation mrl = new ModelResourceLocation(entry.getValue(), "inventory");
//                Minecraft.getInstance().getItemRenderer().getItemModelShaper()getItemModelMesher().register(CustomItems.scripted_item, entry.getKey(), mrl);
//                ModelLoader.setCustomModelResourceLocation(CustomItems.scripted_item, entry.getKey(), mrl);
//            }
	}

    public void clientSync(boolean syncEnd) {
    }
}