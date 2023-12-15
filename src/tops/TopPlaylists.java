package tops;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import songcollections.commands.FollowStats;
import tops.interfaces.Visitable;

import java.util.ArrayList;

/**
 *      Unicul rol al acestei clase este pentru executarea comenzii "getTop5Playlists" <p>
 *      Contine lista de statistici ale tuturor playlist-urilor din program
 * */
public class TopPlaylists extends GetTops implements Visitable {
    private ArrayList<FollowStats> topFwsPlaylists;

    public TopPlaylists(final CommandIn cmd, final ArrayList<FollowStats> topFwsPlaylists) {
        super(cmd);
        this.topFwsPlaylists = topFwsPlaylists;
    }

    /** Metoda pentru design pattern Visitor */
    public ResultGetTop5 accept(final TopCreatorVisitor v) {
        return v.visit(this);
    }

    /** Getter */
    public ArrayList<FollowStats> getTopFwsPlaylists() {
        return topFwsPlaylists;
    }

}
