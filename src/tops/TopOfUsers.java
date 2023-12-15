package tops;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import tops.interfaces.Visitable;
import users.UserInfo;

import java.util.ArrayList;

/**
 * Clasa ce contine lista totala de useri si
 * tipul de statistica generala (getAllUsers, getOnlineUsers)...
 * */
public class TopOfUsers extends GetTops implements Visitable {
    private ArrayList<UserInfo> users;
    private TopType type;       // ---v
    /* 1 - allUsers  ;  2 - OnlineUsers  ;  3 - top5Artists */

    public enum TopType {
        ALLUSERS,
        ONLINEUSERS,
        TOPARTIST,
    }

    public TopOfUsers(final CommandIn cmd, final ArrayList<UserInfo> users,
                      final TopType type) {
        super(cmd);
        this.users = users;
        this.type = type;
    }

    /** Metoda care completeaza "Visitor Pattern" */
    public ResultGetTop5 accept(final TopCreatorVisitor v) {
        return v.visit(this);
    }

    /** Getter */
    public ArrayList<UserInfo> getUsers() {
        return users;
    }

    /** Getter */
    public TopType getType() {
        return type;
    }
}
