package main.users;

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
    private int nrListeners;

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
                    result.setMessage(host.getUsername() + " has another podcast with the same name.");
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
                    result.setMessage(host.getUsername() + " has the same episode in this podcast.");
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
                    result.setMessage(host.getUsername() +
                            " has already added an announcement with this name.");
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

    public ResultOut removeAnnouncement(final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        boolean isValid = false;    //<-- Contorizeaza daca exista vreun anunt cu numele dat in cmd
        int idx = 0;

        for (int i = 0; i < announcementsName.size(); ++i) {
            String news = announcementsName.get(i);
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
            announcementsName.remove(idx);
            announcementsDescription.remove(idx);
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
