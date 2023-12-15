package users;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import player.Player;
import songcollections.collections.Playlist;
import songcollections.collections.SongsCollection;
import search.bar.Select;
import users.pages.HomePage;
import users.pages.Page;

import java.util.ArrayList;

/**
 *      Aceasta clasa este folosita pentru a executa comenzile corespunzatoare
 *      unui utilizator normal
 * */
public final class NormalUser extends UserInfo {
    private Player player;                                    /* Player-ul user-ului */
    private final ArrayList<Playlist> playlists;    /* Tine cont de playlist-urile user-ului */
    private final ArrayList<SongInput> likedSongs;  /* Melodiile apreciate de user */
    private final ArrayList<Playlist> fwdPlaylits; /* Playlist-urile urmarite de user */
    private boolean state;        /* <-- Starea unui utilizator: Online (true), Offline (false) */
    private Page currentPage;  /* <-- Pagina pe care se afla user-ul */
    private Select selectInfo;

    public NormalUser(final UserInput userInfo) {
        setUserInfo(userInfo);
        playlists = new ArrayList<>();          /* Instantiem lista de playlist-uri */
        likedSongs = new ArrayList<>();
        fwdPlaylits = new ArrayList<>();
        userType = UserType.NORMALUSER;   /* Retinem ca aceasta clasa reprezinta un user normal */
        state = true;   /* <-- Utilizatorul este "Online" atunci cand este adaugat pe platforma */
        currentPage = new HomePage(Page.PageType.HOME); // <-- Pagina initiala este "HOME"
    }

    /** Metoda elimina player-ul pentru user-ul care a apelat-o */
    public void removePlayer() {
        /* v--- Decrementez numarul de ascultatori al albumului / playlist / podcast */
        player.getLoadInfo().getSelectInfo().decrementNrListeners();
        getPlayer().setLoadInfo(null);
        getPlayer().removeStats();
        selectInfo = null;
    }

    /**
     *          Metoda de mai jos este responsabila de actualizarea timpului
     *      ramas pentru ce se ruleaza in player.
     **/
    public void updateRemainedTime(final CommandIn command) {
        if (!player.getStats().getPaused() && player.getLoadInfo() != null) {
            player.getStats().setRemainedTime(player.findRemainedTime(command.getTimestamp()));

            if (player.getStats().getRemainedTime() <= 0) {
                /* Verificam ce se ruleaza */
                int runnerType = player.getLoadInfo().getSelectInfo().getResultType();
                if (runnerType == 1) {
                    /* Se rula o melodie */
                    if (!repeatSongEpisode(0)) {
                        removePlayer();
                        return;
                    }
                } else if (runnerType == 2) {
                    /* Se ruleaza un podcast */
                    PodcastInput currentPodcast = player.getLoadInfo().getSelectInfo().getPodcast();
                    /* Cautam episodul curent*/
                    EpisodeInput currentEpisode = null;
                    for (EpisodeInput episode: currentPodcast.getEpisodes()) {
                        if (episode.getName().equals(player.getStats().getName())) {
                            currentEpisode = episode;
                            break;
                        }
                    }

                    int currentIdxEpisode = currentPodcast.getEpisodes().indexOf(currentEpisode);
                    /* Se verifica daca episodul se repeta */
                    if (!repeatSongEpisode(currentIdxEpisode)) {
                        /* Se incerca rularea urmatorului episod (daca exista) */
                        if (findFittingEpisode(currentIdxEpisode) < 0) {
                            removePlayer();
                            return;
                        }
                    }
                } else {
                    /* Se ruleaza un playlist / album */
                    if (!repeatSongCollection()) {
                        SongsCollection currSongsColl = selectInfo.getSongsCollection();
                        /*
                         *   "newIdx" contine indicele urmatoarei melodii relativ la cea care ..
                         * ruleaza si deci de acolo vom incepe calcularea timpului ramas
                         * */
                        int newIdx = currSongsColl.findCurrIdx(this);
                        int result = findFittingSong(newIdx, player.getStats().getShuffle());
                        if (result < 0) {
                            removePlayer();
                            return;
                        }
                    }
                }
            }
        }

        /* Se actualizeaza timestamp-ul pentru ultima comanda  */
        player.setLastLoadTime(command.getTimestamp());
    }

    /**
     *  Campul "remainedTime" din "stats" este cel mai probabil negativ (sau zero)
     *   El contine, cand negativ, cat timp a trecut de la finalizarea piesei care rula
     *   Aflarea timpului ramas se face adunand la "remainedTime" timpul total al piesei
     *   pana cand "remainedTime" devine pozitiv
     *  */
    private int evaluateLeftTime(final int remainedTime, final int initialDuration) {
        int temp = remainedTime;
        while (temp < 0) {
            temp += initialDuration;
        }
        return temp;
    }

