package player.commands;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;

import java.util.ArrayList;

/**
 *      Aceasta clasa va fi folosita pentru definirea listei "topLikedSongs"
 *      Fiecare clasa contine numele unei melodii si numarul de user care o apreciaza
 *      La comanda "GetTop5Songs", lista de mai sus va fi sortata in functie de campul "users"
 * */
public class Like implements Comparable {
    private String songName;            /* Numele melodiei */
    private int users;                          /* Numarul de user care apreciaza melodia */
    private int idx;                              /* Indicele din librarie al melodiei */

    /** Implementare pentru sortarea listei "top5Songs" */
    public int compareTo(final Object otherLike) {
        int compareNoUsers = ((Like) otherLike).getUsers();
        if (users == compareNoUsers) {
            return idx - ((Like) otherLike).getIdx();
        }
        return compareNoUsers - users;
    }

    /**
     *        Metoda folosita pentru a gasi indicele unei melodii in librarie
     * */
    private int getIdxSong(final String song, final LibraryInput library) {
        int index = 0;
        ArrayList<SongInput> songs = library.getSongs();
        for (int i = 0; i < songs.size(); ++i) {
            if (songs.get(i).getName().equals(song)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public Like(final String song, final LibraryInput library) {
        setSongName(song);
        incrementNoUsers();
        idx = getIdxSong(song, library);
    }

    /** Setter */
    public void setSongName(final String song) {
        songName = song;
    }
    /** Getter */
    public String getSongName() {
        return songName;
    }

    /** Incrementeaza numarul de like-uri */
    public void incrementNoUsers() {
        users += 1;
    }

    /** Decrementeaza numarul de like-uri */
    public void decrementNoUsers() {
        if (users > 0) {
            users -= 1;     // <--- Nu are sens sa existe numar negativ de aprecieri
        }
    }

    /** Getter */
    public int getUsers() {
        return users;
    }

    /** Getter */
    public int getIdx() {
        return idx;
    }

    /** Metoda de mai jos implementeaza comanda "like" */
    public static ResultOut likeCommand(final ArrayList<Like> topLikedSongs,
                                                                   final NormalUser currentUser,
                                                                   final CommandIn command,
                                                                   final LibraryInput library) {

        ResultOut result = new ResultOut(command);
        /* Verificam daca player ruleaza */
        if (currentUser.getPlayer() == null || currentUser.getPlayer().getLoadInfo() == null
                || !currentUser.getPlayer().getLoadInfo().getLoaded()) {
            result.setMessage("Please load a source before liking or unliking.");
        } else if (currentUser.getPlayer().getLoadInfo().getSelectInfo().getResultType() == 2) {
            /* Verificam daca player ruleaza un podcast */
            result.setMessage("Loaded source is not a song.");
        } else {
            String currentSong = currentUser.getPlayer().getStats().getName();
            boolean deletedSong = false;

            /* Verificam daca in lista de aprecieri ale user-ului se gaseste melodia care ruleaza */
            for (String song: currentUser.getLikedSongs()) {
                if (song.equals(currentSong)) {
                    /*
                        Daca se gaseste, o vom elimina si marcam ..
                     .. aceasta eliminare (pentru lista topLikedSongs)
                     */
                    currentUser.getLikedSongs().remove(song);
                    deletedSong = true;
                    break;
                }
            }

            if (deletedSong) {
                /* Iteram prin lista "topLikedSongs" si cautam melodia din player */
                for (Like iter: topLikedSongs) {
                    /*
                        Daca se gaseste aceasta melodie in lista de topLikedSongs,
                    vom decrementa numarul de users care o apreciaza
                    */
                    if (iter.getSongName().equals(currentSong)) {
                        iter.decrementNoUsers();
                        break;
                    }
                }
                result.setMessage("Unlike registered successfully.");
            } else {
                /* Melodia nu a fost apreciata de user*/
                currentUser.getLikedSongs().add(currentSong);
                boolean created = false;
                /*
                    O cautam in lista si incrementam numarul de aprecieri; daca nu o gasim,
                    instantiem o clasa pentru ea
                */
                for (Like iter: topLikedSongs) {
                    if (iter.getSongName().equals(currentSong)) {
                        iter.incrementNoUsers();
                        created = true;
                        break;
                    }
                }
                if (!created) {
                    topLikedSongs.add(new Like(currentSong, library));
                }
                result.setMessage("Like registered successfully.");
            }
        }
        return result;
    }
}
