package main;

import fileio.input.UserInput;
import top5.getTopAlbums;
import output.result.ResultOut;
import output.result.Output;
import output.result.ResultGetTop5;
import output.result.ResultShowPlaylists;
import output.result.ResultShowPodcasts;
import output.result.ResultShowAlbums;
import output.result.ResultPreferedSongs;
import songcollections.collections.Album;
import top5.getTopPlaylists;
import top5.getTopSongs;
import fileio.input.LibraryInput;
import input.commands.CommandIn;
import player.Player;
import player.commands.rewind.Prev;
import player.commands.rewind.Next;
import player.commands.rewind.Forward;
import player.commands.rewind.Backward;
import player.commands.Shuffle;
import player.commands.Repeat;
import player.Stats;
import player.Load;
import player.commands.PlayPause;
import player.commands.Like;
import songcollections.commands.AddRemove;
import songcollections.commands.FollowStats;
import songcollections.collections.Playlist;
import search.bar.Search;
import search.bar.Select;
import output.OutputClassFactory;
import top5.topCreatorVisitor;
import top5.getTop;
import top5.getTopOfUsers;
import users.Host;
import users.UserInfo;
import users.NormalUser;
import users.Artist;
import users.UserFactory;

import java.util.ArrayList;
/**
 *         Aceasta clasa interpreteaza comenzile primite ca input si returneaza ..
 *         .. rezultatul tuturor comenzilor.
 * */
public final class AnalyseCommands {
    private static AnalyseCommands instance = null;

    private AnalyseCommands() {
    }

    /** Clasa este singletton; returnam o instanta a acesteia */
    public static AnalyseCommands getInstance() {
        if (instance == null) {
            instance = new AnalyseCommands();
        }
        return instance;
    }

