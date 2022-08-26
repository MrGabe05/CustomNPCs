package noppes.npcs.api.wrapper;

import java.util.Collection;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardObjective;
import noppes.npcs.api.IScoreboardScore;

public class ScoreboardObjectiveWrapper implements IScoreboardObjective{
	private ScoreObjective objective;
	private Scoreboard board;
	
	protected ScoreboardObjectiveWrapper(Scoreboard board, ScoreObjective objective){
		this.objective = objective;
		this.board = board;
	}
	
	@Override
	public String getName(){
		return objective.getName();
	}
	
	@Override
	public String getDisplayName(){
		return objective.getDisplayName().getString();
	}
	
	@Override
	public void setDisplayName(String name){
		if(name.length() <= 0 || name.length() > 32)
			throw new CustomNPCsException("Score objective display name must be between 1-32 characters: %s", name);
		objective.setDisplayName(new TranslationTextComponent(name));
	}
	
	@Override
	public String getCriteria(){
		return objective.getCriteria().getName();
	}
	
	@Override
	public boolean isReadyOnly(){
		return objective.getCriteria().isReadOnly();
	}

	@Override
	public IScoreboardScore[] getScores(){
		Collection<Score> list = board.getPlayerScores(objective);
		IScoreboardScore[] scores = new IScoreboardScore[list.size()];
		int i = 0;
		for(Score score : list){
			scores[i] = new ScoreboardScoreWrapper(score);
			i++;
		}
		return scores;
	}

	@Override
	public IScoreboardScore getScore(String player) {
		if(!hasScore(player))
			return null;
		return new ScoreboardScoreWrapper(board.getOrCreatePlayerScore(player, objective));
	}

	@Override
	public IScoreboardScore createScore(String player) {
		return new ScoreboardScoreWrapper(board.getOrCreatePlayerScore(player, objective));
	}

	@Override
	public void removeScore(String player) {
		board.resetPlayerScore(player, objective);
	}

	@Override
	public boolean hasScore(String player) {
		return board.hasPlayerScore(player, objective);
	}
}
