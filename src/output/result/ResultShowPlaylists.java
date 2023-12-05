package output.result;

import input.commands.CommandIn;
import main.users.UserInfo;
import main.users.NormalUser;

import java.util.ArrayList;

/**
 *      Aceasta clasa este folosita pentru afisarea output-ului comenzii "showPlaylists"
 * */
public class ResultShowPlaylists extends  ResultCommand {
    private ArrayList<PlaylistOutput> result;

    public ResultShowPlaylists(final CommandIn command, final UserInfo user) {
        super(command);
        result = new ArrayList<>();
        ((NormalUser)user).getPlaylists().forEach(playlist ->
                result.add(new PlaylistOutput(playlist)));
    }

    /** Getter */
    public ArrayList<PlaylistOutput> getResult() {
        return result;
    }

}
