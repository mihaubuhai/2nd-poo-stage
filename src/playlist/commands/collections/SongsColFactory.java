package playlist.commands.collections;

import input.commands.CommandIn;

/** Aceasta clasa face parte din Factory design pentru playlist si album */
public class SongsColFactory {
    private SongsColFactory() {
    }

    /**
     *      Metoda care completeaza design pattern-ul "Factory" pentru ..
     *      .. colectia de melodii, extinsa prin "Playlist" si "Album".
     * */
    public static SongsCollection getCollection(final CommandIn command) {
        String commandType = command.getCommand().toLowerCase();
        if (commandType.contains("playlist")) {
            return new Playlist(command);
        } else if (commandType.contains("album")) {
            return new Album(command);
        } else {
            return null;
        }
    }
}
