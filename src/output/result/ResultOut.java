package output.result;

import input.commands.CommandIn;
/**
 *          "ResultOut" este o clasa conceputa pentru a fi folosita ..
 *      .. la scrierea rezultatelor comenzilor de tipul "select", "load".
 */
public class ResultOut extends ResultCommand {
    private String message;

    public ResultOut(final CommandIn command, final String msg) {
        super(command);
        setMessage(msg);
    }

    public ResultOut(final CommandIn command) {
        super(command);
    }

    /** Getter */
    public String getMessage() {
        return message;
    }

    /** Setter */
    public void setMessage(final String message) {
        this.message = message;
    }

}
