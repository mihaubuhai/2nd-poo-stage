package top5;

import input.commands.CommandIn;
import javassist.compiler.ast.Visitor;
import output.result.ResultGetTop5;
import songcollections.collections.Album;
import top5.interfaces.Visitable;

import java.util.ArrayList;

public class getTopAlbums extends getTop implements Visitable {
    private ArrayList<Album> topAlbums;

    public getTopAlbums(final CommandIn cmd, final ArrayList<Album> topAlbums) {
        super(cmd);
        this.topAlbums = topAlbums;
    }

    public ResultGetTop5 accept(final topCreatorVisitor v) {
        return v.visit(this);
    }

    public ArrayList<Album> getTopAlbums() {
        return topAlbums;
    }
}
