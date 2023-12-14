package search.bar;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.Artist;
import main.users.Host;
import main.users.UserInfo;
import main.users.NormalUser;
import main.users.pages.ArtistPage;
import main.users.pages.HostPage;
import main.users.pages.Page;
import output.result.ResultOut;
import playlist.commands.collections.Album;
import playlist.commands.collections.Playlist;
import playlist.commands.collections.SongsCollection;

import java.util.ArrayList;

/**
 *      Metoda de mai jos este esentiala rularii programului.
 *      Aceasta are rolul de a executa comanda "select".
 *      Clasa "Select" este folosita pentru crearea clasei "Player" atunci cand..
 *      .. se incarca ceva
 * */
public class Select {
    private boolean searchDone;                // <-- Daca s-a efectuat search anterior
    private boolean selected;                       // <-- Daca s-a selectat ceva
    private String user;
    private SongInput song;
    private PodcastInput podcast;
    private SongsCollection songsCollection;
    private ArrayList<String> searchResult;
    private int resultType;                 // <-- Contorizeaza ce anume este incarcat in player
    private UserInfo artistHostName;   // <-- Numele artist-ului / host-ului selectat
    /* 1 - melodie      2 - podcast     3 - playlist / album */
    public Select() {
    }

    public Select(final CommandIn command, final ArrayList<String> result) {
        setSearchDone(true);
        setUser(command.getUsername());
        setSearchResult(result);
        /* Linia de mai sus salveaza rezultatul efectiv al cautarii (nume melodii / podcast) */
        setResultType(command);
        artistHostName = null;
    }

    public Select(final Select otherSelect) {
        resultType = otherSelect.getResultType();
        setSearchDone(otherSelect.getSearchDone());
        setSelected(otherSelect.getSelected());
        setUser(otherSelect.getUser());
        setSong(otherSelect.getSong());
        setPodcast(otherSelect.getPodcast());
        setSongsCollection(otherSelect.getSongsCollection());
        setSearchResult(otherSelect.getSearchResult());
        setArtistHostName(otherSelect.getArtistHostName());
    }

    /** Setter */
    public void setResultType(final CommandIn command) {
        /* 1 - song     2 - podcast     3 - playlist */
        if (command.getType().contains("song")) {
            resultType = 1;
        } else if (command.getType().contains("podcast")) {
            resultType = 2;
        } else if (command.getType().contains("playlist") || command.getType().contains("album")){
            resultType = 3;
        } else {
            resultType = 0;
        }
    }

