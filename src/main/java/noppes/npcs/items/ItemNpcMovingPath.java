package noppes.npcs.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.CustomTabs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;


public class ItemNpcMovingPath extends Item{
    public ItemNpcMovingPath(){
		super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }

	@Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
    	ItemStack itemstack = player.getItemInHand(hand);
		if(level.isClientSide || !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_PATHER))
	        return new ActionResult(ActionResultType.PASS, itemstack);
		EntityNPCInterface npc = getNpc(itemstack, level);
		if(npc != null)
			NoppesUtilServer.sendOpenGui(player, EnumGuiType.MovingPath, npc);
        return new ActionResult(ActionResultType.SUCCESS, itemstack);
    }
    
	@Override
	public ActionResultType useOn(ItemUseContext context){
    	if(context.getLevel().isClientSide || !CustomNpcsPermissions.hasPermission(context.getPlayer(), CustomNpcsPermissions.TOOL_PATHER))
			return ActionResultType.FAIL;
		ItemStack stack = context.getItemInHand();
		EntityNPCInterface npc = getNpc(stack, context.getLevel());
		if(npc == null)
			return ActionResultType.PASS;
		List<int[]> list = npc.ais.getMovingPath();
		int[] pos = list.get(list.size() - 1);
		
		int x = context.getClickedPos().getX(), y = context.getClickedPos().getY(), z = context.getClickedPos().getZ();
		list.add(new int[]{x,y,z});
		
        double d3 = x - pos[0];
        double d4 = y - pos[1];
        double d5 = z - pos[2];
        double distance = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

		context.getPlayer().sendMessage(new TranslationTextComponent("message.pather.added", x, y, z, npc.getName()), Util.NIL_UUID);
        if(distance > CustomNpcs.NpcNavRange)
			context.getPlayer().sendMessage(new TranslationTextComponent("message.pather.farwarning", CustomNpcs.NpcNavRange), Util.NIL_UUID);
		
		return ActionResultType.SUCCESS;
    }
	
	private EntityNPCInterface getNpc(ItemStack item, World level){
		if(level.isClientSide || item.getTag() == null)
			return null;
		
		Entity entity = level.getEntity(item.getTag().getInt("NPCID"));
		if(entity == null || !(entity instanceof EntityNPCInterface))
			return null;
		
		return (EntityNPCInterface) entity;
	}
}
