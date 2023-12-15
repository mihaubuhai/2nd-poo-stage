package search.bar;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import users.Artist;
import users.Host;
import users.UserInfo;
import users.NormalUser;
import users.pages.ArtistPage;
import users.pages.HostPage;
import users.pages.Page;
import output.result.ResultOut;
import songcollections.collections.Album;
import songcollections.collections.Playlist;
import songcollections.collections.SongsCollection;

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
    private UserInfo artistHostName;   // <-- Numele artist-ului / host-ului selectat
    private int resultType;                 // <-- Contorizeaza ce anume este incarcat in player
    /* 1 - melodie      2 - podcast     3 - playlist / album */

    private int searchResultsize;

    private ArrayList<SongInput> songsSearched;
    private ArrayList<SongsCollection> collectionsSearched;
    private ArrayList<PodcastInput> podcastsSearched;
    private ArrayList<UserInfo> usersSearched;

    public Select() {
    }

    public Select(final CommandIn command, final Search searchResults) {
        setSearchDone(true);
        setUser(command.getUsername());
        setResultType(command, searchResults);
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
    public void setResultType(final CommandIn command, final Search searchResults) {
        /* 1 - song     2 - podcast     3 - playlist */
        if (command.getType().contains("song")) {
            resultType = 1;
            songsSearched = searchResults.getSongsSearched();
            searchResultsize = songsSearched.size();
        } else if (command.getType().contains("podcast")) {
            resultType = 2;
            podcastsSearched = searchResults.getPodcastsSearched();
            searchResultsize = podcastsSearched.size();
        } else if (command.getType().contains("playlist") || command.getType().contains("album")){
            resultType = 3;
            collectionsSearched = searchResults.getCollectionsSearched();
            searchResultsize = collectionsSearched.size();
        } else {
            resultType = 0;
            usersSearched = searchResults.getUsersSearched();
            searchResultsize = usersSearched.size();
        }
    }

    /**
     *    Aceasta metoda implementeaza comanda "select".
     *    <p>
     *    Aceasta returneaza un obiect pe tiparul output-ului comenzii.
     * */
    public ResultOut selectFunc(final CommandIn cmd, final LibraryInput library,
                                                final ArrayList<UserInfo> users,
                                final NormalUser currUser ) {
        /* Declarare + initializare valoare de retur a metodei */
        ResultOut result = new ResultOut(cmd);

        if (cmd.getItemNumber() > searchResultsize) {
            result.setMessage("The selected ID is too high.");
            return result;
        }

        String selectionName;
        int idx = cmd.getItemNumber() - 1;
        selected = true;

        switch (resultType) {
            case 1 -> {
                /* Se selecteaza o melodie */
                song = songsSearched.get(idx);
                selectionName = song.getName();
                if (song.getIsInAlbum()) {
                    artistHostName = findOwner(song.getArtist(), users);
                }
            }
            case 2 -> {
                /* Se selecteaza un podcast */
                podcast = podcastsSearched.get(idx);
                selectionName = podcast.getName();
                artistHostName = findOwner(podcast.getOwner(), users);
            }
            case 3 -> {
                /* Se selecteaza o colectie de melodii */
                songsCollection = collectionsSearched.get(idx);
                selectionName = songsCollection.getName();
                artistHostName = songsCollection.getOwner();
            }
            default -> {
                /* Se selecteaza un artist / host */
                artistHostName = usersSearched.get(idx);
                selectionName = artistHostName.getUsername();
                if (artistHostName.isArtist()) {
                    currUser.setCurrentPage(new ArtistPage(artistHostName, Page.PageType.ARTIST));
                } else {
                    currUser.setCurrentPage(new HostPage(artistHostName, Page.PageType.HOST));
                }
                selectionName = selectionName + "'s page";
            }
        }

        result.setMessage("Successfully selected " + selectionName + ".");
        currUser.setSelectInfo(this);
        return result;
    }

    private UserInfo findOwner(final String audioFileOwner, final ArrayList<UserInfo> users) {
        for (UserInfo user : users) {
            if (user.getUsername().equals(audioFileOwner)) {
                return user;
            }
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
            /* Pentru cazul 2 si 3, ceea ce ruleaza este o colectie audio; Se apeleaza direct metoda */
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

    /** Setter */
    public void setSearchResult(final int size) {
        searchResultsize = size;
    }

    /** Getter */
    public int getSearchResult() {
        return searchResultsize;
    }

}
