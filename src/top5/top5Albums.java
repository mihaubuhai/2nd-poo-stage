package top5;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import playlist.commands.collections.Album;

import java.util.ArrayList;

public class top5Albums {
    private static top5Albums instance = null;

    private top5Albums() {
    }

    public static top5Albums getInstance() {
        if (instance == null) {
            instance = new top5Albums();
        }
        return instance;
    }

    public ResultGetTop5 getTop5(final CommandIn cmd, final ArrayList<Album> topAlbums) {
        ResultGetTop5 result = new ResultGetTop5(cmd);

        /* Sortam lista de albume */
        topAlbums.sort((o1, o2) -> {
            if (o1.getTotalLikes() == o2.getTotalLikes()) {
                return o2.getName().compareTo(o1.getName());
            }
            return o2.getTotalLikes() - o1.getTotalLikes();
        });

        /* Adaugam albumele in rezultat */
        topAlbums.forEach(album -> result.getResult().add(album.getName()));

        return result;
    }
}
