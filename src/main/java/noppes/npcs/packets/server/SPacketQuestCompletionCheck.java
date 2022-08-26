package noppes.npcs.packets.server;

import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

import java.util.ArrayList;
import java.util.List;

public class SPacketQuestCompletionCheck extends PacketServerBasic {
    private final int questId;

    public SPacketQuestCompletionCheck(int questId) {
        this.questId = questId;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    public static void encode(SPacketQuestCompletionCheck msg, PacketBuffer buf) {
        buf.writeInt(msg.questId);
    }

    public static SPacketQuestCompletionCheck decode(PacketBuffer buf) {
        return new SPacketQuestCompletionCheck(buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get(player);
        PlayerQuestData playerdata = data.questData;
        QuestData questdata = playerdata.activeQuests.get(questId);
        if(questdata == null)
            return;

        Quest quest = questdata.quest;
        if(!quest.questInterface.isCompleted(player))
            return;

        QuestEvent.QuestTurnedInEvent event = new QuestEvent.QuestTurnedInEvent(data.scriptData.getPlayer(), quest);
        event.expReward = quest.rewardExp;

        List<IItemStack> list = new ArrayList<IItemStack>();
        for(ItemStack item : quest.rewardItems.items){
            if(!item.isEmpty())
                list.add(NpcAPI.Instance().getIItemStack(item));
        }

        if(!quest.randomReward){
            event.itemRewards = list.toArray(new IItemStack[list.size()]);
        }
        else{
            if(!list.isEmpty()){
                event.itemRewards = new IItemStack[]{list.get(player.getRandom().nextInt(list.size()))};
            }
        }

        EventHooks.onQuestTurnedIn(data.scriptData, event);

        for(IItemStack item : event.itemRewards){
            if(item != null)
                NoppesUtilServer.GivePlayerItem(player, player, item.getMCItemStack());
        }

        quest.questInterface.handleComplete(player);
        if(event.expReward > 0){
            NoppesUtilServer.playSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.7F + 1.8F));

            player.giveExperiencePoints(event.expReward);
        }
        quest.factionOptions.addPoints(player);
        if(quest.mail.isValid()){
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName().getString(), quest.mail);
        }
        if(!quest.command.isEmpty()){
            FakePlayer cplayer = EntityNPCInterface.CommandPlayer;
            cplayer.setLevel(player.level);
            cplayer.setPos(player.getX(), player.getY(), player.getZ());
            NoppesUtilServer.runCommand(cplayer, "QuestCompletion", quest.command, player);
        }
        PlayerQuestController.setQuestFinished(quest, player);
        if(quest.hasNewQuest()) PlayerQuestController.addActiveQuest(quest.getNextQuest(), player);
    }
}