    /**
     *          Metoda folosita in metoda "repeatSong"
     *      Intoarce durata a melodie / episod de podcast raportat ..
     *      ..la ce este incarcat in player
     * */
    private int getDuration(final int idx) {
        Select selection = player.getLoadInfo().getSelectInfo();
        if (selection.getResultType() == 1) {
            return selection.getSong().getDuration();
        } else {
            return selection.getPodcast().getEpisodes().get(idx).getDuration();
        }
    }

    /**
     *      Metoda de mai jos se ocupa de repetarea unei melodii / episod de podcast.
     *      Parametrul "idx" este folosit doar la episoade de podcast, el reprezentand ...
     *      ... indicele din lista de episoade corespunzator celui care ruleaza in player.
     *      Pentru melodii, aceasta metoda este apelata cu orice parametru.
     * */
    public boolean repeatSongEpisode(final int idx) {
        /* Metoda poate fi apelata si de playere care nu repeta ce au incarcat */
        String repeatMode = player.getStats().getRepeat().toLowerCase();
        if (repeatMode.contains("no")) {
            return false;
        }

        /* Se calculeaza timpul ramas pentru melodie / episod prin metoda "evaluateLeftTime" */
        int initialDuration = getDuration(idx);
        int remainedTime = getPlayer().getStats().getRemainedTime();
        getPlayer().getStats().setRemainedTime(evaluateLeftTime(remainedTime, initialDuration));

        /* Odata timpul actualizat, trebuie verificat tipul de repeat */
        if (repeatMode.contains("once")) {
            /* Se doreste doar o singura repetare */
            getPlayer().getStats().setRepeat("No Repeat");
        }

        /* Pentru "repeat infinite", nu se schimba nimic */
        return true;
    }

    /**
     *      Metoda de mai jos implementeaza starea "repeat" pentru o colectie de melodii.
     *      Adica, in caz de repeat, aceasta verifica ce tip este si efectueaza aferent..
     *      .. operatiile.
     * */
    public boolean repeatSongCollection() {
        /* Metoda poate fi apelata de "playlist" care nu are vreun "repeat mode" */
        String repeatMode = player.getStats().getRepeat().toLowerCase();
        if (repeatMode.contains("no")) {
            return false;
        }

        /* Accessam melodia curenta din playlist / album */
        SongsCollection currSongsColl = selectInfo.getSongsCollection();
        int currIdx = currSongsColl.findCurrIdx(this);

        /* Abordam cazul "repeat current song" */
        if (repeatMode.contains("current")) {
            /* Melodia care ruleaza este tot ceea care era inainte */
            SongInput currSong = currSongsColl.getSongs().get(currIdx);
            int initialDuration = currSong.getDuration();
            int remainedTime = player.getStats().getRemainedTime();
            player.getStats().setRemainedTime(evaluateLeftTime(remainedTime, initialDuration));
        } else {
            findFittingSong(currIdx, player.getStats().getShuffle());
        }

        return true;
    }

    /**
     *      Aceasta metoda este folosita in cadrul metodei "updateTime" pentru playlist
     *      Ea reduce "remainedTime" din "player.stats", care este negativ ..
     *      .. (datorita scurgerii timpului) pana cand campul devine pozitiv,
     *      moment in care stim si ce melodie din playlist este incarcata.
     * */
    private int findFittingSong(final int idx, final boolean shuffle) {
        SongsCollection currPlaylist = selectInfo.getSongsCollection();
        int remainedTime = getPlayer().getStats().getRemainedTime();
        int index = idx;

        while (remainedTime <= 0) {
            if (verifyIdx(index, currPlaylist)) {
                return -1;
            }

            index = findNextSong(index, currPlaylist);
            SongInput songToRun = currPlaylist.getSongs().get(index);
            remainedTime += songToRun.getDuration();
            player.getStats().setName(songToRun.getName());
        }

        player.getStats().setRemainedTime(remainedTime);
        return 1;
    }

    private int findFstSong(final SongsCollection currSongsColl) {
        /* Verificam starea "shuffle" */
        if (!player.getStats().getShuffle()) {
            return 0;
        } else {
            return currSongsColl.getShuffledIndices().get(0);
        }
    }

    /** Aceasta metoda gaseste indicele melodiei urmatoare in playlist al lui "idx" */
    private int findNextSong(final int idx, final SongsCollection currentPlaylist) {
        if (!player.getStats().getShuffle()) {
            return idx + 1;
        } else {
            ArrayList<Integer> shuffles = currentPlaylist.getShuffledIndices();
            /* "idx" reprezinta indicele din playlist, deci trebuie cautat in "shuffleIndices" */
            int corespondingIdx = 0;
            int playlistSize = currentPlaylist.getSongs().size();
            for (int i = 0; i < playlistSize; ++i) {
                if (shuffles.get(i) == idx) {
                    corespondingIdx = i;
                    break;
                }
            }
            if (corespondingIdx == playlistSize - 1) {
                /* Am ajuns la final de vector de indici */
                if (player.getStats().getRepeat().toLowerCase().contains("no")) {
                    return -1;
                } else if (player.getStats().getRepeat().toLowerCase().contains("all")) {
                    return shuffles.get(0);
                } else {
                    return shuffles.get(corespondingIdx);
                }
            }
            return shuffles.get(corespondingIdx + 1);
        }
    }

