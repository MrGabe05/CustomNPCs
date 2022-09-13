package noppes.npcs.api.wrapper;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IVillager;

public class VillagerWrapper<T extends VillagerEntity> extends EntityLivingWrapper<T> implements IVillager {

	public VillagerWrapper(T entity) {
		super(entity);
	}

	public String getProfession() {
		return entity.getVillagerData().getProfession().toString();
	}

	public String VillagerType(){
		return entity.getVillagerData().getType().toString();
	}

	@Override
	public int getType() {
		return EntitiesType.VILLAGER;
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.VILLAGER || super.typeOf(type);
	}
}
