package noppes.npcs.roles;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.server.SPacketDimensionTeleport;

public class RoleTransporter extends RoleInterface implements IRoleTransporter{
	
	public int transportId = -1;
	public String name;
	
	public RoleTransporter(EntityNPCInterface npc) {
		super(npc);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("TransporterId", transportId);
		return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		transportId = nbttagcompound.getInt("TransporterId");
		TransportLocation loc = getLocation();
		if(loc != null){
			name = loc.name;
		}
	}
	
	private int ticks = 10;
	@Override
	public boolean aiShouldExecute() {
		ticks--;
		if(ticks > 0)
			return false;
		ticks = 10;
		
		if(!hasTransport())
			return false;
		
		TransportLocation loc = getLocation();
		if(loc.type != 0)
			return false;

		List<PlayerEntity> inRange = npc.level.getEntitiesOfClass(PlayerEntity.class,npc.getBoundingBox().inflate(6D, 6D, 6D));
		for(PlayerEntity player :inRange){
			if(!npc.canNpcSee(player))
				continue;
			unlock(player, loc);			
		}
		return false;
		
	}

	@Override
	public void interact(PlayerEntity player) {
		if(hasTransport()){
			TransportLocation loc = getLocation();
			if(loc.type == 2){
				unlock(player, loc);	
			}
			NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTransporter, npc);
		}
	}
	
	public void transport(ServerPlayerEntity player, String location){
		TransportLocation loc = TransportController.getInstance().getTransport(location);
        PlayerTransportData playerdata = PlayerData.get(player).transportData;

		if(loc == null || !loc.isDefault() && !playerdata.transports.contains(loc.id))
			return;
		
		RoleEvent.TransporterUseEvent event = new RoleEvent.TransporterUseEvent (player, npc.wrappedNPC, loc);
        if(EventHooks.onNPCRole(npc, event))
			return;
		SPacketDimensionTeleport.teleportPlayer(player, loc.pos.getX(), loc.pos.getY(), loc.pos.getZ(), loc.dimension);
	}
	
	private void unlock(PlayerEntity player, TransportLocation loc){
		PlayerTransportData data = PlayerData.get(player).transportData;
		if(data.transports.contains(transportId))
			return;
		RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, npc.wrappedNPC);
        if(EventHooks.onNPCRole(npc, event))
			return;
        
		data.transports.add(transportId);
		npc.say(player, new Line("mailbox.gotmail"));
		Packets.send((ServerPlayerEntity)player, new PacketChatBubble(npc.getId(), new TranslationTextComponent("transporter.unlock", loc.name), true));
	}
	
	public TransportLocation getLocation(){
		if(npc.isClientSide())
			return null;
		return TransportController.getInstance().getTransport(transportId);
	}
	public boolean hasTransport(){
		TransportLocation loc = getLocation();
		return loc != null && loc.id == transportId;
	}

	public void setTransport(TransportLocation location) {
		transportId = location.id;
		name = location.name;
	}

	@Override
	public int getType() {
		return RoleType.TRANSPORTER;
	}

}
