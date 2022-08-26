package noppes.npcs.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import noppes.npcs.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.util.CustomNPCsScheduler;

public class ItemNpcWand extends Item {
	
    public ItemNpcWand(){
		super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }

	@Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
    	ItemStack itemstack = player.getItemInHand(hand);
		if(!level.isClientSide)
			return new ActionResult(ActionResultType.SUCCESS, itemstack);
		CustomNpcs.proxy.openGui(player, EnumGuiType.NpcRemote);
		return new ActionResult(ActionResultType.SUCCESS, itemstack);
    }

	@Override
	public int getUseDuration(ItemStack p_77626_1_) {
		return 72000;
	}

	@Override
	public ActionResultType useOn(ItemUseContext context){
		if(context.getLevel().isClientSide)
			return ActionResultType.SUCCESS;
		
		if(CustomNpcs.OpsOnly && !context.getPlayer().getServer().getPlayerList().isOp(context.getPlayer().getGameProfile())){
			context.getPlayer().sendMessage(new TranslationTextComponent("availability.permission"), Util.NIL_UUID);
		} else if (CustomNpcsPermissions.hasPermission(context.getPlayer(), CustomNpcsPermissions.NPC_CREATE)) {
			final EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, context.getLevel());
	    	npc.ais.setStartPos(context.getClickedPos().above());
			npc.moveTo(context.getClickedPos().getX() + 0.5F, npc.getStartYPos(), context.getClickedPos().getZ() + 0.5F, context.getPlayer().yRot, context.getPlayer().xRot);

			context.getLevel().addFreshEntity(npc);
			npc.setHealth(npc.getMaxHealth());

			CustomNPCsScheduler.runTack(() -> NoppesUtilServer.sendOpenGui(context.getPlayer(), EnumGuiType.MainMenuDisplay, npc), 100);
		}
		else
			context.getPlayer().sendMessage(new TranslationTextComponent("availability.permission"), Util.NIL_UUID);
        return ActionResultType.SUCCESS;
    }

	@Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity playerIn){
        return stack;
    }
}
