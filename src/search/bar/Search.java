package search.bar;
import fileio.input.SongInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import input.commands.CommandIn;
import input.commands.Filters;
import main.users.NormalUser;
import main.users.UserInfo;
import output.result.ResultOutSearch;
import playlist.commands.collections.Playlist;

import java.util.ArrayList;

/** Aceasta clasa are un unic scop si anume executarea comenzii "Search" */
public final class Search {
    private static Search instance = null;

    private Search() {

    }

    /** Clasa este singletton, se returneaza instanta acesteia */
    public static Search getInstance() {
        if (instance == null) {
            instance = new Search();
        }
        return instance;
    }

    /**
     *   Aceasta metoda implementeaza comanda "search".
     *   Aceasta returneaza o clasa de forma mesajului de output cautat
     */
    public ResultOutSearch searchFunc(final LibraryInput library, final CommandIn command,
                                       final ArrayList<UserInfo> users) {
        ResultOutSearch output = new ResultOutSearch(command);
        final int maxSize = 5;

        /* Verifica tipul comenzii pentru a utiliza functia de verificare de filtre corespunzator*/
        if (command.getType().contains("song")) {
            /* Itereaza prin lista de melodii din librarie */
            for (SongInput song: library.getSongs()) {
                SongInput result = checkFiltersSongs(command.getFilters(), song);

                /*
                *   Se intampla ca melodia curenta sa nu respecte cel putin un filtru impus,
                *   deci functia "checkFilters_Songs" returneaza "null".
                */
                if (result != null) {
                    output.addResult(result.getName());
                }

                /*
                    Precum in cerinta specificat, mai mult de ..
                    ..5 rezultate nu pot fi afisate pentru o cautare.
                */
                if (output.getResults().size() == maxSize) {
                    break;
                }
            }

        } else if (command.getType().contains("podcast")) {
            /* Iterare prin lista de podcasturi din librarie */
            for (PodcastInput podcast: library.getPodcasts()) {
                PodcastInput result = checkFiltersPodcasts(command.getFilters(), podcast);

                if (result != null) {
                    output.addResult(result.getName());
                }
                if (output.getResults().size() == maxSize) {
                    break;
                }
            }
        } else {
            for (UserInfo commandsUser: users) {
                for (Playlist playlist : ((NormalUser)commandsUser).getPlaylists()) {
                    Playlist result = checkFiltersPlaylist(command.getFilters(), playlist);

                    /*Macar un filtru nerespectat rezulta in "result" fiind initializat cu null */
                    if (result != null) {
                        String visibility = result.getVisibility().toLowerCase();
                        if (checkVisibility(command.getUsername(), result)) {
                            /* Rezultatele cautarii se afiseaza doar daca playlist-ul este public */
                            output.addResult(result.getName());
                        }
                    }

                    if (output.getResults().size() == maxSize) {
                        break;
                    }
                }
            }
        }

        output.setMessage("Search returned " + output.getResults().size() + " results");
        return output;
    }

    /**
     *      Aceasta metoda verifica daca playlist-ul ce a satisfacut conditiile din "filters" ..
     *      este privat si al user-ului care a dat comanda
     */
    private boolean checkVisibility(final String username, final Playlist playlist) {
        String visibility = playlist.getVisibility();
        String owner = playlist.getUser();

        /* Indiferent de vizibilitate, user-ul care a creat playlist-ul il poate accesa */
        if (owner.equals(username)) {
            return true;
        }
        /* Daca playlist-ul este public, poate fi accesat de oricine */
        if (visibility.equals("public")) {
            return true;
        }

        /*
            Ramane ca visibility sa fie private, dar pentru owner-ul playlist-ului ..
            .. nu se ajunge aici, deci se returneaza false
        */
        return false;
    }

    /**
     * Metoda de mai jos verifica daca "song" satisface cautarea conditionata de "filters".
     * Variabila "valid_filters" este raspunzatoare de validarea filtrelor comenzii curente.
     * Adica, daca una din conditiile "impuse" de "filters" nu este respectata, melodia
     * nu satisface cererea utilizatorului, lucru sustinut si bazat pe valoarea "valid_filters",
     * deci aceasta melodie nu va fi adaugata in rezultatul comenzii "search".
     * Este necesar si scopul cautarii ca "valid_filters" sa fie egal cu ..
     * .. numarul de campuri nenule ale "filters"
     */
    private static SongInput checkFiltersSongs(final Filters filters, final SongInput song) {
        int validFilters = 0;

        if (filters.getName() != null) {
            if (song.getName().startsWith(filters.getName())) {
                validFilters++;
            }
        }

        if (filters.getAlbum() != null) {
            if (song.getAlbum().equals(filters.getAlbum())) {
                validFilters++;
            }
        }

        if (!filters.getTags().isEmpty()) {
            int tagsFound = 0;

            for (String tag: song.getTags()) {
                for (int idx = 0; idx < filters.getTags().size(); idx++) {
                    String filterTag = filters.getTags().get(idx);
                    if (tag.toLowerCase().equals(filterTag)) {
                        tagsFound++;
                        break;
                    }
                }
            }

            if (tagsFound == filters.getTags().size()) {
                validFilters++;
            }
        }

        if (filters.getLyrics() != null) {
            if (song.getLyrics().toLowerCase().contains(filters.getLyrics().toLowerCase())) {
                validFilters++;
            }
        }

        if (filters.getGenre() != null) {
            if (song.getGenre().toLowerCase().contains(filters.getGenre().toLowerCase())) {
                validFilters++;
            }
        }

        if (filters.getReleaseYear() != null) {             // filters.getRealeaseYear = ">/<2000"
            String releaseYear = filters.getReleaseYear().substring(1);         // 2000
            if (filters.getReleaseYear().contains("<")) {           // < 2000
                if (song.getReleaseYear() < Integer.parseInt(releaseYear)) {
                    validFilters++;
                }
            } else {       // > 2000
                if (song.getReleaseYear() > Integer.parseInt(releaseYear)) {
                    validFilters++;
                }
            }
        }

        if (filters.getArtist() != null) {
            if (song.getArtist().equals(filters.getArtist())) {
                validFilters++;
            }
        }

        if (validFilters == filters.getNonNullFields()) {
            return song;
        }
        return null;
    }

    /**
     *  Metoda de mai jos verifica conditiile impuse de user-ul care ..
     *  .. a solicitat "search" pentru un playlist
     *  */
    private static Playlist checkFiltersPlaylist(final Filters filters, final Playlist playlist) {
        int validFilters = 0;

        if (filters.getOwner() != null) {
            if (playlist.getUser().contains(filters.getOwner())) {
                validFilters++;
            }
        }

        if (filters.getName() != null) {
            if (playlist.getName().contains(filters.getName())) {
                validFilters++;
            }
        }

        if (validFilters == filters.getNonNullFields()) {
            return playlist;
        }

        return null;
    }

    /**
    *   Metoda curenta este similara cu "checkFilters_Songs".
    *   Metoda verifica campurile lui "podcast", in parte, relativ la campurile ...
     *  ... conditionarii "filters".
     */
    private static PodcastInput checkFiltersPodcasts(final Filters filters,
                                                                    final PodcastInput podcast) {
        int validFilters = 0;

        if (filters.getOwner() != null) {
            if (podcast.getOwner().contains(filters.getOwner())) {
                validFilters++;
            }
        }

        if (filters.getName() != null) {
            if (podcast.getName().contains(filters.getName())) {
                validFilters++;
            }
        }

        if (validFilters == filters.getNonNullFields()) {
            return podcast;
        }
        return null;
    }
}
