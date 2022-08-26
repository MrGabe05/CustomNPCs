package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.IPacketServer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SPacketScriptSave extends IPacketServer {
    private int type; //0:npc, 1:block, 2:item, 3:forge, 4:player, 5:door
    private CompoundNBT data;

    public SPacketScriptSave(int type, CompoundNBT data) {
        this.type = type;
        this.data = data;
    }

    public SPacketScriptSave() {
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.scripter || item.getItem() == CustomBlocks.scripted_door_item || item.getItem() == CustomItems.wand ||
                item.getItem() == CustomItems.scripted_item || item.getItem() == CustomBlocks.scripted_item;
    }

    @Override
    public boolean requiresNpc(){
        return type == 0;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.TOOL_SCRIPTER;
    }

    @Override
    public void handle() {
        if(type == 0){
            npc.script.load(data);
            npc.updateAI = true;
            npc.script.lastInited = -1;
        }
        if(type == 1){
            PlayerData pd = PlayerData.get(player);
            TileEntity tile = player.level.getBlockEntity(pd.scriptBlockPos);
            if(!(tile instanceof TileScripted))
                return;
            TileScripted script = (TileScripted) tile;
            script.setNBT(data);
            script.lastInited = -1;
        }
        if(type == 2){
            if(!player.isCreative())
                return;
            ItemScriptedWrapper wrapper = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(player.getMainHandItem());
            wrapper.setMCNbt(data);
            wrapper.lastInited = -1;
            wrapper.saveScriptData();
            wrapper.updateClient = true;
            player.refreshContainer(player.containerMenu);
        }
        if(type == 3){
            ScriptController.Instance.setForgeScripts(data);
        }
        if(type == 4){
            ScriptController.Instance.setPlayerScripts(data);
        }
        if(type == 5){
            PlayerData pd = PlayerData.get(player);
            TileEntity tile = player.level.getBlockEntity(pd.scriptBlockPos);
            if(!(tile instanceof TileScriptedDoor))
                return;
            TileScriptedDoor script = (TileScriptedDoor) tile;
            script.setNBT(data);
            script.lastInited = -1;
        }
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeInt(type);
        buf.writeNbt(data);
    }
}