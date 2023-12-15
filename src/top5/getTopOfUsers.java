package top5;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import top5.interfaces.Visitable;
import users.UserInfo;

import java.util.ArrayList;

public class getTopOfUsers extends getTop implements Visitable {
    private ArrayList<UserInfo> users;
    private TopType type;       // <-- Useri online sau toti userii
    /* 1 - allUsers  ;  2 - OnlineUsers  ;  3 - top5Artists */

    public enum TopType {
        ALLUSERS,
        ONLINEUSERS,
        TOPARTIST,
    }

    public getTopOfUsers(final CommandIn cmd, final ArrayList<UserInfo> users,
                         final TopType type) {
        super(cmd);
        this.users = users;
        this.type = type;
    }

    public ResultGetTop5 accept(final topCreatorVisitor v) {
        return v.visit(this);
    }

    public ArrayList<UserInfo> getUsers() {
        return users;
    }

    public TopType getType() {
        return type;
    }
}
