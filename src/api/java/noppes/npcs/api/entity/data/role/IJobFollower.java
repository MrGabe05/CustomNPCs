package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.INPCJob;

public interface IJobFollower extends INPCJob{

	String getFollowing();

	void setFollowing(String name);
	
	boolean isFollowing();

	ICustomNpc getFollowingNpc();
}
