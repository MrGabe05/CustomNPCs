package noppes.npcs.api.wrapper;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IMonster;

public class MonsterWrapper<T extends MonsterEntity> extends EntityLivingWrapper<T> implements IMonster{
	public MonsterWrapper(T entity) {
		super(entity);
	}

	@Override
	public int getType() {
		return EntitiesType.MONSTER;
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.MONSTER?true:super.typeOf(type);
	}
}
