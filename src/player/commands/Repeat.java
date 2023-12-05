package player.commands;

import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;
import player.Player;
import player.Stats;

/**
 *      Metoda de mai jos are singurul scop de a executa comanda "repeat"
 * */
public class Repeat {
    /** Metoda de mai jos returneaza repeat-ul playlist-ului sub forma unui String */
    public String findRepeatModePlaylist(final Stats currentPlayerStats) {
        if (currentPlayerStats.getRepeat().toLowerCase().contains("no")) {
            /* Prima stare pentru repeat, adica "no repeat" */
            return "Repeat All";
        } else if (currentPlayerStats.getRepeat().toLowerCase().contains("all")) {
            /* A doua stare pentru repeat, adica "repeat all" */
            return  "Repeat Current Song";
        } else {
            /* A treia stare pentru repeat, adica "repeat current song" */
            return  "No Repeat";
        }
    }

    /** Metoda de mai jos returneaza repeat-ul unei piese/episod de podcast sub forma unui string*/
    public String findRepeatModeSongEpisode(final Stats currentPlayerStats) {
        if (currentPlayerStats.getRepeat().toLowerCase().contains("no")) {
            /* Prima stare, schimbam in "repeat once" */
            return "Repeat Once";
        } else if (currentPlayerStats.getRepeat().toLowerCase().contains("once")) {
            /* A doua stare, schimbam in "repeat infinite" */
            return "Repeat Infinite";
        } else {
            /* A treia stare, schimbam in "no repeat" */
            return "No Repeat";
        }
    }

    /** Aceasta metoda reprezinta comanda "repeat" */
    public ResultOut changeRepeatMode(final CommandIn command, final NormalUser user) {
        ResultOut result = new ResultOut(command);
        final int playlistId = 3;

        /* Verificam daca player-ul ruleaza */
        if (user.getPlayer() == null || user.getPlayer().getLoadInfo() == null) {
            result.setMessage("Please load a source before setting the repeat status.");
            return result;
        } else {
            user.updateRemainedTime(command);
            /* Player ruleaza, verificam ce ruleaza */
            Player currentPlayer = user.getPlayer();
            int currentRunner = currentPlayer.getLoadInfo().getSelectInfo().getResultType();
            /*    ^^------ Tine cont de ce se ruleaza in player la un moment dat */

            Stats currentPlayerStats = currentPlayer.getStats();
            String repeatMode;
            if (currentRunner == playlistId) {
                /* Se ruleaza un playlist */
                repeatMode = findRepeatModePlaylist(currentPlayerStats);
            } else {
                /* Se ruleaza fie o melodie fie un episod de podcast */
                repeatMode = findRepeatModeSongEpisode(currentPlayerStats);
            }

            currentPlayerStats.setRepeat(repeatMode);
            result.setMessage("Repeat mode changed to " + repeatMode.toLowerCase() + ".");
        }

        return result;
    }
}
