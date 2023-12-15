package songcollections.collections;

import fileio.input.SongInput;
import input.commands.CommandIn;

public class Album extends SongsCollection {
    private String description;
    private int nrSongsUsed;
    // ^-- Retine cate melodii sunt componente unui playlist al unui user normal

    public Album(final CommandIn command) {
        super(command);
        setName(command.getName());
        setTimeOfCreation(command.getReleaseYear());
        setDescription(command.getDescription());
        isAlbum = true;
    }

    /** Metoda verifica daca in album se mai gaseste o melodie cu acelasi nume ca "song" */
    public boolean isInAlbum(final String song) {
        for (SongInput track: getSongs()) {
            if (track.getName().equals(song)) {
                return true;
            }
        }
        return false;
    }

    /** Setter  */
    public void setDescription(final String description) {
        this.description = description;
    }

    /** Getter */
    public String getDescription() {
        return description;
    }

    /** Metoda care incrementeaza numarul de utilizari al vreunei melodii din album */
    public void incrementNrSongsUsed() {
        nrSongsUsed++;
    }

    /** Metoda care decrementeaza numarul de utilizari al vreunei melodii */
    public void decrementNrSongsUsed() {
        if (nrSongsUsed > 0) {
            nrSongsUsed--;
        }
    }

    /** Getter */
    public int getNrSongsUsed() {
        return nrSongsUsed;
    }
}
