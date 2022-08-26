package noppes.npcs.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomBlocks;
import noppes.npcs.api.constants.QuestType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.quests.QuestLocation;

import java.util.ArrayList;
import java.util.List;

public class TileWaypoint extends TileNpcEntity implements ITickableTileEntity{
	
	public String name = "";

	private int ticks = 10;
	private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
	private List<PlayerEntity> toCheck;
	public int range = 10;

	public TileWaypoint(){
		super(CustomBlocks.tile_waypoint);
	}

	@Override
	public void tick(){
		if(level.isClientSide || name.isEmpty())
			return;
		ticks--;
		if(ticks > 0)
			return;
		ticks = 10;

		toCheck = getPlayerList(range, range, range);
		toCheck.removeAll(recentlyChecked);

		List<PlayerEntity> listMax = getPlayerList(range + 10, range + 10, range + 10);
		recentlyChecked.retainAll(listMax);
		recentlyChecked.addAll(toCheck);
		
		if(toCheck.isEmpty())
			return;
		for(PlayerEntity player : toCheck){
			PlayerData pdata = PlayerData.get(player);
			PlayerQuestData playerdata = pdata.questData;
			for(QuestData data : playerdata.activeQuests.values()){
				if(data.quest.type != QuestType.LOCATION)
					continue;
				QuestLocation quest = (QuestLocation) data.quest.questInterface;
				if(quest.setFound(data, name)){
					player.sendMessage(new TranslationTextComponent(name).append(" ").append(new TranslationTextComponent("quest.found")), Util.NIL_UUID);

					playerdata.checkQuestCompletion(player, QuestType.LOCATION);
					pdata.updateClient = true;
				}
			}
		}
	}

	private List<PlayerEntity> getPlayerList(int x, int y, int z){
		return level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(worldPosition, worldPosition.offset(1, 1, 1)).inflate(x, y, z));
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		name = compound.getString("LocationName");
		range = compound.getInt("LocationRange");
		if(range < 2)
			range = 2;
	}

	@Override
    public CompoundNBT save(CompoundNBT compound){
		if(!name.isEmpty())
			compound.putString("LocationName", name);
		compound.putInt("LocationRange", range);
    	return super.save(compound);
	}
}
