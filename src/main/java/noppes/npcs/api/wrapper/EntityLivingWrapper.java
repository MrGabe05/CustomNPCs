package noppes.npcs.api.wrapper;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IMob;
import noppes.npcs.api.entity.IEntityLiving;

public class EntityLivingWrapper<T extends MobEntity> extends EntityLivingBaseWrapper<T> implements IMob {
	
	public EntityLivingWrapper(T entity) {
		super(entity);
	}

	@Override
	public void navigateTo(double x, double y, double z, double speed){
		entity.getNavigation().stop();
		entity.getNavigation().moveTo(x, y, z, speed * 0.7);
	}

	@Override
	public void clearNavigation(){
		entity.getNavigation().stop();
	}

	@Override
	public IPos getNavigationPath() {
		if(!isNavigating()) {
			return null;
		}
		PathPoint point = entity.getNavigation().getPath().getEndNode();
		if(point == null) {
			return null;
		}
		return new BlockPosWrapper(new BlockPos(point.x, point.y, point.z));
	}

	@Override
	public boolean isNavigating(){
		return !entity.getNavigation().isDone();
	}
	
	@Override
	public boolean isAttacking(){
		return super.isAttacking() || entity.getTarget() != null;
	}

	@Override
	public void setAttackTarget(IEntityLiving living){
		if(living == null)
			entity.setTarget(null);
		else
			entity.setTarget(living.getMCEntity());
		super.setAttackTarget(living);
	}

	@Override
	public IEntityLiving getAttackTarget(){
		IEntityLiving base = (IEntityLiving)NpcAPI.Instance().getIEntity(entity.getTarget());
		return (base != null)? base: super.getAttackTarget();
	}
	
	@Override
	public boolean canSeeEntity(IEntity entity){
		return this.entity.getSensing().canSee(entity.getMCEntity());
	}

	@Override
	public void jump(){
		entity.getJumpControl().jump();
	}
}
