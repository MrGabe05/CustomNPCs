package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;

public class NpcDamageSourceInderect extends IndirectEntityDamageSource{

	public NpcDamageSourceInderect(String par1Str, Entity limbSwingAmountEntity, Entity par3Entity) {
		super(par1Str, limbSwingAmountEntity, par3Entity);
	}

	@Override
    public boolean scalesWithDifficulty() {
    	return false;
    }
}
