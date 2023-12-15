package songcollections.collections;

import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.commands.FollowStats;

import java.util.ArrayList;

public class Playlist extends SongsCollection {
    private String visibility;
    private int followers;

    public Playlist(final CommandIn command) {
        super(command);
        setName(command.getPlaylistName());
        setVisibility("public");
        followers = 0;
        isAlbum = false;
    }

    /**
     *      Metoda de mai jos implementeaza comanda "switchVisibility"
     * */
    public static ResultOut switchVisibility(final CommandIn command,
                                             final NormalUser currentUser) {
        ResultOut result = new ResultOut(command);
        int noPlaylists = currentUser.getPlaylists().size();

        /* Verificam daca s-a dat in comanda un ID invalid */
        if (command.getPlaylistId() > noPlaylists) {
            result.setMessage("The specified playlist ID is too high.");
        } else {
            Playlist currentPlaylist = currentUser.getPlaylists().get(command.getPlaylistId() - 1);
            if (currentPlaylist.getVisibility().equals("public")) {
                currentPlaylist.setVisibility("private");
                result.setMessage("Visibility status updated successfully to private.");
            } else {
                currentPlaylist.setVisibility("public");
                result.setMessage("Visibility status updated successfully to public.");
            }
        }

        return result;
    }

    /**
     *      Metoda care creaza un playlist
     *      Returneaza rezultatul comenzii
     *      Playlist-ul este retinut in lista de playlist-uri ale user-ului
     * */
    public static ResultOut createPlaylist(final NormalUser currentUser, final CommandIn command,
                                           final ArrayList<FollowStats> topFwdPlaylits) {
        ResultOut out = new ResultOut(command);
        boolean isCreated = false;
        // ^-- Pentru verificare existenta a playlist-ului cu numele in command

        /* Iteram prin lista de playlist-uri si verificam numele acestora in parte */
        for (Playlist iter: currentUser.getPlaylists()) {
            if (iter.getName().equals(command.getPlaylistName())) {
                out.setMessage("A playlist with the same name already exists.");
                isCreated = true;
            }
        }

        if (!isCreated) {
            // v-- Cream efectiv playlist-ul
            Playlist newPlaylist = ((Playlist) SongsCollFactory.getCollection(command));
            newPlaylist.setOwner(currentUser);
            currentUser.getPlaylists().add(newPlaylist);
            topFwdPlaylits.add(new FollowStats(command.getPlaylistName()));
            out.setMessage("Playlist created successfully.");
        }

        return out;
    }

    /** Setter */
    public void setVisibility(final String type) {
        visibility = type;
    }
    /** Metoda care incrementeaza numarul de urmaritori ai playlist-ului */
    public void incNrFollowers() {
        followers += 1;
    }
    /** Metoda care decrementeaza numarul de urmaritori ai playlist-ului */
    public boolean decNrFollowers() {
        if (followers > 0) {
            followers -= 1;
        }
        return true;
    }

    /** Getter */
    public String getVisibility() {
        return visibility;
    }
    /** Getter */
    public int getFollowers() {
        return followers;
    }

}
