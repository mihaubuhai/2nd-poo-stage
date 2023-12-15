package songcollections.collections;

import input.commands.CommandIn;

/** Aceasta clasa face parte din Factory design pentru playlist si album */
public final class SongsCollFactory {
    private SongsCollFactory() {
    }

    /**
     *      Metoda care completeaza design pattern-ul "Factory" pentru
     *      colectia de melodii, extinsa de "Playlist" si "Album".
     * */
    public static SongsCollection getCollection(final CommandIn command) {
        String commandType = command.getCommand().toLowerCase();
        if (commandType.contains("playlist")) {
            return new Playlist(command);
        }
        return new Album(command);
    }
}
