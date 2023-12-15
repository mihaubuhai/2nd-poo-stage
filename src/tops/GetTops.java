package tops;

import input.commands.CommandIn;

/**
 *  Clasa care contine campul comun tuturor claselor
 *  ce ajuta la implementarea comenzilor de statistici generale
 *  */
public class GetTops {
    private CommandIn cmd;

    public GetTops(final CommandIn cmd) {
        this.cmd = cmd;
    }

    /** Getter */
    public CommandIn getCmd() {
        return cmd;
    }
}
