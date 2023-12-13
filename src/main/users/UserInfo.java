package main.users;

import fileio.input.SongInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;
import player.commands.Like;
import playlist.commands.FollowStats;
import playlist.commands.collections.Album;
import playlist.commands.collections.Playlist;

import java.util.ArrayList;

/** Aceasta clasa contine campul comun tuturor user-ilor programului */
public class UserInfo {
    private UserInput userInfo;         // <-- detalii despre user
    protected UserType userType;    // <-- tipul user-ului

    protected enum UserType {
        NORMALUSER,
        ARTIST,
        HOST
    }

    /**
     *      Aceasta metoda implementeaza comanda "switchConnectionStatus"
     *      <p>
     *      Aceasta este apelata de user-ul care a invocat comanda.
     */
    public ResultOut changeConnectionStatus(CommandIn command) {
        ResultOut result = new ResultOut(command);

        /*
            Cazul cu "user does not exist" este acoperit de
            metoda "analyseUser" din clasa "AnalyseCommands"
        */
        if (!isNormalUser()) {
            result.setMessage(getUsername() + " is not a normal user.");
        } else {
            /* User-ul este unul normal, efectuam schimbarea de stare */
            ((NormalUser) this).changeState();
            result.setMessage(getUsername() + " has changed status successfully.");
        }

        return result;
    }

    /**
            *      Aceasta metoda implementeaza comanda "addUser"
            *      <p>
            *      Returneaza un obiect de tipul rezultatului asteptat pentru comanda "addUser"
            *      @param currUser este variabila "tempReference" din metoda "analyseUser"
     * */
    public static ResultOut addUser(final ArrayList<UserInfo> users, final UserInfo currUser,
                                    final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        /* Verificam daca referinta este nula (caz in care user-ul nu exista) */
        if (currUser == null) {
            UserInput currUserInfo = new UserInput(cmd.getUsername(), cmd.getAge(), cmd.getCity());
            users.add(UserFactory.getUser(cmd.getType(), currUserInfo));
            result.setMessage("The username " + cmd.getUsername() +
                    " has been added successfully.");
        } else {
            /* Daca referinta nu este nula, cu siguranta "currUser" se gaseste in lista "users" */
            result.setMessage("The username " + cmd.getUsername() + " is already taken.");
        }

        return result;
    }

    /**
     *   Metoda implementeaza comanda "deleteUser"
     *   Primeste ca parametrii lista de useri, user-ul care trebuie sters si comanda
     *   Va returna un obiect pe tiparul output-ului comenzii
     * */
    public static ResultOut deleteUser(final ArrayList<UserInfo> users, final UserInfo currUser,
                                       final CommandIn cmd, final ArrayList<Like> topLikedSongs,
                                        final ArrayList<FollowStats> topFwdPlaylits) {
        ResultOut result = new ResultOut(cmd);

        /* Verificam daca exista cel mult un user care asculta o colectie audio a user-ului */
        if (currUser.checkIfListenedTo()) {
            result.setMessage(currUser.getUsername() + " can't be deleted.");
        } else {
            /* User-ul poate fi eliminat cu succes */
            users.removeIf(user -> user.getUsername().equals(currUser.getUsername()));

            /* Trebuie eliminata orice tine de user-ul eliminat */
            if (currUser.isArtist()) {
                /* TODO pentru ALBUME */
                for (Album album : ((Artist) currUser).getAlbums()) {
                    for (SongInput song : album.getSongs()) {
                        topLikedSongs.removeIf(like -> like.getSongName().equals(song.getName()));
                    }
                }
            } else {
                /* User normal */
                for (Playlist playlist : ((NormalUser) currUser).getPlaylists()) {
                    topFwdPlaylits.removeIf(tempRef ->
                            tempRef.getPlaylistName().equals(playlist.getName()));
                    for (SongInput song : playlist.getSongs()) {
                        topLikedSongs.removeIf(like -> like.getSongName().equals(song.getName()));
                    }
                }
            }

            result.setMessage(currUser.getUsername() + " was successfully deleted.");
        }

        return result;
    }

    /**
     *  <p>
     *   Metoda va fi folosita in cadrul implementarii comenzii "deleteUser"
     *   <p>
     *   Aceasta verifica daca macar un album / playlist / podcast este ascultat de cineva si
     *   returneaza true, altfel returneaza false
     * */
    public boolean checkIfListenedTo() {
        if (isArtist()) {
            for (Album album : ((Artist) this).getAlbums()) {
                if (album.getNrListeners() > 0) {
                    return true;
                }
            }
        } else if (isNormalUser()) {
            for (Playlist playlist : ((NormalUser) this).getPlaylists()) {
                if (playlist.getNrListeners() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Getter */
    public String getUsername() {
        return userInfo.getUsername();
    }

    /** Setter */
    public void setUserInfo(final UserInput stats) {
        userInfo = stats;
    }

    /** Aceasta metoda verifica daca referinta refera catre un user normal */
    public boolean isNormalUser() {
        return userType == UserType.NORMALUSER;
    }

    /** Aceasta metoda verifica daca referinta refera catre un artist */
    public boolean isArtist() {
        return userType == UserType.ARTIST;
    }

    /** Aceasta metoda verifica daca referinta refera catre un host */
    public boolean isHost() {
        return userType == UserType.HOST;
    }

}
