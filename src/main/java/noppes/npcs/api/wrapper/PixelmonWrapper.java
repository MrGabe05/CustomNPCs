package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import noppes.npcs.api.constants.EntitiesType;
import noppes.npcs.api.entity.IPixelmon;
import noppes.npcs.controllers.PixelmonHelper;

public class PixelmonWrapper<T extends AbstractHorseEntity> extends AnimalWrapper<T> implements IPixelmon {

	public PixelmonWrapper(T entity) {
		super(entity);
	}

	@Override
	public Object getPokemonData() {
		return PixelmonHelper.getPokemonData(entity);
	}
 
	@Override
	public int getType() {
		return EntitiesType.PIXELMON;
	}

	@Override
	public boolean typeOf(int type){
		return type == EntitiesType.PIXELMON || super.typeOf(type);
	}
}
