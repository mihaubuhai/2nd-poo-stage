package search.bar;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.UserInfo;
import main.users.NormalUser;
import output.result.ResultOut;
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
    /* 1 - melodie      2 - podcast     3 - playlist / album */
    public Select() {

    }

    public Select(final CommandIn command, final ArrayList<String> result) {
        setSearchDone(true);
        setUser(command.getUsername());
        setSearchResult(result);
        /* Linia de mai sus salveaza rezultatul efectiv al cautarii (nume melodii / podcast) */
        setResultType(command);
    }

    public Select(final Select otherSelect) {
        resultType = otherSelect.getResultType();
        setSearchDone(otherSelect.getSearchDone());
        setSelected(otherSelect.getSelected());
        setUser(otherSelect.getUser());
        setSong(otherSelect.getSong());
        setPodcast(otherSelect.getPodcast());
        setPlaylist(otherSelect.getPlaylist());
        setSearchResult(otherSelect.getSearchResult());
    }

    /** Setter */
    public void setResultType(final CommandIn command) {
        /* 1 - song     2 - podcast     3 - playlist */
        if (command.getType().contains("song")) {
            resultType = 1;
        } else if (command.getType().contains("podcast")) {
            resultType = 2;
        } else {
            resultType = 3;
        }
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
    public void setPlaylist(final Playlist playlist) {
        songsCollection = playlist;
    }

    /** Getter */
    public Playlist getPlaylist() {
        return (Playlist) songsCollection;
    }

    /** Getter */
    public SongsCollection getSongsCollection() {
        return songsCollection;
    }

    /**
     *    Aceasta metoda implementeaza comanda "select"
     *    Aceasta returneaza o clasa pe tiparul output-ului comenzii.
     * */
    public ResultOut selectFunc(final CommandIn command, final LibraryInput library,
                                                final ArrayList<UserInfo> users) {
        /* Declarare + initializare valoare de retur a metodei */
        ResultOut result = new ResultOut(command);
        final int songId = 1;
        final int podcastId = 2;

        if (command.getItemNumber() > getSearchResult().size()) {
            result.setMessage("The selected ID is too high.");
        } else {
            /* In acest moment, melodia / podcast este selectata si gata pentru "load", "like" */
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
            } else {
                /*
                *      Se foloseste itemNumber din command
                *   Iteram prin lista de playlisturi a fiecarui user si
                *   cautam pe cel desemnat de itemNumber
                */
                int id = command.getItemNumber();
                for (UserInfo user: users) {
                    for (Playlist playlist : ((NormalUser)user).getPlaylists()) {
                        String namePlaylist = playlist.getName();
                        String potentialPlaylistName = getSearchResult().get(id - 1);
                        if (namePlaylist.equals(potentialPlaylistName)) {
                            setPlaylist(playlist);
                            String succes = "Successfully selected ";
                            result.setMessage(succes + playlist.getName() + ".");
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }
}
