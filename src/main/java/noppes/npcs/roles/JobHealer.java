package noppes.npcs.roles;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobHealer extends JobInterface{
	private int healTicks = 0;
	public int range = 8;
	public byte type = 2; //0:friendly, 1:enemy, 2:all
	public int speed = 20;
	
	public HashMap<Integer, Integer> effects = new HashMap<Integer, Integer>();

	public JobHealer(EntityNPCInterface npc) {
		super(npc);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.putInt("HealerRange", range);
		nbttagcompound.putByte("HealerType",  type);
		nbttagcompound.put("BeaconEffects", NBTTags.nbtIntegerIntegerMap(effects));
		nbttagcompound.putInt("HealerSpeed", speed);
		return nbttagcompound;
	}

	@Override
	public void load(CompoundNBT nbttagcompound) {
		range = nbttagcompound.getInt("HealerRange");
		type = nbttagcompound.getByte("HealerType");
		effects = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("BeaconEffects", 10));
		speed = ValueUtil.CorrectInt(nbttagcompound.getInt("HealerSpeed"), 10, Integer.MAX_VALUE);
	}
	private List<LivingEntity> affected = new ArrayList<LivingEntity>();
	
	//TODO heal food, heal potion effects, heal more types of entities besides just the player and npcs
	public boolean aiShouldExecute() {
		healTicks++;
		if (healTicks < speed) 
			return false;
		healTicks = 0;
		affected = npc.level.getEntitiesOfClass(LivingEntity.class, npc.getBoundingBox().inflate(range, (double) range / 2.0D, range));
		return !affected.isEmpty();	
	}
	
	@Override
	public boolean aiContinueExecute() {
		return false;
	}

	public void aiStartExecuting() {
		for(LivingEntity entity : affected){
			boolean isEnemy = false;
			if(entity instanceof PlayerEntity){
				isEnemy = npc.faction.isAggressiveToPlayer((PlayerEntity) entity);
			}
			else if(entity instanceof EntityNPCInterface){
				isEnemy = npc.faction.isAggressiveToNpc((EntityNPCInterface) entity);
			}
			else{
				isEnemy = entity instanceof MobEntity;
			}
			
			if(entity == npc || type == 0 && isEnemy || type == 1 && !isEnemy)
				continue;

			for(Integer potionEffect : effects.keySet()){
				Effect p = Effect.byId(potionEffect);
				if(p != null)
					entity.addEffect(new EffectInstance(p, 100, effects.get(potionEffect)));
			}
		}
		affected.clear();
	}

	@Override
	public int getType() {
		return JobType.HEALER;
	}
}
