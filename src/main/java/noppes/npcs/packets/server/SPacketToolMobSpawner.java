package noppes.npcs.packets.server;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.IPacketServer;

import java.io.IOException;

public class SPacketToolMobSpawner extends IPacketServer {
    private boolean createSpawner;
    private boolean server;
    private BlockPos pos;

    private String name = "";
    private int tab = -1;

    private CompoundNBT clone = new CompoundNBT();

    public SPacketToolMobSpawner(boolean createSpawner, BlockPos pos, String name, int tab) {
        this.server = true;
        this.createSpawner = createSpawner;
        this.pos = pos;
        this.name = name;
        this.tab = tab;
    }

    public SPacketToolMobSpawner(boolean createSpawner, BlockPos pos, CompoundNBT clone) {
        this.server = false;
        this.createSpawner = createSpawner;
        this.pos = pos;
        this.clone = clone;
    }

    public SPacketToolMobSpawner(boolean createSpawner, boolean server, BlockPos pos, String name, int tab, CompoundNBT clone) {
        this.createSpawner = createSpawner;
        this.server = server;
        this.pos = pos;
        this.name = name;
        this.tab = tab;
        this.clone = clone;
    }

    public SPacketToolMobSpawner(){

    }

    @Override
    public boolean toolAllowed(ItemStack item){
        return item.getItem() == CustomItems.cloner;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission(){
        if(createSpawner){
            return CustomNpcsPermissions.SPAWNER_CREATE;
        }
        else{
            return CustomNpcsPermissions.SPAWNER_MOB;
        }
    }

    @Override
    public void handle() {
        if(server)
            clone = ServerCloneController.Instance.getCloneData(player.createCommandSourceStack(), name, tab);
        if(clone == null || clone.isEmpty())
            return;
        if(createSpawner){
            createMobSpawner(pos, clone, player);
        }
        else{
            Entity entity = spawnClone(clone, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, player.level);
            if(entity == null){
                player.sendMessage(new StringTextComponent("Failed to create an entity out of your clone"), Util.NIL_UUID);
            }
        }
    }

    public static Entity spawnClone(CompoundNBT compound, double x, double y, double z, World world) {
        ServerCloneController.Instance.cleanTags(compound);
        compound.put("Pos", NBTTags.nbtDoubleList(x, y, z));
        Entity entity = EntityType.create(compound, world).get();
        if(entity == null){
            return null;
        }
        if(entity instanceof EntityNPCInterface){
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            npc.ais.setStartPos(npc.blockPosition());
        }
        world.addFreshEntity(entity);
        return entity;
    }

    public static void createMobSpawner(BlockPos pos, CompoundNBT comp, PlayerEntity player) {
        ServerCloneController.Instance.cleanTags(comp);

        if(comp.getString("id").equalsIgnoreCase("entityhorse")){
            player.sendMessage(new TranslationTextComponent("Currently you cant create horse spawner, its a minecraft bug"), Util.NIL_UUID);
            return;
        }

        player.level.setBlockAndUpdate(pos, Blocks.SPAWNER.defaultBlockState()); //setBlock
        MobSpawnerTileEntity tile = (MobSpawnerTileEntity) player.level.getBlockEntity(pos);
        AbstractSpawner logic = tile.getSpawner();

        if (!comp.contains("id", 8)){
            comp.putString("id", "Pig");
        }
        comp.putIntArray("StartPosNew", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        logic.setNextSpawnData(new WeightedSpawnerEntity(1, comp));
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        createSpawner = buf.readBoolean();
        server = buf.readBoolean();
        pos = buf.readBlockPos();
        name = buf.readUtf();
        tab = buf.readInt();
        clone = buf.readNbt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeBoolean(createSpawner);
        buf.writeBoolean(server);
        buf.writeBlockPos(pos);
        buf.writeUtf(name);
        buf.writeInt(tab);
        buf.writeNbt(clone);
    }
}