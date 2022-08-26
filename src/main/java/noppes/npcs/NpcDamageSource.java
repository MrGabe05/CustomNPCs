package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class NpcDamageSource extends EntityDamageSource{

	public NpcDamageSource(String par1Str, Entity limbSwingAmountEntity) {
		super(par1Str, limbSwingAmountEntity);
	}

	@Override
    public boolean scalesWithDifficulty(){
    	return false;
    }
}
