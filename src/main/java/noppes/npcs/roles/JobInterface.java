package noppes.npcs.roles;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.EnumSet;

public abstract class JobInterface implements INPCJob{
	public static final JobInterface NONE = new JobInterface(null) {
		@Override
		public CompoundNBT save(CompoundNBT compound) {
			return null;
		}

		@Override
		public void load(CompoundNBT compound) {

		}

		@Override
		public int getType() {
			return JobType.NONE;
		}
	};

	public EntityNPCInterface npc;
	
	public boolean overrideMainHand = false;
	public boolean overrideOffHand = false;
	
	public JobInterface(EntityNPCInterface npc){
		this.npc = npc;
	}
	public abstract CompoundNBT save(CompoundNBT compound);
	public abstract void load(CompoundNBT compound);
	public void killed(){};
	public void delete(){};
	
	public boolean aiShouldExecute() {
		return false;
	}
	
	public boolean aiContinueExecute() {
		return aiShouldExecute();
	}
	public void aiStartExecuting() {}
	public void aiUpdateTask() {}
	public void reset() {}
	public void stop() {}
	
	public IItemStack getMainhand(){
		return null;
	}
	
	public IItemStack getOffhand(){
		return null;
	}
	
	public boolean isFollowing() {
		return false;
	}
	
	public EnumSet<Goal.Flag> getFlags() {
		return EnumSet.noneOf(Goal.Flag.class);
	}
	
	public ItemStack stringToItem(String s){
		if(s.isEmpty())
			return ItemStack.EMPTY;
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)));
	}
	
	public String itemToString(ItemStack item){
		if(item == null || item.isEmpty())
			return "";
		return ForgeRegistries.ITEMS.getKey(item.getItem()).toString();
	}
}
