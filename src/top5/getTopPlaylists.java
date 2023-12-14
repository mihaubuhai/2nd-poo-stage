package top5;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import songcollections.commands.FollowStats;
import top5.interfaces.Visitable;

import java.util.ArrayList;
import java.util.Collections;

/**
 *      Unicul rol al acestei clase este pentru executarea comenzii "getTop5Playlists"
 * */
public class getTopPlaylists extends getTop implements Visitable {
    private ArrayList<FollowStats> topFwsPlaylists;

    public getTopPlaylists(final CommandIn cmd, final ArrayList<FollowStats> topFwsPlaylists) {
        super(cmd);
        this.topFwsPlaylists = topFwsPlaylists;
    }

    public ResultGetTop5 accept(final topCreatorVisitor v) {
        return v.visit(this);
    }

    public ArrayList<FollowStats> getTopFwsPlaylists() {
        return topFwsPlaylists;
    }

}
