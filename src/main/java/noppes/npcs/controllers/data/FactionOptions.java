package noppes.npcs.controllers.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.controllers.FactionController;

public class FactionOptions {

	public boolean decreaseFactionPoints = false;
	public boolean decreaseFaction2Points = false;

	public int factionId = -1;
	public int faction2Id = -1;
	
	public int factionPoints = 100;
	public int faction2Points = 100;
	
    public void load(CompoundNBT compound)
    {
    	factionId = compound.getInt("OptionFactions1");
    	faction2Id = compound.getInt("OptionFactions2");

    	decreaseFactionPoints = compound.getBoolean("DecreaseFaction1Points");
    	decreaseFaction2Points = compound.getBoolean("DecreaseFaction2Points");

    	factionPoints = compound.getInt("OptionFaction1Points");
    	faction2Points = compound.getInt("OptionFaction2Points");
    }
	public CompoundNBT save(CompoundNBT par1CompoundNBT)
    {
		par1CompoundNBT.putInt("OptionFactions1", factionId);
		par1CompoundNBT.putInt("OptionFactions2", faction2Id);
		
		par1CompoundNBT.putInt("OptionFaction1Points", factionPoints);
		par1CompoundNBT.putInt("OptionFaction2Points", faction2Points);

		par1CompoundNBT.putBoolean("DecreaseFaction1Points", decreaseFactionPoints);
		par1CompoundNBT.putBoolean("DecreaseFaction2Points", decreaseFaction2Points);
		return par1CompoundNBT;
    }
	public boolean hasFaction(int id) {
		return factionId == id || faction2Id == id;
	}
	public void addPoints(PlayerEntity player) {
		if(factionId < 0 && faction2Id < 0)
			return;

		PlayerData playerdata = PlayerData.get(player);
		PlayerFactionData data = playerdata.factionData;	
		if(factionId >= 0 && factionPoints > 0)
			addPoints(player, data, factionId, decreaseFactionPoints, factionPoints);
		if(faction2Id >= 0 && faction2Points> 0)
			addPoints(player, data, faction2Id, decreaseFaction2Points, faction2Points);
		playerdata.updateClient = true;
	}
	
	private void addPoints(PlayerEntity player, PlayerFactionData data, int factionId, boolean decrease, int points) {
		Faction faction = FactionController.instance.getFaction(factionId);
		if(faction == null)
			return;
		
		if(!faction.hideFaction){
			String message = decrease?"faction.decreasepoints":"faction.increasepoints";
			player.sendMessage(new TranslationTextComponent(message, faction.name, points), Util.NIL_UUID);
		}
		
		data.increasePoints(player, factionId, decrease?-points:points);
		
	}
}
