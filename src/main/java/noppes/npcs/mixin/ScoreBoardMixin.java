package noppes.npcs.mixin;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Scoreboard.class)
public interface ScoreBoardMixin {

    @Accessor(value="playerScores")
    Map<String, Map<ScoreObjective, Score>> getScores();

}
