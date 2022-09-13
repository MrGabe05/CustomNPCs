package noppes.npcs.api.entity.data;

import noppes.npcs.api.handler.data.IAvailability;

public interface IMark {

	IAvailability getAvailability();
	
	int getColor();
	
	void setColor(int color);
	
	int getType();
	
	void setType(int type);
	
	/**
	 * Calling this will send the changes you've made to the clients
	 */
    void update();
}
