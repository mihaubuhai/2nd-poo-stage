package output;

import playlist.commands.collections.SongsCollection;

import java.util.ArrayList;

/** Clasa de mai jos este necesara pentru afisarea colectiilor de melodii */
public class SongsCollOutput {
        protected String name;
        protected ArrayList<String> songs;

        public SongsCollOutput(final SongsCollection collection) {
            name = collection.getName();
            songs = new ArrayList<>();
            collection.getSongs().forEach(song -> songs.add(song.getName()));
        }

        /** Getter */
        public String getName() {
            return name;
        }

        /** Getter */
        public ArrayList<String> getSongs() {
            return songs;
        }
}