    /** Metoda care se ocupa cu prelucrarea comenzilor primite */
    public ArrayList<Output> analyseFunc(final LibraryInput library,
                                         final ArrayList<CommandIn> commands) {
        /* Output-ul pentru fiecare comanda este stocat aici */
        ArrayList<Output> result = new ArrayList<>();

        /* Lista care tine cont de selectiile user-ilor la un moment dat */
        ArrayList<Select> selectedList = new ArrayList<>();

        /* Lista cu users curenti ai programului */
        ArrayList<UserInfo> users = new ArrayList<>();
        for (UserInput newUser: library.getUsers()) {
            users.add(UserFactory.getUser("user", newUser));
        }

        /* Lista cu toate melodiile apreciate (din biblioteca) la un moment dat */
        ArrayList<Like> topLikedSongs = new ArrayList<>();

        /* Lista cu toate playlist-urile urmarite la un moment dat */
        ArrayList<FollowStats> topFwdPlaylits = new ArrayList<>();

        /* Lista cu toate albumele din program */
        ArrayList<Album> topAlbums = new ArrayList<>();

        /* Lista cu podcast-uri care au fost incarcate la un moment dat si acum nu mai ruleaza */
        ArrayList<Player> podcastPlayers = new ArrayList<>();

        /* Itereaza prin lista de comenzi si verifica ce tip este aceasta */
        for (CommandIn cmd: commands) {
            if (cmd.getCommand().contains("get")) {
                topCreatorVisitor v  = new topCreatorVisitor();
                getTop top;
                ResultGetTop5 output;

                switch (cmd.getCommand()) {
                    case "getTop5Albums" ->  {
                        top = new getTopAlbums(cmd, topAlbums);
                        output = ((getTopAlbums) top).accept(v);
                    }
                    case "getAllUsers" -> {
                        top = new getTopOfUsers(cmd, users, getTopOfUsers.TopType.ALLUSERS);
                        output = ((getTopOfUsers) top).accept(v);
                    }
                    case "getOnlineUsers" -> {
                        top = new getTopOfUsers(cmd, users, getTopOfUsers.TopType.ONLINEUSERS);
                        output = ((getTopOfUsers) top).accept(v);
                    }
                    case "getTop5Songs" -> {
                        top = new getTopSongs(cmd, library, topLikedSongs);
                        output = ((getTopSongs) top).accept(v);
                    }
                    case "getTop5Artists" -> {
                        top = new getTopOfUsers(cmd, users, getTopOfUsers.TopType.TOPARTIST);
                        output = ((getTopOfUsers) top).accept(v);
                    }
                    default -> {
                        top = new getTopPlaylists(cmd, topFwdPlaylits);
                        output = ((getTopPlaylists) top).accept(v);
                    }
                }

                result.add(output);
                continue;
            }

            NormalUser currentUser = new NormalUser(null);
            Artist currentArtist = new Artist(null);
            Host currentHost = new Host(null);

            /*
                Actualizez pentru orice player (care ruleaza) timpul relativ la ..
                .. penultima comanda data pentru calcularea remainTime al piesei incarcate
            */
            for (UserInfo updateTimer: users) {
                if (updateTimer.isNormalUser()) {
                    NormalUser iter = (NormalUser) updateTimer;
                    if (iter.getPlayer() != null && iter.getState()) {
                        iter.updateRemainedTime(cmd);
                    }
                }
            }

            /* Cautam in lista "users" pe user-ul care a invocat o comanda */
            UserInfo user = analyseUser(users, cmd, result);
            if (user == null) {
                continue;
            }

            if (user.isNormalUser()) {
                currentUser = (NormalUser) user;
            } else if (user.isArtist()) {
                currentArtist = (Artist) user;
            } else {
                currentHost = (Host) user;
            }

            /* ------------------------------  Verificare comenzi ------------------------------ */
            switch (cmd.getCommand()) {
                case "status" -> result.add(Stats.statusFunc(cmd, currentUser));
                case "removeEvent" -> result.add(currentArtist.removeEvent(user, cmd));
                case "removePodcast" -> result.add(currentHost.removePodcast(cmd, user));
                case "removeAlbum" -> result.add(currentArtist.removeAlbum(cmd, user));
                case "removeAnnouncement" -> result.add(currentHost.removeAnnouncement(cmd, user));
                case "showPodcasts" -> result.add(new ResultShowPodcasts(cmd, currentHost));
                case "showAlbums" -> result.add(new ResultShowAlbums(cmd, currentArtist));
                case "addAlbum" -> result.add(currentArtist.addAlbum(cmd, topAlbums, user));
                case "switchVisibility" -> result.add(Playlist.switchVisibility(cmd, currentUser));
                case "showPlaylists" -> result.add(new ResultShowPlaylists(cmd, currentUser));
                case "createPlaylist" -> result.add(Playlist.
                        createPlaylist(currentUser, cmd, topFwdPlaylits));
                case "addRemoveInPlaylist" -> result.add(AddRemove.
                        addRemoveInPlaylist(currentUser, cmd, users));
                case "like" -> result.add(Like.likeCmd(topLikedSongs, currentUser, cmd, library));
                case "showPreferredSongs" -> result.add(new ResultPreferedSongs(cmd,
                        currentUser.getLikedSongs()));
                case "playPause" -> {
                    PlayPause func = PlayPause.getInstance();
                    result.add((func.playPauseFunc(currentUser.getPlayer(), cmd, currentUser)));
                }
                case "repeat" -> {
                    Repeat repeatChanger = new Repeat();
                    result.add(repeatChanger.changeRepeatMode(cmd, currentUser));
                }
                case "shuffle" -> {
                    Shuffle shuffles = Shuffle.getInstance();
                    result.add(shuffles.shuffleFunc(cmd, currentUser));
                }
                case "forward" -> {
                    Forward forward = new Forward(currentUser.getPlayer());
                    result.add(forward.forwardFunc(cmd));
                }
                case "backward" -> {
                    Backward backward = new Backward(currentUser.getPlayer());
                    result.add(backward.backwardFunc(cmd));
                }
                case "next" -> {
                    Next next = new Next(currentUser);
                    result.add(next.nextFunc(cmd, library));
                }
                case "prev" -> {
                    Prev prev = new Prev(currentUser);
                    result.add(prev.prevFunc(cmd, library));
                }
                case "follow" -> {
                    Select selectInfo = currentUser.getSelectInfo();
                    FollowStats temp = new FollowStats(null);     // <-- doar pentru a apela metoda
                    result.add(temp.followPlaylist(cmd, users, selectInfo, topFwdPlaylits));
                }
                case "printCurrentPage" -> result.add(currentUser.getCurrentPage()
                        .getPage(currentUser, cmd, topLikedSongs));
                case "addEvent" -> {
                    Artist tempArtist = new Artist(null);   // <-- pentru a apela metoda
                    result.add(tempArtist.addEvent(user, cmd));
                }
                case "addMerch" -> {
                    Artist tempArtist = new Artist(null);   // <-- pentru a apela metoda
                    result.add(tempArtist.addMerch(user, cmd));
                }
                case "addPodcast" -> {
                    Host tempRef = new Host(null);
                    result.add(tempRef.addPodcast(cmd, user));
                }
                case "addAnnouncement" -> {
                    Host tempRef = new Host(null);
                    result.add(tempRef.addAnnouncement(cmd, user));
                }
                case "switchConnectionStatus" -> result.add(currentUser
                        .changeConnectionStatus(cmd));
                case "changePage" -> result.add(
                        currentUser.getCurrentPage().changePage(cmd, currentUser));
                case "deleteUser" -> result.add(UserInfo.deleteUser(users, user, cmd, topLikedSongs,
                        topFwdPlaylits, topAlbums));
                case "load" -> {
                    ResultOut resultLoad = new ResultOut(cmd);

                    Player player = currentUser.getPlayer();
                    Select selected = currentUser.getSelectInfo();
                    /* Daca player ruleaza, nu se efectueaza comanda  "load" */
                    if (selected == null || player != null || !selected.getSelected()) {
                        resultLoad.setMessage("Please select a source before attempting to load.");
                    } else {
                        player = new Player(new Load(selected), cmd.getTimestamp());
                        currentUser.setPlayer(player);
                        Load loadInfo = player.getLoadInfo();
                        if (!loadInfo.getLoaded()) {
                            /* Chem functie load */
                            loadInfo.loadFunc(resultLoad, currentUser, podcastPlayers);
                            currentUser.getPlayer().setLastLoadTime(cmd.getTimestamp());
                        }
                    }

                    result.add(resultLoad);
                }
                case "select" -> {
                    /* Caut nodul din lista corespunzator user-ului ce a dat comanda */
                    Select selected = null;
                    for (Select iter : selectedList) {
                        if (iter.getUser().contains(cmd.getUsername())) {
                            selected = iter;
                            break;
                        }
                    }

                    if (selected != null) {
                        /* Chem functia "select" pt user curent si efectuam selectarea */
                        result.add(selected.selectFunc(cmd, library, users, currentUser));
                        selectedList.remove(selected);
                    } else {
                        ResultOut out = new ResultOut(cmd);
                        out.setMessage("Please conduct a search before making a selection.");
                        result.add(out);
                    }
                }
                case "search" -> {
                    /* Player-ul pentru user care da "search" trebuie sa dispara !! */
                    Player player = currentUser.getPlayer();
                    if (player != null && player.getLoadInfo() != null) {
                        if (player.getLoadInfo().getSelectInfo().getResultType() == 2) {
                            /* Daca se rula un podcast, se salveaza statisticile curente */
                            currentUser.updateRemainedTime(cmd);    // <-- se actualizeaza timpul
                            podcastPlayers.add(currentUser.getPlayer());
                        }

                    }
                    currentUser.setSelectInfo(null);
                    currentUser.setPlayer(null);        // <--- se goleste efectiv player-ul

                    Search search = Search.getInstance();
                    result.add(search.searchFunc(library, cmd, users));

                    /* Ne asiguram ca nu mai exista rezultatul search-ului anterior */
                    for (Select iter : selectedList) {
                        if (iter.getUser().contains(cmd.getUsername())) {
                            selectedList.remove(iter);
                            break;
                        }
                    }

                    /* Adaug si faptul ca s-a efectuat search */
                    selectedList.add(new Select(cmd, search));
                }
                default -> {
                }
            }

        }
        return result;
    }

