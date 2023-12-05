package output.result;

import input.commands.CommandIn;
import main.users.UserInfo;
import main.users.NormalUser;
import playlist.commands.collections.Playlist;

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

    public ResultShowPlaylists(final CommandIn command, final UserInfo user) {
        super(command);
        result = new ArrayList<>();
        ((NormalUser)user).getPlaylists().forEach(playlist ->
                result.add(new PlaylistOutput(playlist)));
    }

    /** Getter */
    public ArrayList<PlaylistOutput> getResult() {
        return result;
    }

    /** Clasa de mai jos este necesara pentru executarea comenzii de afisare a playlist-urilor */
    private class PlaylistOutput {
        private String name;
        private ArrayList<String> songs;
        private String visibility;
        private int followers;

        public PlaylistOutput(final Playlist playlist) {
            name = playlist.getName();
            visibility = playlist.getVisibility();
            followers = playlist.getFollowers();
            songs = new ArrayList<>();
            playlist.getSongs().forEach(song -> songs.add(song.getName()));
        }

        /** Getter */
        public String getName() {
            return name;
        }

        /** Getter */
        public ArrayList<String> getSongs() {
            return songs;
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
