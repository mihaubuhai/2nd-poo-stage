package output.result;

import input.commands.CommandIn;
import player.Stats;

/**
 *      Clasa curenta este folosita pentru output-ul comenzii "status"
 */
public class ResultStatus extends ResultCommand {
    private Stats stats;

    public ResultStatus(final CommandIn command) {
        super(command);
    }

    /** Setter */
    public void setStats(final Stats status) {
        this.stats = new Stats(status);
    }

    /** Getter */
    public Stats getStats() {
        return stats;
    }
}
