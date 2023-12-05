package output.result;

import input.commands.CommandIn;

/**
 *      Aceasta clasa contine campul "user" din output-ul majoritatii comenzilor
 * */
public class ResultCommand extends Output {
    private String user;

    public ResultCommand(final CommandIn command) {
        setCommand(command.getCommand());
        setUser(command.getUsername());
        setTimestamp(command.getTimestamp());
    }

    /** Setter */
    public void setUser(final String user) {
        this.user = user;
    }

    /** Getter */
    public String getUser() {
        return user;
    }
}
