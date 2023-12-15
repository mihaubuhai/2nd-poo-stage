package songcollections.collections;

import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import users.UserInfo;

import java.util.ArrayList;

/**
 *      Aceasta clasa reprezinta conceptul de "colectie de melodii",
 *       extinsa de clasele "Playlist" si "Album"
 * */
public class SongsCollection {
    private UserInfo owner;
    private String name;      // <-- Numele colectiei de melodii
    private ArrayList<SongInput> songs; // <-- Melodiile pe care le stocheaza
    private ArrayList<Integer> shuffledIndices; // <-- Vectorul de indici amestecati
    private int timeOfCreation;   // <-- Camp folosit pentru sortarea topului de playlist-uri
    private int nrListeners; //<-- Numarul de ascultatori, la un moment, al unei colectii audio
    protected boolean isAlbum;
    private int totalLikes; // <-- Contine numarul total de like-uri pentru toate piesele

    public SongsCollection(final CommandIn command) {
        setTimeOfCreation(command.getTimestamp());
        setSongs(new ArrayList<>());
    }

    /**     <p>
     *               ---- Metoda va fi apelata de playlist-ul / albumul user-ului curent ----
     *      </p>
     *      Valoarea returnata stocheaza indicele urmatoarei melodii care ar trebui sa se ruleze
     *      relativ la cea care se ruleaza in player
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
            SongsCollection usersPlaylist = user.getPlayer().getLoadInfo().getSelectInfo().getSongsCollection();
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

    /** Setter */
    public void setName(final String name) {
        this.name = name;
    }
    /** Setter */
    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }
    /** Setter */
    public void setShuffledIndices(final ArrayList<Integer> idxes) {
        shuffledIndices = idxes;
    }
    /** Setter */
    public void setTimeOfCreation(final int time) {
        timeOfCreation = time;
    }
    /** Setter */
    public void setOwner(final UserInfo owner) {
        this.owner = owner;
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
    /** Getter */
    public UserInfo getOwner() {
        return owner;
    }

    /** Getter */
    public ArrayList<String> getSongNames() {
        ArrayList<String> result = new ArrayList<>();

        songs.forEach(song -> result.add(song.getName()));

        return result;
    }

    /** Getter */
    public int getNrListeners() {
        return nrListeners;
    }

    /** Metoda incrementeaza numarul de ascultatori al unui user */
    public void incrementNrListeners() {
        nrListeners++;
    }

    /** Metoda decrementeaza numarul de ascultatori al unui user */
    public void decrementNrListeners() {
        if (nrListeners > 0) {
            nrListeners--;
        }
    }

    /** Getter */
    public boolean isAlbum() {
        return isAlbum;
    }

    /** Getter */
    public int getTotalLikes() {
        return totalLikes;
    }

    /** Metoda incrementeaza numarul de like-uri total al colectiei */
    public void incrementTotalLikes() {
        totalLikes++;
    }

    /** Metoda care decrementeaza numarul total de like-uri */
    public void decrementTotalLikes() {
        if (totalLikes > 0) {
            totalLikes--;
        }
    }

}
