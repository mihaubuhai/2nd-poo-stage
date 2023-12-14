package top5.interfaces;

import output.result.ResultGetTop5;
import top5.getTopAlbums;
import top5.getTopPlaylists;
import top5.getTopSongs;
import top5.getTopOfUsers;

public interface Visitor {
    ResultGetTop5 visit(final getTopAlbums toVisit);
    ResultGetTop5 visit(final getTopPlaylists toVisit);
    ResultGetTop5 visit(final getTopSongs toVisit);
    ResultGetTop5 visit(final getTopOfUsers toVisit);
}