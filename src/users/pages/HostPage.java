package users.pages;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import input.commands.CommandIn;
import users.Host;
import users.UserInfo;
import output.result.ResultOut;

import java.util.ArrayList;

/** Clasa de mai jos contine metoda care construieste pagina "Host" a unui user normal */
public class HostPage extends Page {
    public HostPage(final UserInfo user, final PageType type) {
        super(user, type);
    }

    /**
     *  Metoda care creeaza pagina "Host" a unui user normal
     *  <p>
     *   Primeste ca parametrii host-ul pe care user-ul a dat select si comanda in sine
     *   </p>
     *   Intoarce un obiect de tipul rezultatului asteptat al comenzii
     * */
    public ResultOut printPage(final Host host, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        pageContent = new StringBuilder("Podcasts:\n\t[");
        int tempSize = host.getPodcasts().size();
        int pageLength;

        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            for (int i = 0; i < tempSize; ++i) {
                PodcastInput podcast = host.getPodcasts().get(i);
                pageContent.append(podcast.getName());
                pageContent.append(":\n\t[");
                for (EpisodeInput episode : podcast.getEpisodes()) {
                    pageContent.append(episode.getName());
                    pageContent.append(" - ");
                    pageContent.append(episode.getDescription());
                    pageContent.append(", ");
                }

                /* La final, vor fi in plus caracterele: ", "  Le eliminam */
                pageLength = pageContent.length();
                pageContent.replace(pageLength - 2, pageLength, "]");
                pageContent.append("\n, ");
            }
            /* La final, vor fi in plus caracterele "\n, " <- 4 caractere */
            pageLength = pageContent.length();
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        pageContent.append("\n\nAnnouncements:\n\t[");
        tempSize = host.getAnnouncementsName().size();

        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            ArrayList<String> announcementsNames = host.getAnnouncementsName();
            ArrayList<String> announcementsDesc = host.getAnnouncementsDescription();
            for (int i = 0; i < tempSize; ++i) {
                pageContent.append(announcementsNames.get(i));
                pageContent.append(":\n\t");
                pageContent.append(announcementsDesc.get(i));
                pageContent.append("\n, ");
            }
            /* La final, vor fi in plus caractele: ", " */
            pageLength = pageContent.length();
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        result.setMessage(pageContent.toString());

        return result;
    }
}
