package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.INPCRole;

public interface IRoleFollower extends INPCRole{

	int getDays();

	void addDays(int days);

	boolean getInfinite();

	void setInfinite(boolean infinite);

	boolean getGuiDisabled();

	void setGuiDisabled(boolean disabled);
	
	IPlayer getFollowing();
	
	void setFollowing(IPlayer player);
	
	boolean isFollowing();
	
	void reset();

	void setRefuseSoulstone(boolean refuse);

	boolean getRefuseSoulstone();

}
