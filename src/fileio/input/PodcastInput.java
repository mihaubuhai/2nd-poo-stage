package fileio.input;

import java.util.ArrayList;

public final class PodcastInput {
    private String name;
    private String owner;
    private ArrayList<EpisodeInput> episodes;
    private int nrListeners;    // <-- Nr de ascultatori al acestui podcast

    public PodcastInput() {
    }

    public boolean isInPodcast(final EpisodeInput currEpisode, final PodcastInput podcast) {
        for (EpisodeInput episode: podcast.getEpisodes()) {
            if (episode.getName().equals(currEpisode.getName())) {
                return true;
            }
        }
        return false;
    }

    public void incrementNrListeners() {
        nrListeners++;
    }

    public void decrementNrListeners() {
        if (nrListeners > 0) {
            nrListeners--;
        }
    }

    public int retrieveNrListeners() {
        return nrListeners;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        return "PodcastInput{"
                + "name='" + name + '\''
                + ", owner='" + owner + '\''
                + ", episodes=" + episodes
                + '}';
    }
}
