package kx.myfirstmod.misc;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.world.World;

public class ScoreboardReader {
    public static int getPlayerScore(World world, String playerName, String objectiveName) {
        // Get the scoreboard from the world
        Scoreboard scoreboard = world.getScoreboard();

        // Get the objective by name
        ScoreboardObjective objective = scoreboard.getObjective(objectiveName);

        if (objective != null) {
            // Get the player's score for this objective
            ScoreboardPlayerScore playerScore = scoreboard.getPlayerScore(playerName, objective);

            // Return the score value
            return playerScore.getScore();
        }

        // Return 0 if the objective or player does not exist
        return 0;
    }
}