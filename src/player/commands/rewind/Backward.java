package player.commands.rewind;

import fileio.input.EpisodeInput;
import input.commands.CommandIn;
import output.result.ResultOut;
import player.Player;


/**
 *      Aceasta clasa "implementeaza" comanda "backward"
 * */
public class Backward extends RewindPodcast {
    public Backward(final Player player) {
        setCurrentPlayer(player);
    }

    /** Aceasta metoda implementeaza comanda "backward" */
    public ResultOut backwardFunc(final CommandIn command) {
        if (!checkValidity(command)) {
            return getResult();
        }

        /* Se verifica timpul ramas pentru ceea ce ruleaza */
        int remainedTime = currentPlayer.getStats().getRemainedTime();
        EpisodeInput currentEpisode = findEpisode();
        int initDuration = currentEpisode.getDuration();

        if (initDuration - remainedTime > diff) {
            /* Au trecut mai mult de 90 de secunde */
            currentPlayer.getStats().setRemainedTime(remainedTime + diff);
        } else {
            /* Au trecut mai putin de 90 secunde */
            currentPlayer.getStats().setRemainedTime(initDuration);
        }

        setResult(command, "Rewound successfully.");
        return getResult();
    }
}
