package users;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;

import java.util.ArrayList;

public class Host extends UserInfo {
    private ArrayList<PodcastInput> podcasts;
    private ArrayList<String> announcementsName;
    private ArrayList<String> announcementsDescription;

    public Host(final UserInput user) {
        setUserInfo(user);
        userType = UserType.HOST;
        podcasts = new ArrayList<>();
        announcementsName = new ArrayList<>();
        announcementsDescription = new ArrayList<>();
    }

    /**
     *  Aceasta metoda implementeaza comanda "addPodcast"
     *  <p>
     *  Primeste ca parametru comanda care a fost invocata
     *  </p>
     *  Returneaza un obiect de tipul rezultatului asteptat la comanda "cmd"
     * */
    public ResultOut addPodcast(final CommandIn cmd, final UserInfo user) {
        ResultOut result = new ResultOut(cmd);

        if (!user.isHost()) {
            result.setMessage(user.getUsername() + " is not a host.");
        } else {
            Host host = (Host) user;
            String newPodcastName = cmd.getName();
            /* Verificam daca host-ul mai are un album cu acelasi nume */
            for (PodcastInput podcast : host.getPodcasts()) {
                if (podcast.getName().equals(newPodcastName)) {
                    result.setMessage(host.getUsername()
                            + " has another podcast with the same name.");
                    return result;
                }
            }

        /*
            Adaugam pe rand episoadele intr-un podcast nou si verificam
            la fiecare pas daca mai exista unul cu acelasi nume
        */
            PodcastInput newPodcast = new PodcastInput();
            newPodcast.setEpisodes(new ArrayList<>());
            for (EpisodeInput episode : cmd.getEpisodes()) {
                if (!newPodcast.isInPodcast(episode, newPodcast)) {
                    newPodcast.getEpisodes().add(episode);
                } else {
                    result.setMessage(host.getUsername()
                            + " has the same episode in this podcast.");
                    return result;
                }
            }

            /* Podcast-ul nu prezinta probleme, il vom adauga in lista host-ului */
            newPodcast.setName(cmd.getName());
            newPodcast.setOwner(host.getUsername());
            host.getPodcasts().add(newPodcast);
            result.setMessage(host.getUsername() + " has added new podcast successfully.");
        }

        return result;
    }

    /** Metoda care implementeaza comanda "removePodcast" <p>
     *    Primeste ca parametrii comanda si user-ul care a dat comanda <p>
     *    Returneaza un obiect de tipul rezultatului comenzii
     * */
    public ResultOut removePodcast(final CommandIn cmd, final UserInfo user) {
        ResultOut result = new ResultOut(cmd);

        if (!user.isHost()) {
            result.setMessage(user.getUsername() + " is not a host.");
        } else {
            /* Verificam daca host-ul are podcast-ul cu numele dat in cmd */
            Host host = (Host) user;
            for (PodcastInput podcast : host.getPodcasts()) {
                if (podcast.getName().equals(cmd.getName())) {
                    /* Verificam daca se poate sterge */

                    if (podcast.retrieveNrListeners() != 0) {
                        /* Un podcast se poate sterge daca nu are nici un ascultator */
                        result.setMessage(user.getUsername() + " can't delete this podcast.");
                        return result;
                    } else {
                        /* Podcast-ul nu este ascultat de nimeni, il putem sterge */
                        host.getPodcasts().remove(podcast);
                        result.setMessage(user.getUsername()
                                + " deleted the podcast successfully.");
                        return result;
                    }
                }
            }

            /* Ajunsi aici, inseamna ca nu exista podcast-ul cu numele dat in cmd */
            result.setMessage(user.getUsername() + " doesn't have a podcast with the given name.");
        }

        return result;
    }

    /**
     *      <p>
     *      Aceasta metoda adauga un nou anunt
     *      </p>
     *      Primeste ca parametru comanda invocata si returneaza rezultatul precum
     *      se doreste in fisierele .json
     * */
    public ResultOut addAnnouncement(final CommandIn cmd, final UserInfo user) {
        ResultOut result = new ResultOut(cmd);
        String newAnnounceName = cmd.getName();

        if (!user.isHost()) {
            result.setMessage(user.getUsername() + " is not a host.");
        } else {
            Host host = (Host) user;
            /* Verificam daca mai exista un anunt cu acelasi nume */
            for (String news : host.getAnnouncementsName()) {
                if (news.equals(newAnnounceName)) {
                    result.setMessage(host.getUsername()
                            + " has already added an announcement with this name.");
                    return result;
                }
            }

            /* Anuntul nu prezinta probleme; va fi adaugat in lista de anunturi */
            host.getAnnouncementsName().add(newAnnounceName);
            host.getAnnouncementsDescription().add(cmd.getDescription());
            result.setMessage(host.getUsername() + " has successfully added new announcement.");
        }
            return result;
    }

    /** Metoda care elimina un anunt
     *    <p>
     *      Primeste ca paramentru comanda data
     *      </p>
     *      Returneaza rezultatul comenzii
     *
     *  */
    public ResultOut removeAnnouncement(final CommandIn cmd, final UserInfo user) {
        ResultOut result = new ResultOut(cmd);
        boolean isValid = false;    //<-- Contorizeaza daca exista vreun anunt cu numele dat in cmd
        int idx = 0;

        if (!user.isHost()) {
            result.setMessage(user.getUsername() + " is not a host.");
            return result;
        }

        Host host = (Host) user;

        for (int i = 0; i < host.getAnnouncementsName().size(); ++i) {
            String news = host.getAnnouncementsName().get(i);
            if (news.equals(cmd.getName())) {
                isValid = true; //<-- Exista un anunt, deci se poate sterge
                idx = i;
                break;
            }
        }

        /* Verificam daca s-a gasit sau nu anuntul care se vrea sters */
        if (!isValid) {
            result.setMessage(getUsername() + " has no announcement with the given name.");
        } else {
            host.getAnnouncementsName().remove(idx);
            host.getAnnouncementsDescription().remove(idx);
            result.setMessage(getUsername() + " has successfully deleted the announcement.");
        }

        return result;
    }






    /** Getter */
    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }

    /** Getter */
    public ArrayList<String> getAnnouncementsName() {
        return announcementsName;
    }

    /** Getter */
    public ArrayList<String> getAnnouncementsDescription() {
        return announcementsDescription;
    }
}
