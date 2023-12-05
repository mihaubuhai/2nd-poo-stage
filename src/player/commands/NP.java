package player.commands;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;
import playlist.commands.collections.Playlist;

import java.util.ArrayList;

/**
 *      Clasa "NextPrev" implementeaza metoda abstracta care va fi ..
 *      .. folosita de ambele comenzi: "next" si "prev"
 * */
public class NP extends FBNP {
    protected NormalUser currentUser;

    /** Setter */
    public void setCurrentUser(final NormalUser user) {
        currentUser = user;
    }
    /** Getter */
    public Playlist getPlaylist() {
        return currentPlayer.getLoadInfo().getSelectInfo().getPlaylist();
    }

    /** Metoda verifica daca se pot executa comenzile "Next" sau "Repeat" */
    public boolean checkValidity(final CommandIn command) {
        if (currentPlayer == null || currentPlayer.getLoadInfo() == null
                || !currentPlayer.getLoadInfo().getLoaded()) {
            return false;
        }
        return true;
    }

    /**
     *      Metoda de mai jos cauta in librarie melodia care ruleaza
     * */
    public SongInput findSong(final LibraryInput library) {
        String currSong = currentPlayer.getStats().getName();
        for (SongInput song: library.getSongs()) {
            if (song.getName().equals(currSong)) {
                return song;
            }
        }
        return null;
    }

    /**
     *      Metoda care returneaza indicele melodiei din player care ruleaza
     * */
    public int findInPlaylist() {
        Playlist playlist = getPlaylist();
        ArrayList<SongInput> songs = playlist.getSongs();
        String currSong = currentPlayer.getStats().getName();

        for (int idx = 0; idx < songs.size(); idx++) {
            if (songs.get(idx).getName().equals(currSong)) {
                return idx;
            }
        }
        return -1;
    }
}
