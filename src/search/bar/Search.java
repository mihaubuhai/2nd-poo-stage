package search.bar;
import fileio.input.SongInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import input.commands.CommandIn;
import input.commands.Filters;
import main.users.Artist;
import main.users.NormalUser;
import main.users.UserInfo;
import output.result.ResultOutSearch;
import playlist.commands.collections.Album;
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
     *   <p>
     *   Aceasta returneaza un obiect de forma mesajului de output cautat
     */
    public ResultOutSearch searchFunc(final LibraryInput library, final CommandIn cmd,
                                       final ArrayList<UserInfo> users) {
        ResultOutSearch output = new ResultOutSearch(cmd);
        final int maxSize = 5;

        /* Verifica tipul comenzii pentru a utiliza functia de verificare de filtre corespunzator*/
        if (cmd.getType().contains("song")) {
            findFittingSong(cmd, output, maxSize, library.getSongs());

            /*
                Iteram prin lista de albume a fiecarui artist si cautam ..
                .. doar daca nu s-a atins maximul de cautari
            */
            if (output.getResults().size() < maxSize) {
                for (UserInfo tempUser: users) {
                    if (tempUser.isArtist()) {
                        Artist artist = (Artist) tempUser;
                        for (Album album: artist.getAlbums()) {
                            findFittingSong(cmd, output, maxSize, album.getSongs());
                        }
                    }
                }
            }

        } else if (cmd.getType().contains("podcast")) {
            /* Iterare prin lista de podcasturi din librarie */
            for (PodcastInput podcast: library.getPodcasts()) {
                PodcastInput result = checkFiltersPodcasts(cmd.getFilters(), podcast);

                if (result != null) {
                    output.addResult(result.getName());
                }
                if (output.getResults().size() == maxSize) {
                    break;
                }
            }
        } else if (cmd.getType().equals("playlist")) {
            /* Se va cauta un playlist */
            for (UserInfo user: users) {
                if (user.isNormalUser()) {
                    NormalUser normalUser = (NormalUser) user;
                    for (Playlist playlist : normalUser.getPlaylists()) {
                        Playlist result = checkFiltersPlaylist(cmd.getFilters(), playlist);

                        /*Macar un filtru nerespectat rezulta in "result" fiind initializat cu null */
                        if (result != null) {
                            if (checkVisibility(cmd.getUsername(), result)) {
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
        } else if (cmd.getType().equals("album")) {
            /* Se va cauta un album */
            for (UserInfo user: users) {
                if (user.isArtist()) {
                    Artist artist = (Artist) user;
                    for (Album album : artist.getAlbums()) {
                        Album result = checkFiltersAlbum(cmd.getFilters(), album);

                        if (result != null) {
                            output.addResult(result.getName());
                        }

                        if (output.getResults().size() == maxSize) {
                            break;
                        }
                    }
                }
            }
        } else {
            /* Se cauta un artist */
            for (UserInfo user: users) {
                String filter = cmd.getFilters().getName();
                if (user.isArtist() && user.getUsername().startsWith(filter)) {
                    output.addResult(user.getUsername());
                }

                if (output.getResults().size() > maxSize) {
                    break;
                }
            }
        }

        output.setMessage("Search returned " + output.getResults().size() + " results");
        return output;
    }

    private void findFittingSong(CommandIn cmd, ResultOutSearch output, int maxSize, ArrayList<SongInput> songs) {
        /* Itereaza prin lista de melodii din librarie / album */
        for (SongInput song: songs) {
            SongInput result = checkFiltersSongs(cmd.getFilters(), song);

            /*
             *   Se intampla ca melodia curenta sa nu respecte cel putin un filtru impus, ..
             *   .. deci functia "checkFilters_Songs" returneaza "null".
             */
            if (result != null) {
                output.addResult(result.getName());
            }

            /* Mai mult de 5 rezultate nu se pot afisa, deci cautarea se opreste */
            if (output.getResults().size() == maxSize) {
                break;
            }
        }
    }

    /**
     *      Aceasta metoda verifica daca playlist-ul ce a satisfacut conditiile din "filters" ..
     *      este privat si al user-ului care a dat comanda
     */
    private boolean checkVisibility(final String username, final Playlist playlist) {
        String visibility = playlist.getVisibility();
        String owner = playlist.getOwner();

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
     * <p>
     * Adica, daca una din conditiile impuse de "filters" nu este respectata, melodia
     * nu satisface cererea utilizatorului, lucru sustinut si bazat pe valoarea "validFilters",
     * deci aceasta melodie nu va fi adaugata in rezultatul comenzii "search".
     * </p>
     * <p>
     * Este necesar si scopul cautarii ca "validFilters" sa fie egal cu
     * numarul de campuri nenule ale "filters"
     */
    private SongInput checkFiltersSongs(final Filters filters, final SongInput song) {
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
    private Playlist checkFiltersPlaylist(final Filters filters, final Playlist playlist) {
        int validFilters = 0;

        if (filters.getOwner() != null) {
            if (playlist.getOwner().contains(filters.getOwner())) {
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
    private PodcastInput checkFiltersPodcasts(final Filters filters,
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

    /**     <p>
     *  Metoda de mai jos verifica conditiile impuse de cautare pentru un album.
     *      </p>
     *  Metoda va returna parametrul "album" daca se respecta filtrele cerute, altfel null
     * */
    private Album checkFiltersAlbum(final Filters filters, final Album album) {
        int validFilters = 0;

        if (filters.getOwner() != null) {
            if (album.getOwner().contains(filters.getOwner())) {
                validFilters++;
            }
        }

        if (filters.getName() != null) {
            if (album.getName().contains(filters.getName())) {
                validFilters++;
            }
        }

        if (filters.getDescription() != null) {
            if (album.getDescription().contains(filters.getDescription())) {
                validFilters++;
            }
        }

        if (validFilters == filters.getNonNullFields()) {
            return album;
        }
        return null;
    }

}
