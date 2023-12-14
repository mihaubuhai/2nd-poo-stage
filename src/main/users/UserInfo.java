package main.users;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;
import player.commands.Like;
import playlist.commands.FollowStats;
import playlist.commands.collections.Album;
import playlist.commands.collections.Playlist;
import search.bar.Select;

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
     *   <p>
     *   Primeste ca parametrii:
     *   <p>
     *       --> lista de useri totali ai programului
     *       <p>
     *           --> user-ul care trebuie sters
     *           <p>
     *               --> comanda care a fost data
     *               <p>
     *                   --> lista de melodii apreciate pe tot programul
     *                   <p>
     *                       --> lista de playlist-uri urmarite pe tot programul
     *   <p>
     *       Va returna un obiect de tipul rezultatului asteptat la output-ul comenzii "cmd"
     * */
    public static ResultOut deleteUser(final ArrayList<UserInfo> users, final UserInfo currUser,
                                       final CommandIn cmd, final ArrayList<Like> topLikedSongs,
                                        final ArrayList<FollowStats> topFwdPlaylits,
                                        final ArrayList<Select> searchResults) {
        ResultOut result = new ResultOut(cmd);

        /* Verificam daca exista cel mult un user care asculta o colectie audio a user-ului */
        if (currUser.checkIfListenedTo() || currUser.checkIfSmthIsSel(users)) {
            result.setMessage(currUser.getUsername() + " can't be deleted.");
        } else if (!currUser.isNormalUser() && currUser.checkIfSelected(users)) {
            /* Verificam daca un user normal se afla pe pagina user-ului care se vrea sters */
            result.setMessage(currUser.getUsername() + " can't be deleted.");
        } else if (!currUser.isNormalUser() && currUser.checkIfSearched(searchResults)) {
            result.setMessage(currUser.getUsername() + " can't be deleted.");
        } else {
            /* User-ul poate fi eliminat cu succes */
            users.removeIf(user -> user.equals(currUser));

            /* Trebuie eliminata orice tine de user-ul eliminat */
            if (currUser.isArtist()) {
                /* TODO pentru ALBUME */
                for (Album album : ((Artist) currUser).getAlbums()) {
                    for (SongInput song : album.getSongs()) {
                        topLikedSongs.removeIf(like -> like.getSongName().equals(song.getName()));
                    }
                }
            } else if (currUser.isNormalUser()) {
                /* User normal */
                for (UserInfo user : users) {
                    if (user.isNormalUser()) {
                        NormalUser someUser = (NormalUser) user;
                        NormalUser userToDel  = (NormalUser) currUser;

                        /*
                            Iteram prin playlist-urile user-ului care se sterge si le elimin,
                            pentru fiecare user normal, daca le urmareste pe vreunele.
                            Apoi, din lista totala de playlist-uri urmarite, il eliminam.
                        */
                        for (Playlist playlist : userToDel.getPlaylists()) {
                            someUser.getFwdPlaylits().removeIf(tmpPlaylist -> tmpPlaylist.equals(playlist));
                            topFwdPlaylits.removeIf(tmpPlaylist -> tmpPlaylist.getPlaylistName().equals(playlist.getName()));
                            playlist.decNrFollowers();
                        }

                        userToDel.getFwdPlaylits().forEach(Playlist::decNrFollowers);

                        /*
                         *   Iteram prin lista de melodii apreciate pe tot programul si ..
                         * .. decrementez nr de like-uri al unei melodii pe care aces user ..
                         * .. o aprecia
                         * */
                        for (Like song : topLikedSongs) {
                            for (SongInput usersSong : userToDel.getLikedSongs()) {
                                if (song.getSongName().equals(usersSong.getName())) {
                                    song.decrementNoUsers();
                                }
                            }
                        }
                    }
                }
            } else {
                // TODO
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
        } else {
            for (PodcastInput podcast : ((Host) this).getPodcasts()) {
                if (podcast.retrieveNrListeners() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  Metoda verifica daca user-ul care a apelat-o are pagina selectata
     *  de un user
     * */
    public boolean checkIfSelected(final ArrayList<UserInfo> users) {
        if (isNormalUser()) {
            return false;
        } else {
            for (UserInfo user : users) {
                /* Doar un user normal poate selecta un artist / host */
                if (user.isNormalUser()) {
                    NormalUser tempRef = (NormalUser) user;

                    /* Clasa "Page" are un camp ce retine referinta catre artist / host-ul selectat */
                    UserInfo selectedUser = tempRef.getCurrentPage().getUsersPage();
                    /* ^-- Poate fi null daca user-ul este pe pagina Home / LikedContent */

                    if (selectedUser != null && selectedUser.equals(this)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     *  Metoda verifica daca un user normal a selectat o colectie audio sau un fisier audio
     *  al user-ului care a invocat comanda
     * */
    public boolean checkIfSmthIsSel(final ArrayList<UserInfo> users) {
        for (UserInfo user : users) {
            /* Doar un user normal poate selecta o colectie audio */
            if (user.isNormalUser() && !user.equals(this)) {
                /* Verificam daca are o colectie audio selectata (sau o melodie dintr-un album) */
                NormalUser tempRef = (NormalUser) user;
                if (tempRef.getSelectInfo() == null || tempRef.getSelectInfo().getArtistHostName() == null) {
                    /*
                        Verificarea interactiunii unui user normal cu alt user se face
                        prin compararea campului "artistHostName" cu obicetul care a apelat metoda
                    */
                    return false;
                } else if (tempRef.getSelectInfo().getArtistHostName().equals(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIfSearched(final ArrayList<Select> searchResults) {
        for (Select selection : searchResults) {
            for (String result : selection.getSearchResult()) {
                if (result.equals(this.getUsername())) {
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
