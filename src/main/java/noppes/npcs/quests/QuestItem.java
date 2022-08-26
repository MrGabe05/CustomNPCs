package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QuestItem extends QuestInterface{
	public NpcMiscInventory items = new NpcMiscInventory(3);
	public boolean leaveItems = false;
	public boolean ignoreDamage = false;
	public boolean ignoreNBT = false;

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		items.setFromNBT(compound.getCompound("Items"));
		leaveItems = compound.getBoolean("LeaveItems");
		ignoreDamage = compound.getBoolean("IgnoreDamage");
		ignoreNBT = compound.getBoolean("IgnoreNBT");
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		compound.put("Items", items.getToNBT());
		compound.putBoolean("LeaveItems", leaveItems);
		compound.putBoolean("IgnoreDamage", ignoreDamage);
		compound.putBoolean("IgnoreNBT", ignoreNBT);
	}

	@Override
	public boolean isCompleted(PlayerEntity player) {				
		List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
		for(ItemStack reqItem : questItems){			
			if(!NoppesUtilPlayer.compareItems(player, reqItem, ignoreDamage, ignoreNBT)){
				return false;
			}
		}		
		
		return true;
	}
	public Map<ItemStack, Integer> getProgressSet(PlayerEntity player){
		HashMap<ItemStack, Integer> map = new HashMap<ItemStack, Integer>();
		List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
		for(ItemStack item : questItems){
			if(NoppesUtilServer.IsItemStackNull(item))
				continue;
			map.put(item, 0);
		}
		for(int i = 0; i < player.inventory.getContainerSize(); i++) {
			ItemStack item = player.inventory.getItem(i);
			if(NoppesUtilServer.IsItemStackNull(item))
				continue;
			for(Entry<ItemStack, Integer> questItem : map.entrySet()){
				if(NoppesUtilPlayer.compareItems(questItem.getKey(), item, ignoreDamage, ignoreNBT)){
					map.put(questItem.getKey(), questItem.getValue() + item.getCount());
				}
			}
		}
		return map;
	}
	@Override
	public void handleComplete(PlayerEntity player) {
		if(leaveItems)
			return;
		for(ItemStack questitem : items.items){
			if(questitem.isEmpty())
				continue;
			int stacksize = questitem.getCount();
			for(int i = 0; i < player.inventory.getContainerSize(); i++) {
				ItemStack item = player.inventory.getItem(i);
				if(NoppesUtilServer.IsItemStackNull(item))
					continue;
				if(NoppesUtilPlayer.compareItems(item, questitem, ignoreDamage, ignoreNBT)){
					int size = item.getCount();
					if(stacksize - size >= 0){
						player.inventory.setItem(i, ItemStack.EMPTY);
						item.split(size);
					}
					else{
						item.split(stacksize);
					}
					stacksize -= size;
					if(stacksize <= 0)
						break;
				}
			}
		}
	}

	@Override
	public IQuestObjective[] getObjectives(PlayerEntity player) {
		List<IQuestObjective> list = new ArrayList<IQuestObjective>();
		List<ItemStack> questItems = NoppesUtilPlayer.countStacks(items, ignoreDamage, ignoreNBT);
		for(ItemStack stack : questItems) {
			if(!stack.isEmpty()) {
				list.add(new QuestItemObjective(player, stack));
			}
		}
		return list.toArray(new IQuestObjective[list.size()]);
	}

	
	class QuestItemObjective implements IQuestObjective{
		private final PlayerEntity player;
		private final ItemStack questItem;
		public QuestItemObjective(PlayerEntity player, ItemStack item) {
			this.player = player;
			this.questItem = item;
		}
		
		@Override
		public int getProgress() {
			int count = 0;
			for(int i = 0; i < player.inventory.getContainerSize(); i++) {
				ItemStack item = player.inventory.getItem(i);
				if(NoppesUtilServer.IsItemStackNull(item))
					continue;
				if(NoppesUtilPlayer.compareItems(questItem, item, ignoreDamage, ignoreNBT)){
					count += item.getCount();
				}
			}
			return ValueUtil.CorrectInt(count, 0, questItem.getCount());
		}

		@Override
		public void setProgress(int progress) {
			throw new CustomNPCsException("Cant set the progress of ItemQuests");
		}

		@Override
		public int getMaxProgress() {
			return questItem.getCount();
		}

		@Override
		public boolean isCompleted() {
			return NoppesUtilPlayer.compareItems(player, questItem, ignoreDamage, ignoreNBT);
		}

		@Override
		public String getText() {
			return getMCText().getString();
		}

		@Override
		public ITextComponent getMCText() {
			return new StringTextComponent("").append(questItem.getHoverName()).append(": " + getProgress() + "/" + getMaxProgress());
		}
	}

}
