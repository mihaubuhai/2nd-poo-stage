package player.commands.rewind;

import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.SongsCollection;

/**
 *      Clasa de mai jos implementeaza comanda "next"
 * */
public class Next extends RewindSongCol {
    public Next(final NormalUser user) {
        setCurrentUser(user);
        setCurrentPlayer(user.getPlayer());
    }

    /** Metoda de mai jos implementeaza comanda "next" */
    public ResultOut nextFunc(final CommandIn command, final LibraryInput library) {
        /* Verifica daca se poate executa comanda "next" */
        final String fail = "Please load a source before skipping to the next track.";
        final String success = "Skipped to next track successfully. The current track is ";

        if (!checkValidity(command)) {
            setResult(command, fail);
        } else {
            /* Se afla ce se ruleaza in player */
            String repeatMode = currentPlayer.getStats().getRepeat().toLowerCase();
            int playerType = currentPlayer.getLoadInfo().getSelectInfo().getResultType();

            if (playerType == 1) {
                /* Se ruleaza o melodie din biblioteca */
                if (repeatMode.contains("no")) {
                    /* Se da next la o melodie care nu se repeta, deci se goleste player */
                    currentUser.removePlayer();
                    setResult(command, fail);
                } else {
                    /*
                        Se repeta o melodie; timpul trebuie reinitializat cu cel initial ..
                        si repeat-ul schimbat aferent
                     */
                    SongInput currSong = findSong(library);
                    executeSucces(currSong.getName(), command, success, currSong.getDuration());
                    if (repeatMode.contains("once")) {
                        /* Se face doar o singura repetare, dar aceasta s-a efectuat deja*/
                        currentPlayer.getStats().setRepeat("No Repeat");
                    }
                }
            } else if (playerType == 2) {
                /* Se ruleaza un episod de podcast */
                EpisodeInput currEpisode = findEpisode();  // <--- Se cauta episodul care ruleaza
                int episodeIdx = getPodcast().getEpisodes().indexOf(currEpisode);

                if (repeatMode.contains("no")) {
                    /* Nu se repeta vreun episod de podcast, verificam daca este ultimul */
                    if (episodeIdx == getPodcast().getEpisodes().size() - 1) {
                        /* Este cu adevarat ultimul si nu se repeta, deci se elibereaza player */
                        currentUser.removePlayer();
                        setResult(command, fail);
                    } else {
                        /* Nu este final de podcast, se incarca urmatorul episod */
                        EpisodeInput nextEpisode = getPodcast().getEpisodes().get(episodeIdx + 1);
                        executeSucces(nextEpisode.getName(), command, success, nextEpisode.getDuration());
                    }
                } else {
                    /* Episodul se repeta */
                    setResult(command, success + currEpisode.getName() + ".");
                    currentPlayer.getStats().setPaused(false);
                    currentPlayer.getStats().setRemainedTime(currEpisode.getDuration());
                    if (repeatMode.contains("once")) {
                        /* Se repeta doar o data */
                        currentPlayer.getStats().setRepeat("No Repeat");
                    }
                }
            } else {
                /* Se ruleaza un playlist / album */
                SongsCollection currSongCollection = currentUser.getSongsCollection();
                int nextIdx = currSongCollection.findNextIdxSong(currentUser);

                if (repeatMode.contains("no")) {
                    /* Nu se face repeat; verificam daca s-a ajuns la final de playlist */
                    if (nextIdx < 0) {
                        /* Suntem la final de playlist si nu se repeta, deci se goleste player */
                        currentUser.removePlayer();
                        setResult(command, fail);
                    } else {
                        /* Nu suntem la final de playlist, se trece la urmatoarea melodie */
                        SongInput nextSong =currSongCollection.getSongs().get(nextIdx);
                        executeSucces(nextSong.getName(), command, success, nextSong.getDuration());
                    }
                } else if (repeatMode.contains("all")) {
                    /* Se va trece la urmatorea melodie din playlist */
                    SongInput nextSong =currSongCollection.getSongs().get(nextIdx);
                    executeSucces(nextSong.getName(), command, success, nextSong.getDuration());
                } else {
                    /* Se da next la o melodie din playlist cu repeatMode: "repeat current song" */
                    int idx = findInPlaylistOrAlbum();
                    SongInput currSong = currSongCollection.getSongs().get(idx);
                    //  Se va trece, --^ evident, la inceputul melodiei care ruleaza
                    executeSucces(currSong.getName(), command, success, currSong.getDuration());
                }
            }
            // v-- Se schimba timpul ultimei comenzi!!!!
            currentPlayer.setLastLoadTime(command.getTimestamp());
        }

        return  getResult();
    }
}
