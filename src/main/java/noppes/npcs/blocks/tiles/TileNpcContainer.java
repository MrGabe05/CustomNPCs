package noppes.npcs.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;


public abstract class TileNpcContainer extends TileColorable implements IInventory {
    public final NonNullList<ItemStack> inventoryContents;
    public int playerUsing = 0;
    
    public TileNpcContainer(TileEntityType<?> type){
        super(type);
        inventoryContents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

	@Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        ListNBT nbttaglist = compound.getList("Items", 10);

        inventoryContents.clear();
        for (int i = 0; i < nbttaglist.size(); ++i)
        {
            CompoundNBT nbttagcompound1 = nbttaglist.getCompound(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventoryContents.size())
            {
                this.inventoryContents.set(j, ItemStack.of(nbttagcompound1));
            }
        }
    }

	@Override
    public CompoundNBT save(CompoundNBT compound){
        ListNBT nbttaglist = new ListNBT();

        for (int i = 0; i < this.inventoryContents.size(); ++i)
        {
            if (!this.inventoryContents.get(i).isEmpty())
            {
                CompoundNBT tagCompound = new CompoundNBT();
                tagCompound.putByte("Slot", (byte) i);
                this.inventoryContents.get(i).save(tagCompound);
                nbttaglist.add(tagCompound);
            }
        }

        compound.put("Items", nbttaglist);
    	return super.save(compound);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.playerUsing = type;
            return true;
        }
        else {
            return super.triggerEvent(id, type);
        }
    }

	@Override
	public int getContainerSize() {
		return 54;
	}

	@Override
	public ItemStack getItem(int index) {
        return inventoryContents.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ItemStackHelper.removeItem(inventoryContents, index, count);

        if (!itemstack.isEmpty())
        {
            this.setChanged();
        }

        return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
        return inventoryContents.set(index, ItemStack.EMPTY);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);

        if (stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
	}
	
	public abstract ITextComponent getName();


	@Override
	public boolean stillValid(PlayerEntity player) {
        return (player.removed || this.level.getBlockEntity(this.worldPosition) == this) && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void startOpen(PlayerEntity player) {
		playerUsing++;
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		playerUsing--;
	}

	@Override
	public boolean canPlaceItem(int var1, ItemStack var2) {
		return true;
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
	
}
