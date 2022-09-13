package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.data.INPCRole;

public interface IRoleTransporter extends INPCRole {
	
	ITransportLocation getLocation();

	interface ITransportLocation{

		int getId();

		String getDimension();

		int getX();

		int getY();

		int getZ();

		String getName();

		/**
		 * Returns the unlock type
		 * @return 0:discover, 1:from start, 2:from start
		 */
        int getType();
		
	}
}
