package noppes.npcs.util;

import java.util.UUID;

import noppes.npcs.entity.EntityNPCInterface;

import com.mojang.authlib.GameProfile;

public class GameProfileAlt extends GameProfile{

	private static final UUID id = UUID.fromString("c9c843f8-4cb1-4c82-aa61-e264291b7bd6");

	public EntityNPCInterface npc;
	public GameProfileAlt() {
		super(id, "[customnpcs]");
	}

    @Override
    public String getName() {
    	if(npc == null)
    		return super.getName();
        return npc.getName().getString();
    }
    
    @Override
    public UUID getId(){
    	if(npc == null)
    		return id;
    	return npc.getUUID();
    }

    @Override
    public boolean isComplete() {
    	return false;
    }
}
