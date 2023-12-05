package playlist.commands.collections;

import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;

import java.util.ArrayList;

/**
 *      Aceasta clasa reprezinta conceptul de "colectie de melodii", ..
 *      .. lucru 'implementat' de clasele "Playlist" si "Album"
 * */
public class SongsCollection {
    private String name;      // <-- Numele colectiei de melodii
    private ArrayList<SongInput> songs; // <-- Melodiile pe care le stocheaza
    private ArrayList<Integer> shuffledIndices; // <-- Vectorul de indici amestecati
    private int timeOfCreation;   // <-- Camp folosit pentru sortarea topului de playlist-uri

    public SongsCollection(final CommandIn command) {
        setName(command.getPlaylistName());
        setTimeOfCreation(command.getTimestamp());
        setSongs(new ArrayList<>());
    }

    /** Setter */
    public void setName(String name) {
        this.name = name;
    }
    /** Setter */
    public void setSongs(ArrayList<SongInput> songs) {
        this.songs = songs;
    }
    /** Setter */
    public void setShuffledIndices(ArrayList<Integer> idxes) {
        shuffledIndices = idxes;
    }
    /** Setter */
    public void setTimeOfCreation(int time) {
        timeOfCreation = time;
    }

    /** Getter */
    public String getName() {
        return name;
    }
    /** Getter */
    public ArrayList<SongInput> getSongs() {
        return songs;
    }
    /** Getter */
    public ArrayList<Integer> getShuffledIndices() {
        return shuffledIndices;
    }
    /** Getter */
    public int getTimeOfCreation() {
        return timeOfCreation;
    }


    /**             ---- Metoda va fi apelata de playlist-ul / albumul user-ului curent ----
     *      Valoarea returnata stocheaza indicele urmatoarei melodii care ar trebui sa se ruleze ..
     *      .. relativ la cea care se ruleaza in player
     * */
    public int findNextIdxSong(final NormalUser user) {
        /* Cautam indicele melodiei care ruleaza */
        int currentIdxSong = 0;
        String currentSongName = user.getPlayer().getStats().getName();
        for (SongInput song: this.getSongs()) {
            if (song.getName().equals(currentSongName)) {
                /* S-a gasit melodia, aflam indicele si iesim din for */
                currentIdxSong = getSongs().indexOf(song);
                break;
            }
        }
        /* currentIdxSong contine indicele melodiei din playlist care ruleaza */

        String repeatMode = user.getPlayer().getStats().getRepeat().toLowerCase();
        /* Verificam starea shuffle */
        if (!user.getPlayer().getStats().getShuffle()) {
            /* Verificam daca s-a ajuns la final de playlist */
            Playlist usersPlaylist = user.getPlayer().getLoadInfo().getSelectInfo().getPlaylist();
            if (currentIdxSong == usersPlaylist.getSongs().size() - 1) {
                /* S-a ajuns la final de playlist, verificam starea de repeat */
                if (repeatMode.contains("no")) {
                    return -1;
                } else {
                    /* Se disting 2 cazuri: "Repeat All" si "Repeat Current Song" */
                    if (repeatMode.contains("all")) {
                        return 0;
                    }
                    return currentIdxSong;
                }
            }
            /* Melodia urmatoare va fi evident urmatoarea dupa indice */
            return currentIdxSong + 1;
        } else {
            /* Shuffle este activ, vom cauta in vectorul de indici indicele curent */
            int currShuffledIdx = getShuffledIndices().indexOf(currentIdxSong);
            /*
                "currShuffledIdx" va contine indicele din "shuffleIndices" ..
                .. aferent melodiei care ruleaza
            */

            if (currShuffledIdx == getShuffledIndices().size() - 1) {
                /* Se poate ca "currShuffleIdx" sa fie ultimul indice din  "shuffleIndices" */
                /* S-a ajuns la final de playlist, verificam starea de repeat */
                if (repeatMode.contains("no")) {
                    return -1;
                } else {
                    /* Se disting 2 cazuri: "Repeat All" si "Repeat Current Song" */
                    if (repeatMode.contains("all")) {
                        return getShuffledIndices().get(0);
                    }
                    return currentIdxSong;
                }
            }

            return getShuffledIndices().get(currShuffledIdx + 1);
        }
    }
}
