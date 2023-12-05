package top5;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import playlist.commands.FollowStats;

import java.util.ArrayList;
import java.util.Collections;

/**
 *      Unicul rol al acestei clase este pentru executarea comenzii "getTop5Playlists"
 * */
public final class top5Playlists {
    private static top5Playlists instance = null;

    private top5Playlists() {
    }

    /** Clasa este singletton, se returneaza o instanta a sa */
    public static top5Playlists getInstance() {
        if (instance == null) {
            instance = new top5Playlists();
        }
        return instance;
    }

    /** Metoda de mai jos implementeaza comanda "getTop5Playlists" */
    public ResultGetTop5 getTop5Playlists(final CommandIn command,
                                          final ArrayList<FollowStats> topFwsPlaylists) {
        ResultGetTop5 result = new ResultGetTop5(command);
        /* Sortam playlist-urile dupa campul "followers" (in principiu) */
        Collections.sort(topFwsPlaylists);

        final int maxSize = 5;
        /* Verificam daca sunt mai mult de 5 elemente in top-ul playlist-urilor */
        if (topFwsPlaylists.size() > maxSize) {
            for (int i = 0; i < maxSize; i++) {
                result.getResult().add(topFwsPlaylists.get(i).getPlaylistName());
            }
        } else {
            topFwsPlaylists.forEach(memb -> result.getResult().add(memb.getPlaylistName()));
        }

        return result;
    }

}
