package playlist.commands;

import input.commands.CommandIn;
import main.users.UserInfo;
import main.users.NormalUser;
import output.result.ResultOut;
import playlist.commands.collections.Playlist;
import search.bar.Select;

import java.util.ArrayList;
/**
 *      Aceasta clasa este folosita pentru comanda "getTop5Playlists"
 *      Implementarea consta intr-o lista de clase de acest gen care vor fi ..
 *      .. sortate dupa campul "followers" la invocarea comenzii amintite
 * */
public class FollowStats implements Comparable {
    private String playlistName;
    private int followers;
    private int timeCreation;     // <--- camp folosit pentru sortarea topului de playlist-uri

    /** Clasa necesara pentru sortarea listei de playlist-rui populare */
    public int compareTo(final Object otherFollow) {
        int compareNoFollows = ((FollowStats) otherFollow).getFollowers();
        if (followers == compareNoFollows) {
            /* Se vor sorta dupa momentul in care au fost create */
            return timeCreation - ((FollowStats) otherFollow).getTimeCreation();
        }
        return compareNoFollows - followers;
        // ^-- Ordinea de baza; se sorteaza dupa numarul de urmaritori
    }

    public FollowStats(final String playlistName) {
        setPlaylistName(playlistName);
    }

    /** Getter */
    public int getTimeCreation() {
        return timeCreation;
    }

    /** Setter */
    public void setTimeCreation(final int time) {
        timeCreation = time;
    }

    /** Setter */
    public void setPlaylistName(final String playlist) {
        playlistName = playlist;
    }

    /** Getter */
    public String getPlaylistName() {
        return playlistName;
    }

    /** Aceasta metoda incrementeaza numarul de urmaritori al unui playlist */
    public void incFollowers() {
        followers += 1;
    }

    /** Aceasta metoda decrementeaza numarul de urmaritori */
    public void decFollowers() {
        if (followers > 0) {
            followers -= 1;     // <-- Nu are sens numar de urmaritori negativ
        }
    }

    /** Getter */
    public int getFollowers() {
        return followers;
    }

    /**
     *      Aceasta metoda implementeaza comanda "followPlaylist".
     * */
    public ResultOut followPlaylist(final CommandIn command, final ArrayList<UserInfo> users,
                                                        final Select selectInfo,
                                                    final ArrayList<FollowStats> topFwdPlaylits) {
        final int playlistId = 3;
        ResultOut result = new ResultOut(command);
        /*
            "selectInfo" contine informatiile despre ce a selectat ..
             .. user-ul care a dat comanda "command"
        */
        if (selectInfo == null || !selectInfo.getSelected()) {
            result.setMessage("Please select a source before following or unfollowing.");
        } else {
            /* Se verifica daca ce s-a incarcat este altceva decat playlist */
            if (selectInfo.getResultType() != playlistId) {
                result.setMessage("The selected source is not a playlist.");
            } else {
                /*
                    Se verifica daca ce s-a selectat este un playlist ..
                    .. al user-ului care a dat comanda
                */
                NormalUser currUser = findUser(users, command.getUsername());
                boolean isUsers = false;
                if (currUser == null) {
                    return null;
                }

                // v--- Playlist selectat de user
                String currPlaylist = selectInfo.getPlaylist().getName();
                for (Playlist playlist: currUser.getPlaylists()) {
                    String tempPlaylist = playlist.getName();
                    if (tempPlaylist.equals(currPlaylist)) {
                        /* User-ul vrea sa urmareasca un playlist al sau, operatiune invalida */
                        result.setMessage("You cannot follow or unfollow your own playlist.");
                        isUsers = true;
                        break;
                    }
                }

                /* Daca playlist-ul nu este al user-ului care a dat comanda*/
                if (!isUsers) {
                    /* Cautam statisticile playlist-ului curent */
                    FollowStats follows = null;
                    for (FollowStats iter: topFwdPlaylits) {
                        if (iter.getPlaylistName().equals(currPlaylist)) {
                            follows = iter;
                            break;
                        }
                    }
                    if (follows == null) {
                        /* Se poate ca playlist-ul sa nu fi avut urmaritori inainte */
                        follows = new FollowStats(currPlaylist);
                        follows.setTimeCreation(selectInfo.getPlaylist().getTimeOfCreation());
                        topFwdPlaylits.add(follows);
                    }

                    boolean isFollowed = false;
                    /*
                        In lista de playlist-uri urmarite ale user-ului care a dat comanda ..
                        .. cautam pe playlist selectat
                    */
                    Playlist selectedPlaylist = selectInfo.getPlaylist();
                    for (String playlits: currUser.getFwdPlaylits()) {
                        if (playlits.equals(currPlaylist)) {
                            /* Playlist-ul era urmarit, acum nu se mai urmareste */
                            isFollowed = true;
                            currUser.getFwdPlaylits().remove(playlits);
                            selectedPlaylist.decNrFollowers();
                            follows.decFollowers();
                            result.setMessage("Playlist unfollowed successfully.");
                            break;
                        }
                    }

                    if (!isFollowed) {
                        /* Playlist-ul nu a fost urmarit inainte */
                        currUser.getFwdPlaylits().add(currPlaylist);
                        selectedPlaylist.incNrFollowers();
                        follows.incFollowers();
                        result.setMessage("Playlist followed successfully.");
                    }
                }
            }
        }

        return result;
    }

    /** Metoda gaseste user-ul "username" in lista "users'"*/
    public NormalUser findUser(final ArrayList<UserInfo> users, final String username) {
        for (UserInfo user: users) {
            if (user.getUsername().equals(username)) {
                return (NormalUser)user;
            }
        }
        return null;
    }
}
