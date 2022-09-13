package noppes.npcs.api.handler.data;

import net.minecraft.util.text.ITextComponent;

public interface IQuestObjective {

	int getProgress();
	
	/**
	 * Does not work for Item or Dialog quests
	 * @param progress Progress of the objective
	 */
	void setProgress(int progress);
	
	int getMaxProgress();
	
	boolean isCompleted();

	String getText();

	ITextComponent getMCText();
}
