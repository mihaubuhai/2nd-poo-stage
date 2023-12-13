package output;

import fileio.input.PodcastInput;
import java.util.ArrayList;

/** Clasa folosita pentru afisarea unui podcast */
public class PodcastOutput {
    private String name;
    private ArrayList<String> episodes;

    public PodcastOutput(final PodcastInput podcast) {
        name = podcast.getName();
        episodes = new ArrayList<>();
        podcast.getEpisodes().forEach(episode -> episodes.add(episode.getName()));
    }

    /** Getter */
    public String getName() {
        return name;
    }

    /** Setter */
    public ArrayList<String> getEpisodes() {
        return episodes;
    }

}
