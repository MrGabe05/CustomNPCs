package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.ForgeEventHandler;
import noppes.npcs.NBTTags;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SPacketScriptGet extends PacketServerBasic {

    private final int type; //0:npc, 1:block, 2:item, 3:forge, 4:player, 5:door

    public SPacketScriptGet(int type) {
        this.type = type;
    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.scripted_item || item.getItem() == CustomItems.scripter || item.getItem() == CustomItems.wand ||
                item.getItem() == CustomBlocks.scripted_door_item || item.getItem() == CustomBlocks.scripted_item;
    }

    @Override
    public boolean requiresNpc(){
        return type == 0;
    }

    public static void encode(SPacketScriptGet msg, PacketBuffer buf) {
        buf.writeInt(msg.type);
    }

    public static SPacketScriptGet decode(PacketBuffer buf) {
        return new SPacketScriptGet(buf.readInt());
    }

    @Override
    protected void handle() {
        CompoundNBT compound = new CompoundNBT();
        if(type == 0){
            npc.script.save(compound);
            compound.put("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.npcScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if(type == 1){
            PlayerData data = PlayerData.get(player);
            TileEntity tile = player.level.getBlockEntity(data.scriptBlockPos);
            if(!(tile instanceof TileScripted))
                return;
            ((TileScripted) tile).getNBT(compound);
            compound.put("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.blockScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if(type == 2){
            ItemScriptedWrapper iw = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(player.getMainHandItem());
            compound = iw.getMCNbt();
            compound.put("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.itemScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if(type == 3){
            ScriptController.Instance.forgeScripts.save(compound);
            compound.put("Methods", NBTTags.nbtStringList(ForgeEventHandler.eventNames));
        }
        if(type == 4){
            ScriptController.Instance.playerScripts.save(compound);
            compound.put("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.playerScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        if(type == 5){
            PlayerData data = PlayerData.get(player);
            TileEntity tile = player.level.getBlockEntity(data.scriptBlockPos);
            if(!(tile instanceof TileScriptedDoor))
                return;
            ((TileScriptedDoor) tile).getNBT(compound);
            compound.put("Methods", NBTTags.nbtStringList(Arrays.stream(EnumScriptType.doorScripts).map(type -> type.function).collect(Collectors.toList())));
        }
        compound.put("Languages", ScriptController.Instance.nbtLanguages());
        Packets.send(player, new PacketGuiData(compound));
    }
}