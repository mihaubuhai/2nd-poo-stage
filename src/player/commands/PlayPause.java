package player.commands;

import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;
import player.Player;

/**
 *      Aceasta clasa singletton are singurul rol de a "implementa" comanda "playPause"
 * */
public final class PlayPause {
    private static PlayPause instance;

    private PlayPause() {
    }

    /** Returneaza clasa statica "PlayPause" */
    public static PlayPause getInstance() {
        if (instance == null) {
            instance = new PlayPause();
        }
        return instance;
    }

    /** Metoda implementeaza comanda "playPause" */
    public ResultOut playPauseFunc(final Player player, final CommandIn command,
                                   final NormalUser currentUser) {
        ResultOut result = new ResultOut(command);

        /* Se verifica aceasta comanda se invoca pentru player care nu ruleaza nimic */
        if (player == null || player.getLoadInfo() == null) {
            result.setMessage("Please load a source before attempting"
                    + " to pause or resume playback.");
            return result;
        }

        player.getStats().setPaused(player.getStats().getPaused() ^ true);

        if (player.getStats().getPaused()) {
            result.setMessage("Playback paused successfully.");
        } else {
            result.setMessage("Playback resumed successfully.");
        }

        return result;
    }

}