    /**
     *    Aceasta metoda implementeaza comanda "select".
     *    <p>
     *    Aceasta returneaza un obiect pe tiparul output-ului comenzii.
     * */
    public ResultOut selectFunc(final CommandIn command, final LibraryInput library,
                                                final ArrayList<UserInfo> users,
                                final NormalUser currUser ) {
        /* Declarare + initializare valoare de retur a metodei */
        ResultOut result = new ResultOut(command);
        final int songId = 1;
        final int podcastId = 2;
        final int songCollId = 3;

        if (command.getItemNumber() > getSearchResult().size()) {
            result.setMessage("The selected ID is too high.");
        } else {
            /* In acest moment, melodia / podcast este selectata */
            setSelected(true);
            if (resultType == songId) {
                        /*  v--- Melodia ce se vrea selectata */
                String selectedSong = getSearchResult().get(command.getItemNumber() - 1);
                /* Se itereaza prin lista de melodii din biblioteca si se salveaza referinta */
                for (SongInput song: library.getSongs()) {
                    String libSongName = song.getName();
                    if (libSongName.equals(selectedSong)) {
                        setSong(song);      // <--- salvam referinta catre melodia selectata
                        result.setMessage("Successfully selected " + libSongName + ".");
                        break;
                    }
                }

                /* Se poate ca melodia ce se vrea cautata sa nu fie in librarie; Cautam in lista artistilor */
                for (UserInfo tempUser: users) {
                    if (tempUser.isArtist()) {
                        Artist artist = (Artist) tempUser;
                        for (Album album: artist.getAlbums()) {
                            for (SongInput song: album.getSongs()) {
                                String albumSongName = song.getName();
                                if (albumSongName.equals(selectedSong)) {
                                    setSong(song);
                                    setArtistHostName(artist);
                                    setSongsCollection(album);
                                    // ^-- Se retine faptul ca user-ul asculta ceva ce nu este in librarie
                                    result.setMessage("Successfully selected " + albumSongName + ".");
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (resultType == podcastId) {
                /* Similar cu cazul "result_type == 1" */
                String selectedPodcast = getSearchResult().get(command.getItemNumber() - 1);
                /* Se itereaza prin lista de podcast-uri si se salveaza referinta catre podcast */
                for (PodcastInput podcast: library.getPodcasts()) {
                    String libPodcastName = podcast.getName();
                    if (libPodcastName.equals(selectedPodcast)) {
                        setPodcast(podcast);
                        result.setMessage("Successfully selected " + libPodcastName + ".");
                        break;
                    }
                }

                /* Podcastul care se vrea selectat nu se afla in librarie; cautam la hosts */
                for (UserInfo user : users) {
                    if (user.isHost()) {
                        for (PodcastInput podcast : ((Host) user).getPodcasts()) {
                            if (podcast.getName().equals(selectedPodcast)) {
                                setPodcast(podcast);
                                setArtistHostName(user);
                                result.setMessage("Successfully selected " + podcast.getName() +
                                        ".");
                                break;
                            }
                        }
                    }
                }
            } else if (resultType == songCollId) {
                /* Aici se selecteaza fie playlist, fie album */
                int id = command.getItemNumber();
                String potentialName = getSearchResult().get(id - 1);
                String msg;

                for (UserInfo user: users) {
                    /* Verificam intai daca se da select la un playlist */
                    if (user.isNormalUser()) {
                        for (Playlist playlist: ((NormalUser) user).getPlaylists()) {
                            if ((msg = verifyCollection(playlist, potentialName)) != null) {
                                setArtistHostName(user);
                                result.setMessage(msg);
                                break;
                            }
                        }
                    } else if (user.isArtist()) {
                        /* Verificam daca se da select la un album */
                        for (Album album: ((Artist) user).getAlbums()) {
                            if ((msg = verifyCollection(album, potentialName)) != null) {
                                setArtistHostName(user);
                                result.setMessage(msg);
                                break;
                            }
                        }
                    }
                }
            } else {
                /* Se selecteaza artist / host */
                String name = getSearchResult().get(command.getItemNumber() - 1);

                /* Se cauta artist / host */
                for (UserInfo user: users) {
                    if (user.getUsername().equals(name)) {
                        /*
                            Pentru a afisa pagina corespunzator, se schimba campul ce desemneaza ..
                            .. pagina al user-ului care a invocat comanda
                        */
                        if (user.isArtist()) {
                            currUser.setCurrentPage(new ArtistPage(user, Page.PageType.ARTIST));
                        } else {
                            currUser.setCurrentPage(new HostPage(user, Page.PageType.HOST));
                        }
                        result.setMessage("Successfully selected " + name + "'s page.");
                        setArtistHostName(user);
                    }
                }
            }
        }

        currUser.setSelectInfo(this);
        return result;
    }

    /**
     *  <p>Metoda folosita pentru selectarea unei colectii de melodii</p>
     *  Returneaza mesajul care se afiseaza ca rezultat al comenzii "select"
     *   */
    private String verifyCollection(final SongsCollection collection, final String potentialName) {
        String collectionName = collection.getName();
        if (collectionName.equals(potentialName)) {
            setSongsCollection(collection);
            return "Successfully selected " + collectionName + ".";
        }
        return null;
    }

    /**
     *  <p>
     *  Metoda are rolul de a decrementa numarul de ascultatori al unui podcast / playlist / album
     *  <p>
     *  In functie de ce este incarcat, metoda va accesa obiectul corespunzator,
     *  lucru verificabil prin campul resultType
     */
    public void decrementNrListeners() {
        if (artistHostName == null) {
            // ^-- Ceea ce este incarcat in player este din librarie, nu influenteaza pe nimeni
            return;
        }
        switch (resultType) {
            case 1 -> {
                /* S-a selectat o melodie dintr-un album / playlist */
                String album = song.getAlbum();
                SongsCollection tempRef = findAlbum(album);
                tempRef.decrementNrListeners();
            }
            case 2 -> podcast.decrementNrListeners();
            /* Pentru cazul 3, ceea ce ruleaza este o colectie de melodii; Se apeleaza direct metoda */
            case 3 -> songsCollection.decrementNrListeners();
        }
    }

    /** Metoda similara cu "decrementNrListeners" */
    public void incrementNrListeners() {
        if (artistHostName == null) {
            return;
        }

        switch (resultType) {
            case 1 -> {
                String album = song.getAlbum();
                Album tempRef = findAlbum(album);
                tempRef.incrementNrListeners();
            }
            case 2 -> podcast.incrementNrListeners();
            case 3 -> songsCollection.incrementNrListeners();
        }
    }

    private Album findAlbum(final String albumName) {
        for (Album album: ((Artist) artistHostName).getAlbums()) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return new Album(null);
    }

    /** Getter */
    public int getResultType() {
        return resultType;
    }

    /** Setter */
    public void setSearchDone(final boolean searchDone) {
        this.searchDone = searchDone;
    }

    /** Getter */
    public boolean getSearchDone() {
        return searchDone;
    }

    /** Setter */
    public void setSelected(final boolean status) {
        this.selected = status;
    }

    /** Getter */
    public boolean getSelected() {
        return selected;
    }

    /** Setter */
    public void setSong(final SongInput song) {
        this.song = song;
    }

    /** Getter */
    public SongInput getSong() {
        return song;
    }

    /** Setter */
    public void setUser(final String user) {
        this.user = user;
    }

    /** Setter */
    public void setArtistHostName(final UserInfo name) {
        artistHostName = name;

    }

    /** Getter */
    public UserInfo getArtistHostName() {
        return artistHostName;
    }

    /** getter */
    public String getUser() {
        return user;
    }

    /** Setter */
    public void setPodcast(final PodcastInput podcast) {
        this.podcast = podcast;
    }

    /** Getter */
    public PodcastInput getPodcast() {
        return podcast;
    }

    /** Setter */
    public void setSearchResult(final ArrayList<String> result) {
        searchResult = result;
    }

    /** Getter */
    public ArrayList<String> getSearchResult() {
        return searchResult;
    }

    /** Setter */
    public void setSongsCollection(final SongsCollection collection) {
        songsCollection = collection;
    }

    /** Getter */
    public Playlist getPlaylist() {
        return (Playlist) songsCollection;
    }

    /** Getter */
    public SongsCollection getSongsCollection() {
        return songsCollection;
    }

}
