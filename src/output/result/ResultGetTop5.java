package output.result;

import input.commands.CommandIn;

import java.util.ArrayList;

/**
 *      Aceasta clasa contine campul "result" dar fara campul "user"
 * */
public class ResultGetTop5 extends Output {
    private ArrayList<String> result;

    public ResultGetTop5(final CommandIn command) {
        setCommand(command.getCommand());
        setTimestamp(command.getTimestamp());
        result = new ArrayList<>();
    }

    /** Getter */
    public ArrayList<String> getResult() {
        return result;
    }
}
