package tops;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import songcollections.collections.Album;
import tops.interfaces.Visitable;

import java.util.ArrayList;

/** Clasa ce contine lista de albume, folosita pentru implementarea comenzii "getTop5Albums" */
public class TopAlbums extends GetTops implements Visitable {
    private ArrayList<Album> topAlbums;

    public TopAlbums(final CommandIn cmd, final ArrayList<Album> topAlbums) {
        super(cmd);
        this.topAlbums = topAlbums;
    }

    /** Metoda care face parte din design pattern-ul "Visitor" */
    public ResultGetTop5 accept(final TopCreatorVisitor v) {
        return v.visit(this);
    }

    /** Getter */
    public ArrayList<Album> getTopAlbums() {
        return topAlbums;
    }
}