    /**
     *      <p>
     *      Aceasta metoda:
     *      </p>
     *          <p>
     *          --> va gasi in lista "users" pe user care a invocat comanda
     *          </p>
     *          <p>
     *          --> va verifica starea acestuia (online / offline sau apartenent in lista "users")
     *          </p>
     *      <p>
     *      Metoda va intoarce:
     *      </p>
     *      <p>
     *      -> referinta catre obiectul in cauza, caz in care user-ul este online si exista
     *      </p>
     *      <p>
     *      -> null, daca user-ul este:
     *              </p>
     *              <p>
     *              ---> offline
     *              </p>
     *              <p>
     *              ---> nu exista si comanda nu este "addUser"
     *              </p>
     *              <p>
     *              ---> s-a efectuat comanda "addUser" (indiferent de rezultatul acesteia)
     *              </p>
     * */
    private UserInfo analyseUser(final ArrayList<UserInfo> users, final CommandIn cmd,
                                 final ArrayList<Output> result) {
        UserInfo tempReference = null;
        /* Cautam user-ul care a dat comanda "cmd" */
        for (UserInfo iter: users) {
            if (iter.getUsername().equals(cmd.getUsername())) {
                tempReference = iter;
                break;
            }
        }

        if (cmd.getCommand().equals("addUser")) {
            /* Se va adauga user-ul (daca nu exista) */
            result.add(UserInfo.addUser(users, tempReference, cmd));
            return null;
        }
        if (tempReference == null) {
            /* User-ul nu exista si comanda curenta nu il adauga pe platforma, deci eroare */
            result.add(OutputClassFactory.getOutput(cmd, OutputClassFactory.UserState.NOEXIST));
            return null;
        }
        if (cmd.getCommand().contains("switchConnection")) {
            /* Se schimba starea user-ului (daca este normal)  */
            result.add(tempReference.changeConnectionStatus(cmd));
            return null;
        }

        /* Verificam daca user-ul este unul normal */
        if (tempReference.isNormalUser()) {
            NormalUser normalTemp = (NormalUser) tempReference;

            if (!normalTemp.getState()) {
                if (cmd.getCommand().equals("status")) {
                    /* Se afiseaza starea player-ului, chiar daca user-ul este offline */
                    result.add(Stats.statusFunc(cmd, normalTemp));
                } else {
                    /* User-ul este offline si comanda este una nepermisa acestuia */
                    result.add(OutputClassFactory.getOutput(cmd,
                            OutputClassFactory.UserState.OFFLINE));
                }
                return null;
            }

            /* User normal online, actualizam timpul trecut al player-ului */
//            normalTemp.updateRemainedTime(cmd);
        }

        /* User-ul este fie online, fie artist sau host */
        return tempReference;
    }

}
