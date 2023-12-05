package player.commands;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import input.commands.CommandIn;
import output.result.ResultOut;

/**
 *      Clasa "ForwardBackwardNextPrev" este punctul comun al acestor comenzi
 *      Adica ele au nevoie de aceleasi obiecte pentru a se executa, iar acestea ..
 *      .. sunt inglobate in clasa abstracta de mai jos.
 *      Fiecare "implementare" (prev, next, forward, backward) va extinde aceasta clasa.
 * */
abstract class FBNP {
    protected Player currentPlayer;
    protected ResultOut result;

    /** Setter */
    public void setCurrentPlayer(final Player player) {
        currentPlayer = player;
    }

    /** Setter */
    public void setResult(final CommandIn command, final String msg) {
        result = new ResultOut(command);
        result.setMessage(msg);
    }

    /** Getter */
    public ResultOut getResult() {
        return result;
    }

    /** Getter */
    public PodcastInput getPodcast() {
        return currentPlayer.getLoadInfo().getSelectInfo().getPodcast();
    }

    /** Metoda care verifica daca se poate efectua comanda invocata */
    public abstract boolean checkValidity(CommandIn command);

    /**
     *      Metoda de mai jos returneaza episodul care ruleaza
     * */
    public EpisodeInput findEpisode() {
        int currentEpisodeIdx = 0;
        String currentEpisodeName = currentPlayer.getStats().getName();
        for (EpisodeInput episode: getPodcast().getEpisodes()) {
            if (episode.getName().equals(currentEpisodeName)) {
                currentEpisodeIdx = getPodcast().getEpisodes().indexOf(episode);
                break;
            }
        }
        return getPodcast().getEpisodes().get(currentEpisodeIdx);
    }
}
