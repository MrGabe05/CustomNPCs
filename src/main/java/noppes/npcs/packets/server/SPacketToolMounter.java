package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketToolMounter extends PacketServerBasic {

    private int type; //0:client clone, 1:server clone, 2:mob, 3:player mount
    private String name = "";
    private int tab = -1;
    private CompoundNBT compound = new CompoundNBT();


    private SPacketToolMounter(int type, String name, int tab, CompoundNBT compound) {
        this.type = type;
        this.name = name;
        this.tab = tab;
        this.compound = compound;
    }

    public SPacketToolMounter(int type, String name, int tab) {
        this.type = type;
        this.name = name;
        this.tab = tab;
    }

    public SPacketToolMounter(int type, CompoundNBT compound) {
        this.type = type;
        this.compound = compound;
    }

    public SPacketToolMounter() {
        this.type = 3;
    }

    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.mount;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        return CustomNpcsPermissions.TOOL_MOUNTER;
    }

    public static void encode(SPacketToolMounter msg, PacketBuffer buf) {
        buf.writeInt(msg.type);
        buf.writeUtf(msg.name);
        buf.writeInt(msg.tab);
        buf.writeNbt(msg.compound);
    }

    public static SPacketToolMounter decode(PacketBuffer buf) {
        return new SPacketToolMounter(buf.readInt(), buf.readUtf(32767), buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get(player);
        if(data.mounted == null)//shouldnt happen
            return;
        if(type == 0){
            Entity entity = EntityType.create(compound, player.level).get();
            entity.setPos(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            player.level.addFreshEntity(entity);
            entity.startRiding(data.mounted, true);
        }
        else if(type == 1){
            Entity entity = EntityType.create(ServerCloneController.Instance.getCloneData(player.createCommandSourceStack(), name, tab), player.level).get();
            entity.setPos(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            player.level.addFreshEntity(entity);
            entity.startRiding(data.mounted, true);
        }
        else if(type == 2){
            ResourceLocation loc = EntityUtil.getAllEntities(player.level, false).get(name);
            EntityType type = ForgeRegistries.ENTITIES.getValue(loc);
            Entity entity = type.create(player.level);
            if(entity == null)
                return;
            entity.setPos(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            player.level.addFreshEntity(entity);
            entity.startRiding(data.mounted, true);

        }
        else{
            player.startRiding(data.mounted, true);
        }
    }

}