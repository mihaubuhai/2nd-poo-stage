package main.users;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;

import java.util.ArrayList;

public class Host extends UserInfo {
    private ArrayList<PodcastInput> podcasts;
    private int nrListeners;

    public Host(final UserInput user) {
        setUserInfo(user);
        userType = UserType.HOST;
        podcasts = new ArrayList<>();
    }


    /**
     *  Aceasta metoda implementeaza comanda "addPodcast"
     *  Primeste ca parametru comanda care a fost invocata
     *  Returneaza un obiect de tipul rezultatului asteptat de comanda "cmd"
     * */
    public ResultOut addPodcast(final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        String newPodcastName = cmd.getName();
        /* Verificam daca host-ul mai are un album cu acelasi nume */
        for (PodcastInput podcast : podcasts) {
            if (podcast.getName().equals(newPodcastName)) {
                result.setMessage(getUsername() + " has another podcast with the same name.");
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
                result.setMessage(getUsername() + " has the same episode in this podcast.");
                return result;
            }
        }

        /* Podcast-ul nu prezinta probleme, il vom adauga in lista host-ului */
        newPodcast.setName(cmd.getName());
        newPodcast.setOwner(getUsername());
        podcasts.add(newPodcast);
        result.setMessage(getUsername() + " has added new podcast successfully.");
        return result;
    }







    /** Getter */
    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }
}
