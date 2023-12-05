package main;

import fileio.input.UserInput;
import main.users.NormalUser;
import main.users.UserFactory;
import main.users.UserInfo;
import output.result.*;
import playlist.commands.collections.SongsColFactory;
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
public final class AnalyzeCommands {
    private static AnalyzeCommands instance = null;

    private AnalyzeCommands() {
    }

    /** Clasa este singletton; returnam o instanta a acesteia */
    public static AnalyzeCommands getInstance() {
        if (instance == null) {
            instance = new AnalyzeCommands();
        }
        return instance;
    }

    /** Metoda care se ocupa cu prelucrarea comenzilor primite */
    public ArrayList<Output> anaylzeFunc(final LibraryInput library,
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
            }

            NormalUser currentUser;
            //Artist
            //Host

            /* Cautam in lista "users" user curent  */
            UserInfo user = analyzeUser(users, command, result);
            if (user == null) {
                continue;
            }
            currentUser = (NormalUser) user;

            /*
                Actualizez pentru orice player (care ruleaza) timpul relativ la ..
                .. penultima comanda data pentru calcularea remainTime al piesei incarcate
            */
            for (UserInfo updateTimer: users) {
                if (updateTimer.isNormalUser()) {
                    if (((NormalUser) updateTimer).getPlayer() != null) {
                        ((NormalUser) updateTimer).updateRemainedTime(command);
                    }
                }
            }

            /* ------------------------------  Verificare comenzi ------------------------------ */
            if (command.getCommand().contains("search")) {
                /* Player-ul pentru user care da "search" trebuie sa dispara !! */
                Player player = currentUser.getPlayer();
                if (player != null && player.getLoadInfo() != null) {
                    if (player.getLoadInfo().getSelectInfo().getResultType() == 2) {
                        /* Daca se rula un podcast, se salveaza statisticile curente */
                        currentUser.updateRemainedTime(command);    // <-- se actualizeaza timpul
                        podcastPlayers.add(currentUser.getPlayer());
                    }
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
            } else if (command.getCommand().contains("select")) {
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
                    result.add(selected.selectFunc(command, library, users));
                } else {
                    ResultOut out = new ResultOut(command);
                    out.setMessage("Please conduct a search before making a selection.");
                    result.add(out);
                }
            } else if (command.getCommand().contains("load")) {
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
            } else if (command.getCommand().contains("status")) {
                result.add(Stats.statusFunc(command, currentUser));
            } else if (command.getCommand().contains("playPause")) {
                PlayPause func = PlayPause.getInstance();
                Player player = currentUser.getPlayer();
                result.add((func.playPauseFunc(player, command, currentUser)));
            } else if (command.getCommand().contains("createPlaylist")) {
                result.add(Playlist.createPlaylist(currentUser, command, topFwdPlaylits));
            } else if (command.getCommand().contains("addRemoveInPlaylist")) {
                result.add(AddRemove.addRemoveInPlaylist(currentUser, command, library.getSongs()));
            } else if (command.getCommand().equals("like")) {
                result.add(Like.likeCommand(topLikedSongs, currentUser, command, library));
            } else if (command.getCommand().equals("showPreferredSongs")) {
                result.add(new ResultPreferedSongs(command, currentUser.getLikedSongs()));
            } else if (command.getCommand().equals("showPlaylists")) {
                result.add(new ResultShowPlaylists(command, currentUser));
            } else if (command.getCommand().equals("repeat")) {
                Repeat repeatChanger = new Repeat();
                result.add(repeatChanger.changeRepeatMode(command, currentUser));
            } else if (command.getCommand().equals("shuffle")) {
                Shuffle shuffles = Shuffle.getInstance();
                result.add(shuffles.shuffleFunc(command, currentUser));
            } else if (command.getCommand().equals("forward")) {
                Forward forward = new Forward(currentUser.getPlayer());
                result.add(forward.forwardFunc(command));
            } else if (command.getCommand().equals("backward")) {
                Backward backward = new Backward(currentUser.getPlayer());
                result.add(backward.backwardFunc(command));
            } else if (command.getCommand().equals("next")) {
                Next next = new Next(currentUser);
                result.add(next.nextFunc(command, library));
            } else if (command.getCommand().equals("prev")) {
                Prev prev = new Prev(currentUser);
                result.add(prev.prevFunc(command, library));
            } else if (command.getCommand().equals("switchVisibility")) {
                result.add(Playlist.switchVisibility(command, currentUser));
            } else if (command.getCommand().contains("follow")) {
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
            } else if (command.getCommand().contains("switch")) {
                result.add(currentUser.changeConnectionStatus(command));
            }

        }
        return result;
    }

    /**
     *      Aceasta metoda:
     *          --> va gasi in lista "users" pe user care a invocat comanda
     *          --> va verifica starea acestuia (online / offline sau apartenent in lista "users")
     *
     *      Metoda va intoarce, in cazul in care user este online si exista, referinta catre ..
     *      .. obiectul in cauza, altfel va returna null ( daca este offline sau ..
     *      .. nu exista si comanda nu este "addUser" )
     * */
    private UserInfo analyzeUser(ArrayList<UserInfo> users, CommandIn command, ArrayList<Output> result) {
        UserInfo tempRefference = null;
        /* Cautam user-ul care a dat comanda "command" */
        for (UserInfo iter: users) {
            if (iter.getUsername().equals(command.getUsername())) {
                tempRefference = iter;
                break;
            }
        }

        /* Verificam daca user-ul exista */
        if (tempRefference == null) {
            if (command.getCommand().equals("addUser")) {
                /* Se va adauga user-ul */

            } else {
                /* User-ul nu exista si nu se doreste adaugat pe platforma */
                result.add(OutputClassFactory.getOutput(command, OutputClassFactory.UserState.NOEXIST));
                return null;
            }
        }  else {
            /* User-ul exista, verificam daca user-ul este offline*/
            if (tempRefference.isNormalUser()) {
                if (!((NormalUser) tempRefference).getState()) { /*online = true; offline = false*/
                    /* User-ul este offline */
                    if (command.getCommand().contains("switchConnection")) {
                        /* Se schimba starea user-ului  */
                        result.add(tempRefference.changeConnectionStatus(command));
                    } else if (command.getCommand().contains("status")) {
                        // Se afiseaza starea player-ului, intrucat nu se sterge daca user-ul este offline
                        result.add(Stats.statusFunc(command, (NormalUser) tempRefference));
                    } else {
                        result.add(OutputClassFactory.getOutput(command, OutputClassFactory.UserState.OFFLINE));
                    }
                    return null;
                }
            }
        }

        /* User-ul este fie online, fie artist sau host */
        return tempRefference;
    }

}
