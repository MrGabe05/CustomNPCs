package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.controllers.QuestController;

import javax.annotation.Nullable;

public class PlayerMail implements IInventory, IPlayerMail{
	public String subject = "";
	public String sender = "";
	public CompoundNBT message = new CompoundNBT();
	public long time = 0;
	public boolean beenRead = false;
	public int questId = -1;
    public NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
	
	public long timePast;
	
	public void readNBT(CompoundNBT compound) {
		subject = compound.getString("Subject");
		sender = compound.getString("Sender");
		time = compound.getLong("Time");
		beenRead = compound.getBoolean("BeenRead");
		message = compound.getCompound("Message");
		timePast = compound.getLong("TimePast");
		if(compound.contains("MailQuest"))
			questId = compound.getInt("MailQuest");

		items.clear();
        ListNBT nbttaglist = compound.getList("MailItems", 10);
        for (int i = 0; i < nbttaglist.size(); ++i)
        {
            CompoundNBT nbttagcompound1 = nbttaglist.getCompound(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.items.size())
            {
                this.items.set(j, ItemStack.of(nbttagcompound1));
            }
        }
	}

	public CompoundNBT writeNBT() {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("Subject", subject);
		compound.putString("Sender", sender);
		compound.putLong("Time", time);
		compound.putBoolean("BeenRead", beenRead);
		compound.put("Message", message);
		compound.putLong("TimePast", System.currentTimeMillis() - time);
		compound.putInt("MailQuest", questId);

		if(hasQuest())
			compound.putString("MailQuestTitle", getQuest().title);

        ListNBT nbttaglist = new ListNBT();

        for (int i = 0; i < this.items.size(); ++i)
        {
            if (!this.items.get(i).isEmpty())
            {
                CompoundNBT nbttagcompound1 = new CompoundNBT();
                nbttagcompound1.putByte("Slot", (byte)i);
                this.items.get(i).save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        compound.put("MailItems", nbttaglist);
		return compound;
	}
	
	public boolean isValid(){
		return !subject.isEmpty() && !message.isEmpty() && !sender.isEmpty();
	}

	public boolean hasQuest() {
		return getQuest() != null;
	}
	@Override
	public Quest getQuest() {
		return  QuestController.instance != null?QuestController.instance.quests.get(questId):null;
	}

	@Override
	public int getContainerSize() {
		return 4;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public ItemStack getItem(int i) {
        return this.items.get(i);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ItemStackHelper.removeItem(items, index, count);

        if (!itemstack.isEmpty())
        {
            this.setChanged();
        }

        return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int var1) {
        return items.set(var1, ItemStack.EMPTY);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);

        if (stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
	}

	@Override
	public void setChanged() {
		
	}

	@Override
	public boolean stillValid(PlayerEntity var1) {
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {
		
	}

	@Override
	public void stopOpen(PlayerEntity player) {
	}

	@Override
	public boolean canPlaceItem(int var1, ItemStack var2) {
		return true;
	}

	public PlayerMail copy() {
		PlayerMail mail = new PlayerMail();
		mail.readNBT(writeNBT());
		return mail;
	}


	@Override
    public boolean isEmpty(){
        for (int slot = 0; slot < this.getContainerSize(); slot++){
        	ItemStack item = getItem(slot);
            if (!NoppesUtilServer.IsItemStackNull(item) && !item.isEmpty()){
                return false;
            }
        }
        return true;
    }

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String[] getText(){
		List<String> list = new ArrayList<String>();
        ListNBT pages = message.getList("pages", 8);
        for (int i = 0; i < pages.size(); ++i){
        	list.add(pages.getString(i));
        }        
        return list.toArray(new String[list.size()]);
	}

	@Override
	public void setText(String[] pages){
		ListNBT list = new ListNBT();
		if(pages != null && pages.length > 0){
			for(String page : pages){
				list.add(StringNBT.valueOf(page));
			}
		}
		message.put("pages", list);
	}

	@Override
	public void setQuest(int id) {
		this.questId = id;
	}

	@Override
	public IContainer getContainer() {
		return NpcAPI.Instance().getIContainer(this);
	}

	@Override
	public void clearContent() {

	}
}