    /**
     *      Aceasta metoda gaseste indicele dinaintea lui "idx"
     * */
    public int findPrevSong(final int idx, final SongsCollection currSongsColl) {
        String repeatMode = player.getStats().getRepeat().toLowerCase();
        if (!player.getStats().getShuffle()) {
            if (idx == 0 && repeatMode.contains("all")) {
                return currSongsColl.getSongs().size() - 1;
            }
            return idx - 1;
        } else {
            ArrayList<Integer> shuffles = currSongsColl.getShuffledIndices();
            /* "idx" contine indicele lui playlist, deci trebuie cautat in vectorul shuffles */
//            if (idx == 0 && repeatMode.contains("all")) {
                /* idx reprezenta indicele piesei ce ar trebui incarcata */
//                return shuffles.get(shuffles.size() - 1);
//            }
            int correspondingIdx = 0;
            for (int i = 0; i < shuffles.size(); ++i) {
                if (shuffles.get(i) == idx) {
                    correspondingIdx = i;
                    break;
                }
            }
            if (correspondingIdx == 0) {
                /* Melodia este prima din vectorul de indici amestecati, se disting cazurile */
                if (repeatMode.contains("no")) {
                    /* Nu se repeta nimic,  returnam o valoare negativa */
                    return -1;
                } else if (repeatMode.contains("all")) {
                    return shuffles.get(shuffles.size() - 1);
                } else {
                    return correspondingIdx;
                }
            }

            return shuffles.get(correspondingIdx - 1);
        }
    }

    /**
     *      Metoda verifica finalul playlist-ului
     * */
    public boolean verifyIdx(final int idx, final SongsCollection currSongsColl) {
        /* Verificam starea shuffle */
        ArrayList<Integer> shuffles = currSongsColl.getShuffledIndices();
        if (idx < 0) {
            return true;
        }
        if (!player.getStats().getShuffle()) {
            int playlistSize = currSongsColl.getSongs().size();
            return idx >= playlistSize - 1;
        } else {
            /* Cautam pe "idx" in "shuffles" */
            int corespondingIdx = -1;
            for (int i = 0; i < shuffles.size(); ++i) {
                if (shuffles.get(i) == idx) {
                    corespondingIdx = i;
                    break;
                }
            }
            return corespondingIdx == shuffles.size() - 1;
        }
    }

    /**
     *      Aceasta metoda "stabilizeaza" campul "remainedTime" pentru podcast-uri
     *      Adica, va cauta episodul din podcast la care "remainedTime" este pozitiv
     * */
    private int findFittingEpisode(final int idxEpisode) {
        PodcastInput currentPodcast = player.getLoadInfo().getSelectInfo().getPodcast();
        int remainedTime = player.getStats().getRemainedTime();
        int index = idxEpisode;

        while (remainedTime <= 0) {
            /* Verificam daca am ajuns la final de podcast */
            if (index >= currentPodcast.getEpisodes().size() - 1) {
                return -1;      // <-- Se va goli player-ul
            }

            index += 1;
            EpisodeInput episode = currentPodcast.getEpisodes().get(index);
            int initDuration = episode.getDuration();
            remainedTime += initDuration;
        }

        EpisodeInput currentEpisode = currentPodcast.getEpisodes().get(index);
        player.getStats().setRemainedTime(remainedTime);
        player.getStats().setName(currentEpisode.getName());

        return 0;
    }

    /** Getter */
    public SongsCollection getSongsCollection() {
        return player.getLoadInfo().getSelectInfo().getSongsCollection();
    }

    /** Setter */
    public void setPlayer(final Player newPlayer) {
        player = newPlayer;
    }

    /** Setter */
    public void setCurrentPage(final Page page) {
        currentPage = page;
    }

    /** Getter */
    public Player getPlayer() {
        return player;
    }

    /** Getter */
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    /** Getter */
    public ArrayList<SongInput> getLikedSongs() {
        return likedSongs;
    }

    /** Metoda returneaza lista cu numele melodiilor apreciate de user */
    public ArrayList<String> getLikedSongsNames() {
        ArrayList<String> tempRef = new ArrayList<>();
        likedSongs.forEach(song -> tempRef.add(song.getName()));
        return tempRef;
    }

    /** Getter */
    public ArrayList<Playlist> getFwdPlaylits() {
        return fwdPlaylits;
    }

    /** Aceasta metoda va "flip-ui" starea utilizatorului  */
    public void changeState() {
        state ^= true;
    }

    /** Getter */
    public boolean getState() {
        return state;
    }

    /** Getter */
    public Page getCurrentPage() {
        return currentPage;
    }

    /** Setter */
    public void setSelectInfo(final Select selectInfo) {
        this.selectInfo = selectInfo;
    }

    /** Getter */
    public Select getSelectInfo() {
        return selectInfo;
    }

}
