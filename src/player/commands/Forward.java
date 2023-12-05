package player.commands;

import fileio.input.EpisodeInput;
import input.commands.CommandIn;
import output.result.ResultOut;

/**
 *      Aceasta clasa "implementeaza" comanda Forward
 * */
public class Forward extends FB {
    public Forward(final Player player) {
        setCurrentPlayer(player);
    }

    /** Metoda de mai jos implementeaza comanda "forward" */
    public ResultOut forwardFunc(final CommandIn command) {
        if (!checkValidity(command)) {
            return getResult();
        }
        /* Cazurile de esec au fost acoperite, se efectueaza comanda */
        int remainedTime = currentPlayer.getStats().getRemainedTime();
        if (remainedTime > diff) {
            /* Se avanseaza cu 90 secunde */
            currentPlayer.getStats().setRemainedTime(remainedTime - diff);
            setResult(command, "Skipped forward successfully.");
        } else {
            /* Se cauta episodul curent */
            EpisodeInput currentEpisode = findEpisode();
            int currentEpisodeIdx = getPodcast().getEpisodes().indexOf(currentEpisode);

            /* Se trece la urmatorul episod */
            EpisodeInput nextEpisode = getPodcast().getEpisodes().get(currentEpisodeIdx + 1);
            currentPlayer.getStats().setName(nextEpisode.getName());
            currentPlayer.getStats().setRemainedTime(nextEpisode.getDuration());

            setResult(command, "Skipped forward successfully.");
        }

        return getResult();
    }
}
