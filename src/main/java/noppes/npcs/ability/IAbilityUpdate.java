package noppes.npcs.ability;

public interface IAbilityUpdate extends IAbility {
	
	public abstract boolean isActive();

	public abstract void update();
}
