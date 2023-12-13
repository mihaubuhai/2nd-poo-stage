package main;

import fileio.input.UserInput;
import main.users.*;
import main.users.pages.Page;
import output.result.*;
import top5.top5Playlists;
import top5.top5Songs;
import fileio.input.LibraryInput;
import input.commands.CommandIn;
import player.Player;
import player.commands.rewounding.Prev;
import player.commands.rewounding.Next;
import player.commands.rewounding.Forward;
import player.commands.rewounding.Backward;
import player.commands.Shuffle;
import player.commands.Repeat;
import player.Stats;
import player.Load;
import player.commands.PlayPause;
import player.commands.Like;
import playlist.commands.AddRemove;
import playlist.commands.FollowStats;
import playlist.commands.collections.Playlist;
import search.bar.Search;
import search.bar.Select;
import output.OutputClassFactory;

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

        /* Lista cu podcast-uri care au fost incarcate la un moment dat si acum nu mai ruleaza */
        ArrayList<Player> podcastPlayers = new ArrayList<>();

        /* Itereaza prin lista de comenzi si verifica ce tip este aceasta */
        for (CommandIn command: commands) {
            if (command.getCommand().equals("getTop5Playlists")) {
                top5Playlists top = top5Playlists.getInstance();
                result.add(top.getTop5Playlists(command, topFwdPlaylits));
                continue;
            } else if (command.getCommand().equals("getTop5Songs")) {
                top5Songs top = top5Songs.getInstance();
                result.add(top.getTop5Songs(command, topLikedSongs, library));
                continue;
            } else if (command.getCommand().contains("getOnline")) {
                ResultGetTop5 output = new ResultGetTop5(command);
                /* Iteram prin lista "users" */
                users.forEach(utilizator -> {
                    /* Verificam daca sunt user-i normali, apoi daca sunt online */
                    if (utilizator.isNormalUser()) {
                        if (((NormalUser) utilizator).getState()) {
                            output.getResult().add(utilizator.getUsername());
                        }
                    }
                });
                result.add(output);
                continue;
            } else if (command.getCommand().contains("AllUsers")) {
                ResultGetTop5 output = new ResultGetTop5(command);
                users.forEach(user -> {
                    if (user.isNormalUser()) {
                        output.getResult().add(user.getUsername());
                    }
                });
                users.forEach(user -> {
                    if (user.isArtist()) {
                        output.getResult().add(user.getUsername());
                    }
                });
                users.forEach(user -> {
                    if (user.isHost()) {
                        output.getResult().add(user.getUsername());
                    }
                });
                result.add(output);
                continue;
            }

            NormalUser currentUser = null;
            Artist currentArtist = null;
            Host currentHost = null;

            /*
                Actualizez pentru orice player (care ruleaza) timpul relativ la ..
                .. penultima comanda data pentru calcularea remainTime al piesei incarcate
            */
            for (UserInfo updateTimer: users) {
                if (updateTimer.isNormalUser()) {
                    NormalUser iter = (NormalUser) updateTimer;
                    if (iter.getPlayer() != null && iter.getState()) {
                        iter.updateRemainedTime(command);
                    }
                }
            }

            /* Cautam in lista "users" pe user-ul care a invocat o comanda */
            UserInfo user;
            if ((user = analyseUser(users, command, result)) == null) {
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
            if (command.getCommand().contains("search") && currentUser != null) {
                /* Player-ul pentru user care da "search" trebuie sa dispara !! */
                Player player = currentUser.getPlayer();
                if (player != null && player.getLoadInfo() != null) {
                    if (player.getLoadInfo().getSelectInfo().getResultType() == 2) {
                        /* Daca se rula un podcast, se salveaza statisticile curente */
                        currentUser.updateRemainedTime(command);    // <-- se actualizeaza timpul
                        podcastPlayers.add(currentUser.getPlayer());
                    }

                    /* Decrementez numarul de ascultatori al colectiei audio ascultata de user */
                    currentUser.getPlayer().getLoadInfo().getSelectInfo().decrementNrListeners();
                }
                currentUser.setPlayer(null);        // <--- se goleste efectiv player-ul

                Search search = Search.getInstance();
                ResultOutSearch temporary = search.searchFunc(library, command, users);
                /* v-- Valid deoarece clasa "ResultOutSearch" este o subclasa a "ResultCommand" */
                result.add(temporary);

                /* Ne asiguram ca nu mai exista rezultatul search-ului anterior */
                for (Select iter : selectedList) {
                    if (iter.getUser().contains(command.getUsername())) {
                        selectedList.remove(iter);
                        break;
                    }
                }

                /* Adaug si faptul ca s-a efectuat search */
                selectedList.add(new Select(command, temporary.getResults()));
            } else if (command.getCommand().contains("select") && currentUser != null) {
                /* Caut nodul din lista "selected_list" corespunzator user-ului ce a dat comanda */
                Select selected = null;
                for (Select iter : selectedList) {
                    if (iter.getUser().contains(command.getUsername())) {
                        selected = iter;
                        break;
                    }
                }

                if (selected != null) {
                    /* Chem functia "select" pt user curent si efectuam selectarea */
                    result.add(selected.selectFunc(command, library, users, currentUser));
                } else {
                    ResultOut out = new ResultOut(command);
                    out.setMessage("Please conduct a search before making a selection.");
                    result.add(out);
                }
            } else if (command.getCommand().contains("load") && currentUser != null) {
                ResultOut resultLoad = new ResultOut(command);
                boolean succes = false;    /* <-- Arata daca s-a dat "select" inainte de "load" */
                for (Select selected: selectedList) {
                    if (selected.getUser().equals(currentUser.getUsername())) {
                        if (selected.getSelected()) {
                            Player player = new Player(new Load(selected), command.getTimestamp());
                            currentUser.setPlayer(player);
                            selectedList.remove(selected);
                        }
                        break;
                    }
                }

                /* Daca player ruleaza, nu se efectueaza comanda  "load" */
                Player player = currentUser.getPlayer();
                if (player != null && player.getLoadInfo() != null) {
                    Load loadInfo = player.getLoadInfo();
                    if (!loadInfo.getLoaded()) {
                        /* Chem functie load */
                        loadInfo.loadFunc(resultLoad, currentUser, podcastPlayers);
                        currentUser.getPlayer().setLastLoadTime(command.getTimestamp());
                        succes = true;
                    }
                }

                if (!succes) {
                    resultLoad.setMessage("Please select a source before attempting to load.");
                }

                result.add(resultLoad);
            } else if (command.getCommand().contains("status") && currentUser != null) {
                result.add(Stats.statusFunc(command, currentUser));
            } else if (command.getCommand().contains("playPause") && currentUser != null) {
                PlayPause func = PlayPause.getInstance();
                Player player = currentUser.getPlayer();
                result.add((func.playPauseFunc(player, command, currentUser)));
            } else if (command.getCommand().contains("createPlaylist") && currentUser != null) {
                result.add(Playlist.createPlaylist(currentUser, command, topFwdPlaylits));
            } else if (command.getCommand().contains("addRemoveInPlaylist") && currentUser != null) {
                result.add(AddRemove.addRemoveInPlaylist(currentUser, command, library.getSongs()));
            } else if (command.getCommand().equals("like") && currentUser != null) {
                result.add(Like.likeCommand(topLikedSongs, currentUser, command, library));
            } else if (command.getCommand().equals("showPreferredSongs") && currentUser != null) {
                result.add(new ResultPreferedSongs(command, currentUser.getLikedSongs()));
            } else if (command.getCommand().equals("showPlaylists") && currentUser != null) {
                result.add(new ResultShowPlaylists(command, currentUser));
            } else if (command.getCommand().equals("repeat") && currentUser != null) {
                Repeat repeatChanger = new Repeat();
                result.add(repeatChanger.changeRepeatMode(command, currentUser));
            } else if (command.getCommand().equals("shuffle") && currentUser != null) {
                Shuffle shuffles = Shuffle.getInstance();
                result.add(shuffles.shuffleFunc(command, currentUser));
            } else if (command.getCommand().equals("forward") && currentUser != null) {
                Forward forward = new Forward(currentUser.getPlayer());
                result.add(forward.forwardFunc(command));
            } else if (command.getCommand().equals("backward") && currentUser != null) {
                Backward backward = new Backward(currentUser.getPlayer());
                result.add(backward.backwardFunc(command));
            } else if (command.getCommand().equals("next") && currentUser != null) {
                Next next = new Next(currentUser);
                result.add(next.nextFunc(command, library));
            } else if (command.getCommand().equals("prev") && currentUser != null) {
                Prev prev = new Prev(currentUser);
                result.add(prev.prevFunc(command, library));
            } else if (command.getCommand().equals("switchVisibility") && currentUser != null) {
                result.add(Playlist.switchVisibility(command, currentUser));
            } else if (command.getCommand().contains("follow") && currentUser != null) {
                /* Se cauta "selected" in "selected_list" aferent user-ului curent */
                Select selectInfo = null;
                String userName = command.getUsername();
                for (Select selection: selectedList) {
                    if (selection.getUser().equals(userName)) {
                        selectInfo = selection;
                        break;
                    }
                }

                FollowStats temp = new FollowStats(null);       // <-- doar pentru a apela metoda
                result.add(temp.followPlaylist(command, users, selectInfo, topFwdPlaylits));
            } else if (command.getCommand().contains("switch") && currentUser != null) {
                result.add(currentUser.changeConnectionStatus(command));
            } else if (command.getCommand().equals("addAlbum")) {
                if (!user.isArtist()) {
                    /* Verificam daca user-ul care a apelat aceasta metoda este artist */
                    ResultOut out = new ResultOut(command);
                    out.setMessage(user.getUsername() + " is not an artist.");
                    result.add(out);
                } else {
                    result.add(currentArtist.addAlbum(command));
                }
            } else if (command.getCommand().equals("showAlbums")) {
                result.add(new ResultShowAlbums(command, currentArtist));
            } else if (command.getCommand().equals("printCurrentPage") && currentUser != null) {
                result.add(currentUser.getCurrentPage().getPage(currentUser, command, topLikedSongs));
            } else if (command.getCommand().contains("Event")) {
                Artist tempArtist = new Artist(null);   // <-- pentru a apela metoda
                result.add(tempArtist.addEvent(user, command));
            } else if (command.getCommand().contains("Merch")) {
                Artist tempArtist = new Artist(null);   // <-- pentru a apela metoda
                result.add(tempArtist.addMerch(user, command));
            } else if (command.getCommand().contains("delete")) {
                result.add(UserInfo.deleteUser(users, user, command, topLikedSongs, topFwdPlaylits));
            } else if (command.getCommand().equals("addPodcast")) {
                Host tempRef = new Host(null);
                result.add(tempRef.addPodcast(command, user));
            } else if (command.getCommand().equals("addAnnouncement")) {
                Host tempRef = new Host(null);
                result.add(tempRef.addAnnouncement(command, user));
            } else if (command.getCommand().equals("showPodcasts")) {
                result.add(new ResultShowPodcasts(command, currentHost));
            } else if (command.getCommand().equals("removeAnnouncement")) {
                if (!user.isHost()) {
                    ResultOut out = new ResultOut(command);
                    out.setMessage(user.getUsername() + " is not a host.");
                    result.add(out);
                } else {
                    result.add(currentHost.removeAnnouncement(command));
                }
            } else if (command.getCommand().equals("removeAlbum")) {
                result.add(((Artist) user).removeAlbum(command, user));
            } else if (command.getCommand().equals("changePage") && currentUser != null) {
                result.add(currentUser.getCurrentPage().changePage(command, currentUser));
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
