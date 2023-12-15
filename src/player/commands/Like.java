package player.commands;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import search.bar.Select;

import java.util.ArrayList;

/**
 *      Aceasta clasa va fi folosita pentru definirea listei "topLikedSongs"
 *      Fiecare clasa contine numele unei melodii si numarul de user care o apreciaza
 *      La comanda "GetTop5Songs", lista de mai sus va fi sortata in functie de campul "users"
 * */
public class Like implements Comparable {
    private SongInput songName;            /* Numele melodiei */
    private int idx;                              /* Indicele din librarie al melodiei */
    private final int bound = 100;

    public Like(final SongInput song, final LibraryInput library,
                final ArrayList<Like> topLikedSongs) {
        setSongName(song);
        idx = getIdxSong(song.getName(), library, topLikedSongs);
    }

    /** Metoda de mai jos implementeaza comanda "like" */
    public static ResultOut likeCmd(final ArrayList<Like> topLikedSongs,
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
            SongInput currSongRef = findSong(currentUser);

            /* Verificam daca in lista de aprecieri ale user-ului se gaseste melodia care ruleaza */
            for (String song : currentUser.getLikedSongsNames()) {
                if (song.equals(currentSong)) {
                    /*
                        Daca se gaseste, o vom elimina si marcam ..
                     .. aceasta eliminare (pentru lista topLikedSongs)
                     */
                    currentUser.getLikedSongs().removeIf(tempSong ->
                            tempSong.getName().equals(song));
                    deletedSong = true;
                    break;
                }
            }

            if (deletedSong) {
                /* Iteram prin lista "topLikedSongs" si cautam melodia din player */
                    /*
                        Daca se gaseste aceasta melodie in lista de topLikedSongs,
                    vom decrementa numarul de users care o apreciaza
                    */
                topLikedSongs.forEach(song -> {
                    if (song.getSong().equals(currSongRef)) {
                        song.getSong().decNrLikes();
                    }
                });

                result.setMessage("Unlike registered successfully.");
            } else {
                /* Melodia nu a fost apreciata de user*/
                currentUser.getLikedSongs().add(currSongRef);
                boolean created = false;
                currSongRef.incNrLikes();
                /*
                    O cautam in lista si incrementam numarul de aprecieri; daca nu o gasim,
                    instantiem o clasa pentru ea
                */
                for (Like iter : topLikedSongs) {
                    if (iter.getSongName().equals(currentSong)) {
                        created = true;
                        break;
                    }
                }
                if (!created) {
                    topLikedSongs.add(new Like(currSongRef, library, topLikedSongs));
                }

                result.setMessage("Like registered successfully.");
            }
        }
        return result;
    }

    /** Implementare pentru sortarea listei "top5Songs" */
    public int compareTo(final Object otherLike) {
        int compareNrUsers = ((Like) otherLike).getNrLikes();
        if (getNrLikes() == compareNrUsers) {
            if (idx > bound && ((Like) otherLike).getIdx() > bound) {
                /*
                    Daca sunt melodii din albume, sa le seteze
                    in functie de cum au fost adaugate in aplicatie
                    (Se presupune ca nu se adauga 100 de melodii in top-ul de melodii)
                */
                return songName.retrieveTimestampAdded()
                        - ((Like) otherLike).getSong().retrieveTimestampAdded();
            }
            /* Setare crescatoare pentru melodii din biblioteca */
            return idx - ((Like) otherLike).getIdx();
        }
        return compareNrUsers - getNrLikes();
    }

    /**
     *        Metoda folosita pentru a gasi indicele unei melodii in librarie
     * */
    private int getIdxSong(final String song, final LibraryInput library,
                           final ArrayList<Like> topLikedSongs) {
        final int upperbound = 999;
        int index = upperbound + topLikedSongs.size();
        /* ^--- Pentru a marca melodiile dintr-un album */
        ArrayList<SongInput> songs = library.getSongs();
        for (int i = 0; i < songs.size(); ++i) {
            if (songs.get(i).getName().equals(song)) {
                index = i;
                break;
            }
        }
        return index;
    }


    private static SongInput findSong(final NormalUser user) {
        String songToFind = user.getPlayer().getStats().getName();
        Select usersInfo = user.getPlayer().getLoadInfo().getSelectInfo();

        if (usersInfo.getSong() != null) {
            return usersInfo.getSong();
        } else {
            SongInput song = null;
            for (SongInput tempSong : usersInfo.getSongsCollection().getSongs()) {
                if (tempSong.getName().equals(songToFind)) {
                    song = tempSong;
                    break;
                }
            }
            return song;
        }
    }

    /** Setter */
    public void setSongName(final SongInput song) {
        songName = song;
    }
    /** Getter */
    public String getSongName() {
        return songName.getName();
    }

    /** Getter */
    public int getNrLikes() {
        return songName.retrieveNrLikes();
    }

    /** Getter */
    public SongInput getSong() {
        return songName;
    }

    /** Getter */
    public int getIdx() {
        return idx;
    }
}
