package output.result;

import input.commands.CommandIn;
import users.NormalUser;
import songcollections.collections.Playlist;
import output.SongsCollOutput;

import java.util.ArrayList;

/**
 *      Aceasta clasa este folosita pentru afisarea output-ului comenzii "showPlaylists"
 * */
public class ResultShowPlaylists extends  ResultCommand {
    private ArrayList<PlaylistOutput> result;

    public ResultShowPlaylists(final CommandIn command) {
        super(command);
        result = new ArrayList<>();
    }

    public ResultShowPlaylists(final CommandIn command, final NormalUser user) {
        super(command);
        result = new ArrayList<>();
        user.getPlaylists().forEach(playlist -> result.add(new PlaylistOutput(playlist)));
    }

    /** Getter */
    public ArrayList<PlaylistOutput> getResult() {
        return result;
    }

    /** Clasa de mai jos este necesara pentru executarea comenzii de afisare a playlist-urilor */
    private class PlaylistOutput extends SongsCollOutput {
        private String visibility;
        private int followers;

        PlaylistOutput(final Playlist playlist) {
            super(playlist);
            visibility = playlist.getVisibility();
            followers = playlist.getFollowers();
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

}
