package noppes.npcs.controllers.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMarkData;

import java.util.ArrayList;
import java.util.List;

public class MarkData implements ICapabilityProvider{
	
	@CapabilityInject(MarkData.class)
	public static Capability<MarkData> MARKDATA_CAPABILITY = null;
	private static final String NBTKEY = "cnpcmarkdata";
	private static final ResourceLocation CAPKEY = new ResourceLocation("customnpcs", "markdata");
	private LivingEntity entity;

	private final LazyOptional<MarkData> instance = LazyOptional.of(() -> this);
	public List<Mark> marks = new ArrayList<Mark>();

	public void setNBT(CompoundNBT compound){
		List<Mark> marks = new ArrayList<Mark>();
		ListNBT list = compound.getList("marks", 10);
		for(int i = 0; i < list.size(); i++){
			CompoundNBT c = list.getCompound(i);
			Mark m = new Mark();
			m.type = c.getInt("type");
			m.color = c.getInt("color");
			m.availability.load(c.getCompound("availability"));
			marks.add(m);
		}
		this.marks = marks;
	}
	
	public CompoundNBT getNBT() {
		CompoundNBT compound = new CompoundNBT();
		ListNBT list = new ListNBT();
		for(Mark m : marks){
			CompoundNBT c = new CompoundNBT();
			c.putInt("type", m.type);
			c.putInt("color", m.color);
			c.put("availability", m.availability.save(new CompoundNBT()));
			list.add(c);
		}
		compound.put("marks", list);
		return compound;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == MARKDATA_CAPABILITY)
			return instance.cast();
		return LazyOptional.empty();
	}

	public static void register(AttachCapabilitiesEvent<Entity> event) {
		event.addCapability(CAPKEY, new MarkData());
	}

	public void save() {
		entity.getPersistentData().put(NBTKEY, getNBT());
	}

	public IMark addMark(int type) {
		Mark m = new Mark();
		m.type = type;
		marks.add(m);
		if(!entity.level.isClientSide)
			syncClients();
		return m;
	}

	public IMark addMark(int type, int color) {
		Mark m = new Mark();
		m.type = type;
		m.color = color;
		marks.add(m);
		if(!entity.level.isClientSide)
			syncClients();
		return m;
	}

	private static final MarkData backup = new MarkData();
	public static MarkData get(LivingEntity entity) {
		MarkData data = entity.getCapability(MARKDATA_CAPABILITY, null).orElse(backup);
		if(data.entity == null){
			data.entity = entity;
			data.setNBT(entity.getPersistentData().getCompound(NBTKEY));
		}
		return data;
	}

	public void syncClients() {
		Packets.sendAll( new PacketMarkData(entity.getId(), getNBT()));
	}
	
	public class Mark implements IMark{	
		public int type = MarkType.NONE;
		
		public Availability availability = new Availability();
		
		public int color = 0xFFED51;

		@Override
		public IAvailability getAvailability() {
			return availability;
		}

		@Override
		public int getColor() {
			return color;
		}

		@Override
		public void setColor(int color) {
			this.color = color;
		}

		@Override
		public int getType() {
			return type;
		}

		@Override
		public void setType(int type) {
			this.type = type;
		}

		@Override
		public void update() {
			MarkData.this.syncClients();
		}
	}
}
