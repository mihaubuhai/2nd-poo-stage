package playlist.commands.collections;

import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;

public class Playlist extends SongsCollection {
    private String userName;        // <-- numele utilizatorului care a creat acest playlist
    private String visibility;
    private int followers;

    public Playlist(final CommandIn command) {
        super(command);
        setName(command.getPlaylistName());
        setUser(command.getUsername());
        setVisibility(new String("public"));
        followers = 0;
    }

    /** Setter */
    public void setUser(final String username) {
        userName = username;
    }
    /** Setter */
    public void setVisibility(final String type) {
        visibility = type;
    }

    /** Getter */
    public String getVisibility() {
        return visibility;
    }
    /** Getter */
    public String getUser() {
        return userName;
    }
    /** Getter */
    public int getFollowers() {
        return followers;
    }

    /** Metoda care incrementeaza numarul de urmaritori ai playlist-ului */
    public void incNrFollowers() {
        followers += 1;
    }
    /** Metoda care decrementeaza numarul de urmaritori ai playlist-ului */
    public void decNrFollowers() {
        if (followers > 0) {
            followers -= 1;
        }
    }

    /**
     *      Metoda de mai jos implementeaza comanda "switchVisibility"
     * */
    public static ResultOut switchVisibility(final CommandIn command, final NormalUser currentUser) {
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

}
