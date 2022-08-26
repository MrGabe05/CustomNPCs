package noppes.npcs.packets.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleFollower;

import java.util.HashMap;

public class SPacketFollowerHire extends PacketServerBasic {

    public SPacketFollowerHire() {
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return true;
    }

    @Override
    public boolean requiresNpc(){
        return true;
    }

    public static void encode(SPacketFollowerHire msg, PacketBuffer buf) {
    }

    public static SPacketFollowerHire decode(PacketBuffer buf) {
        return new SPacketFollowerHire();
    }

    @Override
    protected void handle() {
        if(npc.role.getType() != RoleType.FOLLOWER)
            return;
        if(npc.role.getType() != RoleType.FOLLOWER)
            return;
        Container con = player.containerMenu;
        if(con == null || !(con instanceof ContainerNPCFollowerHire))
            return;

        ContainerNPCFollowerHire container = (ContainerNPCFollowerHire) con;
        RoleFollower role = (RoleFollower) npc.role;
        followerBuy(role, container.currencyMatrix, player, npc);
    }

    public static void followerBuy(RoleFollower role, IInventory currencyInv, ServerPlayerEntity player, EntityNPCInterface npc){
        ItemStack currency = currencyInv.getItem(0);
        if(currency == null || currency.isEmpty())
            return;
        HashMap<ItemStack,Integer> cd = new HashMap<ItemStack,Integer>();
        for(int slot = 0; slot < role.inventory.items.size(); slot++){
            ItemStack is = role.inventory.items.get(slot);
            if(is.isEmpty() || is.getItem() != currency.getItem())
                continue;
            int days = 1;
            if(role.rates.containsKey(slot))
                days = role.rates.get(slot);

            cd.put(is,days);
        }
        if(cd.size() == 0)
            return;
        int stackSize = currency.getCount();
        int days = 0;

        int possibleDays = 0;
        int possibleSize = stackSize;
        while(true){
            for(ItemStack item : cd.keySet()){
                int rDays = cd.get(item);
                int rValue = item.getCount();
                if(rValue > stackSize)
                    continue;
                int newStackSize = stackSize % rValue;
                int size = stackSize - newStackSize;
                int posDays = (size / rValue) * rDays;
                if(possibleDays <= posDays){
                    possibleDays = posDays;
                    possibleSize = newStackSize;
                }
            }
            if(stackSize == possibleSize)
                break;
            stackSize = possibleSize;
            days += possibleDays;
            possibleDays = 0;
        }

        RoleEvent.FollowerHireEvent event = new RoleEvent.FollowerHireEvent (player, npc.wrappedNPC, days);
        if(EventHooks.onNPCRole(npc, event))
            return;

        if(event.days == 0)
            return;

        if(stackSize <= 0)
            currencyInv.setItem(0, ItemStack.EMPTY);
        else
            currencyInv.setItem(0, currency.split(stackSize));



        npc.say(player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", days+""), player, npc)));
        role.setOwner(player);
        role.addDays(days);
    }
}