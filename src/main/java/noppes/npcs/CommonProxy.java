package noppes.npcs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class CommonProxy {
	
	public boolean newVersionAvailable = false;
	public int revision = 4;

	public void load() {

	}

	public void postload() {
	}

	public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
		// TODO Auto-generated method stub
	}

	public void openGui(PlayerEntity player, EnumGuiType gui) {
		// TODO Auto-generated method stub
	}


	public void openGui(PlayerEntity player, Object guiscreen) {
		// TODO Auto-generated method stub
		
	}

	public void spawnParticle(LivingEntity player, String string, Object... ob) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasClient() {
		return false;
	}

	public PlayerEntity getPlayer() {
		return null;
	}

	public void spawnParticle(BasicParticleType type, double x, double y, double z,
							  double motionX, double motionY, double motionZ, float scale) {
	}

	public PlayerData getPlayerData(PlayerEntity player) {
		return null;
		
	}

    public Item.Properties getItemProperties() {
		return new Item.Properties().tab(CustomTabs.tab);
    }
}
