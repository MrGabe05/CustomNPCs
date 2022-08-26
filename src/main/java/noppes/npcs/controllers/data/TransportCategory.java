package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Vector;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class TransportCategory {
	public int id = -1;
	public String title = "";
	public HashMap<Integer,TransportLocation> locations;
	public TransportCategory(){
		locations = new HashMap<Integer, TransportLocation>();
	}
	public Vector<TransportLocation> getDefaultLocations() {
		Vector<TransportLocation> list = new Vector<TransportLocation>();
		for(TransportLocation loc : locations.values())
			if(loc.isDefault())
				list.add(loc);
		return list;
	}

	public void readNBT(CompoundNBT compound){
    	id = compound.getInt("CategoryId");
    	title = compound.getString("CategoryTitle");

    	ListNBT locs = compound.getList("CategoryLocations", 10);
        if(locs == null || locs.size() == 0)
        	return;

        for(int ii = 0; ii < locs.size(); ii++)
        {
        	TransportLocation location = new TransportLocation();
        	location.readNBT(locs.getCompound(ii));
        	location.category = this;
        	locations.put(location.id,location);
        }
	}

	public void writeNBT(CompoundNBT compound){
    	compound.putInt("CategoryId", id);
    	compound.putString("CategoryTitle", title);
        ListNBT locs = new ListNBT();
    	for(TransportLocation location : locations.values()){
	        locs.add(location.writeNBT());
    	}
    	compound.put("CategoryLocations", locs);
	}
}
