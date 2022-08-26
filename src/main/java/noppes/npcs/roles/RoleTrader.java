package noppes.npcs.roles;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleTrader;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.NBTJsonUtil;

public class RoleTrader extends RoleInterface implements IRoleTrader{

    public String marketName = "";
	public NpcMiscInventory inventoryCurrency;
	public NpcMiscInventory inventorySold;

	public boolean ignoreDamage = false;
	public boolean ignoreNBT = false;
	
	public boolean toSave = false;
	
	public RoleTrader(EntityNPCInterface npc) {
		super(npc);
		inventoryCurrency = new NpcMiscInventory(36);
		inventorySold = new NpcMiscInventory(18);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
        nbttagcompound.putString("TraderMarket", marketName);
		writeNBT(nbttagcompound);
        if (toSave && !npc.isClientSide()) {
        	RoleTrader.save(this, marketName);
        }
        toSave = false;
        return nbttagcompound;
	}
	
	public CompoundNBT writeNBT(CompoundNBT nbttagcompound){
    	nbttagcompound.put("TraderCurrency", inventoryCurrency.getToNBT());
    	nbttagcompound.put("TraderSold", inventorySold.getToNBT());
        nbttagcompound.putBoolean("TraderIgnoreDamage", ignoreDamage);
        nbttagcompound.putBoolean("TraderIgnoreNBT", ignoreNBT);
        return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
        marketName = nbttagcompound.getString("TraderMarket");
        readNBT(nbttagcompound);
    	try {
			//Market.load(this, marketName);
		} catch (Exception ex) {
			LogWriter.except(ex);
			
		}
	}
	
	public void readNBT(CompoundNBT nbttagcompound){
		inventoryCurrency.setFromNBT(nbttagcompound.getCompound("TraderCurrency"));
		inventorySold.setFromNBT(nbttagcompound.getCompound("TraderSold"));
        ignoreDamage = nbttagcompound.getBoolean("TraderIgnoreDamage");
        ignoreNBT = nbttagcompound.getBoolean("TraderIgnoreNBT");		
	}
	
	@Override
	public void interact(PlayerEntity player) {
		npc.say(player, npc.advanced.getInteractLine());
        try {
        	RoleTrader.load(this, marketName);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
		NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTrader, npc);
	}

	public boolean hasCurrency(ItemStack itemstack) {
		if(itemstack == null)
			return false;
		for(ItemStack item : inventoryCurrency.items){
			if(!item.isEmpty() && NoppesUtilPlayer.compareItems(item, itemstack, ignoreDamage, ignoreNBT)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public IItemStack getSold(int slot){
		return NpcAPI.Instance().getIItemStack(inventorySold.getItem(slot));
	}
	
	@Override
	public IItemStack getCurrency1(int slot){
		return NpcAPI.Instance().getIItemStack(inventoryCurrency.getItem(slot));
	}
	
	@Override
	public IItemStack getCurrency2(int slot){
		return NpcAPI.Instance().getIItemStack(inventoryCurrency.getItem(slot+18));
	}
	
	@Override
	public void set(int slot, IItemStack currency, IItemStack currency2, IItemStack sold){
		if(sold == null)
			throw new CustomNPCsException("Sold item was null");
		
		if(slot >= 18 || slot < 0)
			throw new CustomNPCsException("Invalid slot: " + slot);
		
		if(currency == null){
			currency = currency2;
			currency2 = null;
		}

		if(currency != null)
			inventoryCurrency.items.set(slot, currency.getMCItemStack());
		else
			inventoryCurrency.items.set(slot, ItemStack.EMPTY);
		
		if(currency2 != null)
			inventoryCurrency.items.set(slot + 18, currency2.getMCItemStack());
		else
			inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
		
		inventorySold.items.set(slot, sold.getMCItemStack());
	}

	@Override
	public void remove(int slot){
		if(slot >= 18 || slot < 0)
			throw new CustomNPCsException("Invalid slot: " + slot);
		inventoryCurrency.items.set(slot, ItemStack.EMPTY);
		inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
		inventorySold.items.set(slot, ItemStack.EMPTY);
	}
	/**
	 * @param name The trader Linked Market name
	 */
	@Override
	public void setMarket(String name){
		marketName = name;
		RoleTrader.load(this, name);
	}
	
	/**
	 * @return Get the currently set Linked Market name
	 */
	@Override
	public String getMarket(){
		return marketName;
	}

    static public void save(RoleTrader r, String name) {
    	if(name.isEmpty())
    		return;
    	File file = getFile(name + "_new");
    	File file1 = getFile(name);
        
        try {
			NBTJsonUtil.SaveFile(file, r.writeNBT(new CompoundNBT()));
	        if(file1.exists()){
	        	file1.delete();
	        }
	        file.renameTo(file1);
		} catch (Exception e) {
			
		} 
    }


    static public void load(RoleTrader role, String name){
    	if(role.npc.level.isClientSide)
    		return;
    	File file = getFile(name);
    	if(!file.exists())
    		return;
    	
    	try {
			role.readNBT(NBTJsonUtil.LoadFile(file));
		} catch (Exception e) {
		} 
    }
    private static File getFile(String name){
    	File dir = new File(CustomNpcs.getWorldSaveDirectory(), "markets");
    	if(!dir.exists())
    		dir.mkdir();
    	return new File(dir, name.toLowerCase() + ".json");
    }

    public static void setMarket(EntityNPCInterface npc, String marketName) {
    	if(marketName.isEmpty())
    		return;
    	if(!getFile(marketName).exists())
    		save((RoleTrader) npc.role, marketName);
		
        load((RoleTrader) npc.role, marketName);
    }

	@Override
	public int getType() {
		return RoleType.TRADER;
	}
}
