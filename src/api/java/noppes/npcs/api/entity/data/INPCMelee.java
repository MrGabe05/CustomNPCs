package noppes.npcs.api.entity.data;

public interface INPCMelee {

	int getStrength();

	void setStrength(int strength);

	int getDelay();

	void setDelay(int speed);

	int getRange();

	void setRange(int range);

	int getKnockback();

	void setKnockback(int knockback);

	int getEffectType();

	int getEffectTime();

	int getEffectStrength();

	void setEffect(int type, int strength, int time);

}
