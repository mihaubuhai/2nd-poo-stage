package top5;

import fileio.input.LibraryInput;
import input.commands.CommandIn;
import output.result.ResultGetTop5;
import player.commands.Like;
import top5.interfaces.Visitable;

import java.util.ArrayList;
import java.util.Collections;

/**
 *      Clasa a fost creata pentru a executa comanda "getTop5Songs"
 * */
public final class getTopSongs extends getTop implements Visitable {
    private ArrayList<Like> topLikedSongs;
    private LibraryInput library;

    public getTopSongs(final CommandIn cmd, final LibraryInput library,
                       final ArrayList<Like> topLikedSongs) {
        super(cmd);
        this.library = library;
        this.topLikedSongs = topLikedSongs;
    }

    public ResultGetTop5 accept(final topCreatorVisitor v) {
        return v.visit(this);
    }

    public ArrayList<Like> getTopLikedSongs() {
        return topLikedSongs;
    }

    public LibraryInput getLibrary() {
        return library;
    }

}
