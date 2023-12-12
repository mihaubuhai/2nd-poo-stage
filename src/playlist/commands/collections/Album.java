package playlist.commands.collections;

import fileio.input.SongInput;
import input.commands.CommandIn;

public class Album extends SongsCollection {
    private String description;

    public Album(final CommandIn command) {
        super(command);
        setName(command.getName());
        setTimeOfCreation(command.getReleaseYear());
        setDescription(command.getDescription());
    }

    /** Metoda verifica daca in album se mai gaseste o melodie cu acelasi nume ca "song" */
    public boolean isInAlbum(SongInput song) {
        for (SongInput track: getSongs()) {
            if (track.getName().equals(song.getName())) {
                return true;
            }
        }
        return false;
    }

    /** Setter  */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Getter */
    public String getDescription() {
        return description;
    }
}
