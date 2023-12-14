package output.result;

import input.commands.CommandIn;
import users.Host;
import output.PodcastOutput;

import java.util.ArrayList;

/** Clasa folosita pentru comanda "showPodcasts" */
public class ResultShowPodcasts  extends ResultCommand {
    private ArrayList<PodcastOutput> result;

    public ResultShowPodcasts(final CommandIn command) {
        super(command);
        result = new ArrayList<>();
    }

    public ResultShowPodcasts(final CommandIn command, final Host host) {
        super(command);
        result = new ArrayList<>();
        host.getPodcasts().forEach(podcast -> result.add(new PodcastOutput(podcast)));
    }

    /** Getter */
    public ArrayList<PodcastOutput> getResult() {
        return result;
    }
}

