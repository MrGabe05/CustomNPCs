package noppes.npcs.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.constants.EnumGuiType;

import java.util.List;


public class ItemTeleporter extends Item {
	
    public ItemTeleporter(){
        super(new Item.Properties().stacksTo(1).tab(CustomTabs.tab));
    }

	@Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
    	ItemStack itemstack = player.getItemInHand(hand);
		if(!level.isClientSide)
	        return new ActionResult(ActionResultType.PASS, itemstack);
    	CustomNpcs.proxy.openGui(player, EnumGuiType.NpcDimensions);
        return new ActionResult(ActionResultType.PASS, itemstack);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity livingEntity){
    	if(livingEntity.level.isClientSide)
    		return true;
        float f = livingEntity.xRot;
        float f1 = livingEntity.yRot;
        Vector3d vector3d = livingEntity.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 80;
        Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);

        RayTraceResult movingobjectposition = livingEntity.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, livingEntity));
        if (movingobjectposition == null)
            return true;
        
        Vector3d vec32 = livingEntity.getViewVector(f);
        boolean flag = false;
        float f9 = 1.0F;
        List list = livingEntity.level.getEntities(livingEntity, livingEntity.getBoundingBox().inflate(vec32.x * d0, vec32.y * d0, vec32.z * d0).inflate((double)f9, (double)f9, (double)f9));

        for (int i = 0; i < list.size(); ++i){
            Entity entity = (Entity)list.get(i);

            if (entity.canBeCollidedWith()){
                float f10 = entity.getPickRadius();
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate((double)f10, (double)f10, (double)f10);

                if (axisalignedbb.contains(vector3d)){
                    flag = true;
                }
            }
        }

        if (flag)
            return true;
        
        if (movingobjectposition.getType() == RayTraceResult.Type.BLOCK){
        	BlockPos pos = ((BlockRayTraceResult)movingobjectposition).getBlockPos();
            
            while(livingEntity.level.getBlockState(pos).getBlock() != Blocks.AIR){
            	pos = pos.above();
            }
            livingEntity.teleportTo(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
        }
        
    	return true;
    }
}
