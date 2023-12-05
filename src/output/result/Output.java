package output.result;

/**
 *      Aceasta clasa contine toate campurile comune comenzilor de output
 * */
public class Output {
    private String command;
    private int timestamp;

    /** Setter */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /** Getter */
    public int getTimestamp() {
        return timestamp;
    }

    /** Setter */
    public void setCommand(final String command) {
        this.command = command;
    }

    /** Getter */
    public String getCommand() {
        return command;
    }
}
