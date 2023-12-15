package output.result;

import input.commands.CommandIn;
import users.Artist;
import output.SongsCollOutput;

import java.util.ArrayList;

/** Clasa folosita pentru afisarea albumelor unui artist */
public class ResultShowAlbums extends ResultCommand {
    private ArrayList<SongsCollOutput> result;

    public ResultShowAlbums(final CommandIn command) {
        super(command);
        result = new ArrayList<>();
    }

    public ResultShowAlbums(final CommandIn command, final Artist artist) {
        super(command);
        result = new ArrayList<>();
        artist.getAlbums().forEach(album -> result.add(new SongsCollOutput(album)));
    }

    /** Getter */
    public ArrayList<SongsCollOutput> getResult() {
        return result;
    }
}
