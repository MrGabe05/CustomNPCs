package noppes.npcs.entity.data;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataInventory implements IInventory, INPCInventory{
	public Map<Integer, IItemStack> drops = new HashMap<Integer, IItemStack>();
	public Map<Integer, Integer> dropchance = new HashMap<Integer, Integer>();
	public Map<Integer, IItemStack> weapons = new HashMap<Integer, IItemStack>();
	public Map<Integer, IItemStack> armor = new HashMap<Integer, IItemStack>();
		
	private int minExp = 0;
	private int maxExp = 0;
	
	public int lootMode = 0;
	
	private EntityNPCInterface npc;
	
	public DataInventory(EntityNPCInterface npc){
		this.npc = npc;
	}
	
	public CompoundNBT save(CompoundNBT nbttagcompound){
		nbttagcompound.putInt("MinExp", minExp);
		nbttagcompound.putInt("MaxExp", maxExp);
		nbttagcompound.put("NpcInv", NBTTags.nbtIItemStackMap(drops));
		nbttagcompound.put("Armor", NBTTags.nbtIItemStackMap(armor));
		nbttagcompound.put("Weapons", NBTTags.nbtIItemStackMap(weapons));
		nbttagcompound.put("DropChance", NBTTags.nbtIntegerIntegerMap(dropchance));
		nbttagcompound.putInt("LootMode", lootMode);
		return nbttagcompound;
	}
	
	public void load(CompoundNBT nbttagcompound){
		minExp = nbttagcompound.getInt("MinExp");
		maxExp = nbttagcompound.getInt("MaxExp");
		drops = NBTTags.getIItemStackMap(nbttagcompound.getList("NpcInv", 10));
		armor = NBTTags.getIItemStackMap(nbttagcompound.getList("Armor", 10));
		weapons = NBTTags.getIItemStackMap(nbttagcompound.getList("Weapons", 10));
		dropchance = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("DropChance", 10));
		lootMode = nbttagcompound.getInt("LootMode");
	}

	@Override
	public IItemStack getArmor(int slot){
		return armor.get(slot);
	}

	@Override
	public void setArmor(int slot, IItemStack item) {
		armor.put(slot, item);
		npc.updateClient = true;
	}

	@Override
	public IItemStack getRightHand(){
		return weapons.get(0);
	}

	@Override
	public void setRightHand(IItemStack item){
		weapons.put(0, item);
		npc.updateClient = true;
	}

	@Override
	public IItemStack getProjectile(){
		return weapons.get(1);
	}

	@Override
	public void setProjectile(IItemStack item){
		weapons.put(1, item);
		npc.updateAI = true;
	}

	@Override
	public IItemStack getLeftHand(){
		return weapons.get(2);
	}

	@Override
	public void setLeftHand(IItemStack item){
		weapons.put(2, item);
		npc.updateClient = true;
	}
	
	@Override
	public IItemStack getDropItem(int slot){
		if(slot < 0 || slot > 8)
			throw new CustomNPCsException("Bad slot number: " + slot);
		
		IItemStack item = npc.inventory.drops.get(slot);
		if(item == null)
			return null;
		
		return NpcAPI.Instance().getIItemStack(item.getMCItemStack());
	}
	
	@Override
	public void setDropItem(int slot, IItemStack item, int chance){
		if(slot < 0 || slot > 8)
			throw new CustomNPCsException("Bad slot number: " + slot);

		chance = ValueUtil.CorrectInt(chance, 1, 100);
		
		if(item == null || item.isEmpty()){
			dropchance.remove(slot);
			drops.remove(slot);
		}
		else{
			dropchance.put(slot, chance);
			drops.put(slot, item);
		}
	}
	
	@Override
	public IItemStack[] getItemsRNG() {
		ArrayList<IItemStack> list = new ArrayList<IItemStack>();
		for (int i : drops.keySet()) {
			IItemStack item = drops.get(i);
			if(item == null || item.isEmpty())
				continue;
			int dchance = 100;
			if(dropchance.containsKey(i))
				dchance = dropchance.get(i);
			int chance = npc.level.random.nextInt(100) + dchance;
			if(chance >= 100){
				list.add(item);
			}
		}
		return list.toArray(new IItemStack[list.size()]);
	}
	

	public void dropStuff(NpcEvent.DiedEvent event, Entity entity, DamageSource damagesource) {
		ArrayList<ItemEntity> list = new ArrayList<ItemEntity>();
		if(event.droppedItems != null) {
			for (IItemStack item : event.droppedItems) {
				ItemEntity e = getItemEntity(item.getMCItemStack().copy());
				if(e != null)
					list.add(e);
			}
		}
		
		int enchant = 0;
        if (damagesource.getEntity() instanceof PlayerEntity){
        	enchant = EnchantmentHelper.getMobLooting((LivingEntity)damagesource.getEntity());
        }
        
        if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(npc, damagesource, list, enchant, true)){
            for (ItemEntity item : list){
            	if(lootMode == 1 && entity instanceof PlayerEntity){
            		PlayerEntity player = (PlayerEntity)entity;
            		item.setPickUpDelay(2);
            		npc.level.addFreshEntity(item);
            		ItemStack stack = item.getItem();
            		int i = stack.getCount();

            		if (player.inventory.add(stack)) {
                        entity.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            			player.take(item, i);

            			if (stack.getCount() <= 0) {
            				item.remove();
            			}
            		}
            	}
            	else
            		npc.level.addFreshEntity(item);
            }
        }
        int exp = event.expDropped;
        while (exp > 0){
            int var2 = ExperienceOrbEntity.getExperienceValue(exp);
            exp -= var2;

            if(lootMode == 1 && entity instanceof PlayerEntity){
                npc.level.addFreshEntity(new ExperienceOrbEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), var2));
            }
            else{
                npc.level.addFreshEntity(new ExperienceOrbEntity(npc.level, npc.getX(), npc.getY(), npc.getZ(), var2));
            }
        }
		
	}
	
	public ItemEntity getItemEntity(ItemStack itemstack) {
		if (itemstack == null || itemstack.isEmpty()) {
			return null;
		}
		ItemEntity entityitem = new ItemEntity(npc.level, npc.getX(),
				(npc.getY() - 0.30000001192092896D) + (double) npc.getEyeHeight(), npc.getZ(),
				itemstack);
		entityitem.setPickUpDelay(40);

		float f2 = npc.getRandom().nextFloat() * 0.5F;
		float f4 = npc.getRandom().nextFloat() * 3.141593F * 2.0F;
		entityitem.setDeltaMovement(-MathHelper.sin(f4) * f2, 0.20000000298023224D, MathHelper.cos(f4) * f2);

		return entityitem;
	}
	
	@Override
	public int getContainerSize() {
		return 15;
	}
	@Override
	public ItemStack getItem(int i) {
		if(i < 4)
			return ItemStackWrapper.MCItem(getArmor(i));
		else if(i < 7)
			return ItemStackWrapper.MCItem(weapons.get(i-4));
		else
			return ItemStackWrapper.MCItem(drops.get(i-7));
	}
	@Override
	public ItemStack removeItem(int par1, int limbSwingAmount) {
		int i =0;
        Map<Integer, IItemStack> var3;

        if (par1 >= 7)
        {
        	var3 = drops;
            par1 -= 7;
        }
        else if (par1 >= 4)
        {
        	var3 = weapons;
            par1 -= 4;
            i = 1;
        }
        else{
        	var3 = armor;
            i = 2;
        }
        
        ItemStack var4 = null;
        if (var3.get(par1) != null){

            if (var3.get(par1).getMCItemStack().getCount() <= limbSwingAmount){
                var4 = var3.get(par1).getMCItemStack();
                var3.put(par1, null);
            }
            else{
                var4 = var3.get(par1).getMCItemStack().split(limbSwingAmount);

                if (var3.get(par1).getMCItemStack().getCount() == 0){
                    var3.put(par1, null);
                }
            }
        }
        if(i == 1)
        	weapons = var3;
        if(i == 2)
        	armor = var3;
        if(var4 == null)
        	return ItemStack.EMPTY;
        return var4;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int par1) {
		int i = 0;
        Map<Integer, IItemStack> var2;

        if (par1 >= 7){
        	var2 = drops;
            par1 -= 7;
        }
        else if (par1 >= 4){
        	var2 = weapons;
            par1 -= 4;
            i = 1;
        }
        else{
        	var2 = armor;
            i = 2;
        }

        if (var2.get(par1) != null){
            ItemStack var3 = var2.get(par1).getMCItemStack();
            var2.put(par1, null);
            if(i == 1)
            	weapons = var2;
            if(i == 2)
            	armor = var2;
            return var3;
        }
        return ItemStack.EMPTY;
	}
	@Override
    public void setItem(int par1, ItemStack limbSwingAmountItemStack){
		int i = 0;
        Map<Integer, IItemStack> var3;

        if (par1 >= 7)
        {
        	var3 = drops;
            par1 -= 7;
        }
        else if (par1 >= 4)
        {
        	var3 = weapons;
            par1 -= 4;
            i = 1;
        }
        else{
        	var3 = armor;
            i = 2;
        }
    	var3.put(par1, NpcAPI.Instance().getIItemStack(limbSwingAmountItemStack));
        
        if(i == 1)
        	weapons = var3;
        if(i == 2)
        	armor = var3;
    }

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(PlayerEntity var1) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void setChanged() {
		
	}

	@Override
	public void startOpen(PlayerEntity player) {
		
	}
	@Override
	public void stopOpen(PlayerEntity player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getExpMin(){
		return npc.inventory.minExp;
	}
	
	@Override
	public int getExpMax(){
		return npc.inventory.maxExp;
	}

	@Override
	public int getExpRNG(){
		int exp = minExp;
		if (maxExp - minExp > 0)
			exp += npc.level.random.nextInt(maxExp - minExp);
		return exp;
	}

	@Override
	public void setExp(int min, int max){
		min = Math.min(min, max);
		
		npc.inventory.minExp = min;
		npc.inventory.maxExp = max;
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
	public void clearContent() {

	}
}
