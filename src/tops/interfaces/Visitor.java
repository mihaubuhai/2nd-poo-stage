package tops.interfaces;

import output.result.ResultGetTop5;
import tops.TopAlbums;
import tops.TopPlaylists;
import tops.TopSongs;
import tops.TopOfUsers;

public interface Visitor {
    /** Visitor Pattern */
    ResultGetTop5 visit(TopAlbums toVisit);
    /** Visitor Pattern */
    ResultGetTop5 visit(TopPlaylists toVisit);
    /** Visitor Pattern */
    ResultGetTop5 visit(TopSongs toVisit);
    /** Visitor Pattern */
    ResultGetTop5 visit(TopOfUsers toVisit);
}
