package noppes.npcs.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemScripted extends Item {
	
	public ItemScripted(Item.Properties props){
		super(props);
	}
	
	public static ItemScriptedWrapper GetWrapper(ItemStack stack) {
		return (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(stack);
	}

	@Override
    public boolean showDurabilityBar(ItemStack stack){
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(istack instanceof ItemScriptedWrapper)
			return ((ItemScriptedWrapper)istack).durabilityShow;
        return super.showDurabilityBar(stack);
    }

	@Override
    public double getDurabilityForDisplay(ItemStack stack){
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(istack instanceof ItemScriptedWrapper)
			return 1 - ((ItemScriptedWrapper)istack).durabilityValue;
        return super.getDurabilityForDisplay(stack);
    }

	@Override
    public int getRGBDurabilityForDisplay(ItemStack stack){
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(!(istack instanceof ItemScriptedWrapper))
			return super.getRGBDurabilityForDisplay(stack);
		int color = ((ItemScriptedWrapper)istack).durabilityColor;
		if(color >= 0)
			return color;
        return MathHelper.hsvToRgb(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

	@Override
    public int getItemStackLimit(ItemStack stack){
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(istack instanceof ItemScriptedWrapper)
			return istack.getMaxStackSize();
        return super.getItemStackLimit(stack);
    }

	@Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker){
        return true;
    }

	@Override
	public boolean shouldOverrideMultiplayerNbt() {
		return true;
	}

	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(istack instanceof ItemScriptedWrapper)
			return ((ItemScriptedWrapper)istack).getMCNbt();
		return null;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		if(nbt == null) {
			return;
		}
		IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
		if(istack instanceof ItemScriptedWrapper)
			((ItemScriptedWrapper)istack).setMCNbt(nbt);
	}
}
