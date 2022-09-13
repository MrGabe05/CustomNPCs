package noppes.npcs;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketEyeBlink;

public class ModelEyeData extends ModelPartData{
	private final Random r = new Random();
	public boolean glint = true;
	
	public int browThickness = 4;
	public int eyePos = 1;

	public int skinColor = 0xB4846D;
	public int browColor = 0x5b4934;	
	
	public long blinkStart = 0;
	
	public ModelEyeData(){
	    super("eyes");
		this.color = (new Integer[]{8368696, 16247203, 10526975, 10987431, 10791096, 4210943, 14188339, 11685080, 6724056, 15066419,
			8375321, 15892389, 10066329, 5013401, 8339378, 3361970, 6704179, 6717235, 10040115, 16445005, 6085589, 4882687, 55610})[r.nextInt(23)];
	}

	public CompoundNBT save(){
		CompoundNBT compound = super.save();
		compound.putBoolean("Glint", glint);
		
		compound.putInt("SkinColor", skinColor);
		compound.putInt("BrowColor", browColor);
		
		compound.putInt("PositionY", eyePos);
		compound.putInt("BrowThickness", browThickness);
		return compound;
	}
	
	public void load(CompoundNBT compound){
		if(compound.isEmpty())
			return;
		super.load(compound);
	    glint = compound.getBoolean("Glint");
	    
	    skinColor = compound.getInt("SkinColor");
	    browColor = compound.getInt("BrowColor");
	    
	    eyePos = compound.getInt("PositionY");
	    browThickness = compound.getInt("BrowThickness");
	}
	
	public boolean isEnabled(){
		return this.type >= 0;
	}
	
	public void update(EntityNPCInterface npc){
	    if(!isEnabled() || !npc.isAlive() || npc.isClientSide())
	       return;
		if(blinkStart < 0){
			blinkStart++;
		}
		else if(blinkStart == 0){
			if(r.nextInt(140) == 1){
				blinkStart = System.currentTimeMillis();
				if(npc != null){
					Packets.sendNearby(npc, new PacketEyeBlink(npc.getId()));
				}
			}
		}
		else if(System.currentTimeMillis() - blinkStart > 300){
			blinkStart = -20;
		}
	}
}
