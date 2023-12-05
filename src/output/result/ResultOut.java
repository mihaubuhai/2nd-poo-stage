package output.result;

/*
    "ResultOut" este o clasa conceputa pentru a fi folosita la
    scrierea rezultatelor comenzilor de tipul "select", "load".
 */

import input.commands.CommandIn;

public class ResultOut extends ResultCommand {
    private String message;

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
