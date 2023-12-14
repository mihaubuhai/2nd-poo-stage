package output.result;

import fileio.input.SongInput;
import input.commands.CommandIn;

import java.util.ArrayList;

/**
 *      Aceasta clasa este folosita pentru output-ul comenzii
 *      "showPreferedSongs"
 * */
public class ResultPreferedSongs extends ResultCommand {
    private ArrayList<String> result;

    public ResultPreferedSongs(final CommandIn command, final ArrayList<SongInput> likes) {
        super(command);
        result = new ArrayList<>();
        likes.forEach(song -> result.add(song.getName()));
    }

    /** Setter */
    public void setResult(final ArrayList<String> result) {
        this.result = new ArrayList<>(result);
    }

    /** Getter */
    public ArrayList<String> getResult()  {
        return result;
    }

}
