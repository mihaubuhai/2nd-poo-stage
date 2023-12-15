package tops;

import fileio.input.LibraryInput;
import input.commands.CommandIn;
import output.result.ResultGetTop5;
import player.commands.Like;
import tops.interfaces.Visitable;

import java.util.ArrayList;

/**
 *      Clasa a fost creata pentru a executa comanda "getTop5Songs"<p>
 *      Contine lista de piese apreciate de toti userii si biblioteca
 * */
public final class TopSongs extends GetTops implements Visitable {
    private ArrayList<Like> topLikedSongs;
    private LibraryInput library;

    public TopSongs(final CommandIn cmd, final LibraryInput library,
                    final ArrayList<Like> topLikedSongs) {
        super(cmd);
        this.library = library;
        this.topLikedSongs = topLikedSongs;
    }

    /** Visitor Pattern */
    public ResultGetTop5 accept(final TopCreatorVisitor v) {
        return v.visit(this);
    }

    /** Getter */
    public ArrayList<Like> getTopLikedSongs() {
        return topLikedSongs;
    }

    /** Getter */
    public LibraryInput getLibrary() {
        return library;
    }

}
