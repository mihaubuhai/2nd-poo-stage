package player.commands;

import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;

import java.util.ArrayList;

/**
 *      Clasa de mai jos este responsabila cu executarea comenzii "prev"
 * */
public class Prev extends NP {
    public Prev(final NormalUser user) {
        setCurrentUser(user);
        setCurrentPlayer(user.getPlayer());
    }

    /** Metoda de mai jos implementeaza comanda "prev" */
    public ResultOut prevFunc(final CommandIn command, final LibraryInput library) {
        /* Se verifica daca se poate efectua comanda "prev" */
        final String fail = "Please load a source before returning to the previous track.";
        if (!checkValidity(command)) {
            setResult(command, fail);
        } else {
            /* Se poate efectua "prev" si intai se afla ce se ruleaza in player */
            int playerType = currentPlayer.getLoadInfo().getSelectInfo().getResultType();
            final String succes = "Returned to previous track successfully. The current track is ";

            if (playerType == 1) {
                /* Se ruleaza o melodie din biblioteca */
                SongInput currSong = findSong(library);         // <-- Se cauta melodia
                currentPlayer.getStats().setPaused(false);
                currentPlayer.getStats().setRemainedTime(currSong.getDuration());
                setResult(command, succes + currSong.getName() + ".");
            } else if (playerType == 2) {
                /* Se ruleaza un episod de podcast */
                EpisodeInput currEpisode = findEpisode();       // <-- Se cauta episodul
                int remainedTime = currentPlayer.getStats().getRemainedTime();
                int initDuration = currEpisode.getDuration();
                int currEpisodeIdx = getPodcast().getEpisodes().indexOf(currEpisode);

                if (initDuration - remainedTime >= 1 || currEpisodeIdx == 0) {
                    /* Se revine la inceputul episodului (si daca suntem la inceput de podcast) */
                    currentPlayer.getStats().setPaused(false);
                    currentPlayer.getStats().setRemainedTime(initDuration);
                    setResult(command, succes + currEpisode.getName() + ".");
                } else {
                    /* Se ruleaza episodul precedent */
                    EpisodeInput prevEpisode = getPodcast().getEpisodes().get(currEpisodeIdx - 1);
                    currentPlayer.getStats().setPaused(false);
                    currentPlayer.getStats().setRemainedTime(prevEpisode.getDuration());
                    setResult(command, succes + prevEpisode.getName() + ".");
                }
            } else {
                /* Se ruleaza un playlist */
                int currSongIdx = findInPlaylist();
                SongInput currSong = getPlaylist().getSongs().get(currSongIdx);
                currentUser.updateRemainedTime(command);

                /*
                    Pot exista probleme din cauza starii "shuffle";
                    intrucat pentru "shuffle" e necesar ca indicele din shuffle sa fie primul
                */
                if (!currentPlayer.getStats().getShuffle()) {
                    checkTime(currSong, currSongIdx, command, false, succes);
                } else {
                    int shuffleCurrIdx = getPlaylist().getShuffledIndices().indexOf(currSongIdx);
                    checkTime(currSong, shuffleCurrIdx, command, true, succes);
                }
            }
            // v-- Se schimba timpul ultimei comenzi!!!!
            currentPlayer.setLastLoadTime(command.getTimestamp());
        }

        return getResult();
    }

    /** Metoda gaseste indicele din playlist anterior celui corespunzator melodiei care ruleaza */
    private int getPrevidx(final int idx, final boolean shuffle) {
        if (shuffle) {
            return findPrevIdxShuffle(idx);
        }
        return currentUser.findPrevSong(idx, getPlaylist());
    }

    /**
     *          Metoda abordeaza problema alegerii melodiei din playlist antecedente celei ..
     *      .. care ruleaza raportat la starea "shuffle"
     */
    private void checkTime(final SongInput currSong, final int currIdx,
                                            final CommandIn command, final boolean shuffle,
                                            final String succes) {
        int remainedTime = currentPlayer.getStats().getRemainedTime();
        int initDuration = currSong.getDuration();
        if (initDuration - remainedTime >= 1 || currIdx == 0) {
            /* Se revine la melodia curenta (daca este prima, se ruleaza tot ea) */
            currentPlayer.getStats().setPaused(false);
            currentPlayer.getStats().setRemainedTime(initDuration);
            setResult(command, succes + currSong.getName() + ".");
        } else {
            /* Se ruleaza melodia precedenta */
            int prevIdx = getPrevidx(currIdx, shuffle);
            SongInput prevSong = getPlaylist().getSongs().get(prevIdx);
            initDuration = prevSong.getDuration();
            currentPlayer.getStats().setPaused(false);
            currentPlayer.getStats().setRemainedTime(initDuration);
            currentPlayer.getStats().setName(prevSong.getName());
            setResult(command, succes + prevSong.getName() + ".");
        }
    }

    /**
     *   Metoda returneaza indicele din playlist corespunzator celui dinaintea lui idx in "shuffle"
     * */
    private int findPrevIdxShuffle(final int idx) {
        /* idx - indice al lui "shuffle" */
        ArrayList<Integer> shuffles = getPlaylist().getShuffledIndices();
        String repeatMode = currentPlayer.getStats().getRepeat().toLowerCase();

        /* Verificam tipul de repeat al player-ului */
        if (idx == 0 && repeatMode.contains("all")) {
            /* Final de vector de indici amestecati + stare de repeat all */
            return shuffles.get(shuffles.size() - 1);
        }
        return shuffles.get(idx - 1);
    }
}
