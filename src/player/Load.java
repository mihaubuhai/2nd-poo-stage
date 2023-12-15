package player;

import fileio.input.SongInput;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.SongsCollection;
import search.bar.Select;

import java.util.ArrayList;

public class Load {
    private boolean loaded;
    private Select selectInfo;

    public Load(final Select selectInfo) {
        this.selectInfo = new Select(selectInfo);
        this.loaded = false;
    }

    /** Aceasta metoda implementeaza comanda "load" */
    public void loadFunc(final ResultOut result,
                                        final NormalUser currentUser,
                                        final ArrayList<Player> oldPodcastPlayers) {

        final int playlistID = 3;
        final int podcastId = 2;

        /* Verificam daca ceea ce s-a selectat este un podcast din cele salvate */
        Select currentSelectInfo = currentUser.getPlayer().getLoadInfo().getSelectInfo();
        if (currentSelectInfo.getResultType() == podcastId) {
            for (Player oldPodcastPlayer: oldPodcastPlayers) {
                Select storedSelectInfo = oldPodcastPlayer.getLoadInfo().getSelectInfo();
                String currentPodcastName = currentSelectInfo.getPodcast().getName();
                String storedPodcastName = storedSelectInfo.getPodcast().getName();
                String storedPlayerName = oldPodcastPlayer.getLoadInfo().getSelectInfo().getUser();

                if (currentPodcastName.equals(storedPodcastName)
                        && storedPlayerName.equals(currentUser.getUsername())) {
                    /*
                        S-a selectat un podcast care a rulat la un moment dat,
                        deci va trebui sa ruleze de unde a fost stopat
                    */
                    currentUser.setPlayer(oldPodcastPlayer);
                    verifyIfNotInLib(currentSelectInfo);
                    result.setMessage("Playback loaded successfully.");
                    return;
                }
            }
            /* Se poate intampla ca podcast-ul selectat sa nu fi fost incarcat inainte */
            currentUser.getPlayer().setStats(this);
        } else if (currentSelectInfo.getResultType() == playlistID) {
            /* In cazul in care ceea ce este selectat este un playlist / album */
            SongsCollection playlist = currentSelectInfo.getSongsCollection();
            /* Verificam daca playlist este gol */
            ArrayList<SongInput> playlistSongs = playlist.getSongs();
            if (playlistSongs.isEmpty()) {
                /* Selectia curenta este invalida, trebuie eliberat player-ul */
                currentUser.setPlayer(null);
                result.setMessage("You can't load an empty audio collection!");
                return;
            }
            currentUser.getPlayer().setStats(this);
        }

        setLoaded(true);
        verifyIfNotInLib(currentSelectInfo);
        selectInfo.setSearchDone(false);
        result.setMessage("Playback loaded successfully.");
    }


    /**
     * <p>
     *  Metoda este folosita pentru a verifica daca ceea ce se incarca in player nu este in librarie
     *  </p>
     *  Acest fapt se datoreaza comenzii "deleteUser", pentru care un caz de esec se numara si
     *  situatia in care un album / podcast / playlist este ascultat de cineva la momentul invocarii
     *   comenzii
     *  <p>
     *  Verificarea se bazeaza pe compararea campului "artisHostName" din clasa "Select"..
     *  Daca este null, atunci ceea ce s-a selectat se afla in librarie; altfel nu este null
     * */
    private void verifyIfNotInLib(final Select selection) {
        if (selection.getArtistHostName() != null) {
            selection.incrementNrListeners();
        }
    }



    /** Setter */
    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }

    /** Getter */
    public boolean getLoaded() {
        return loaded;
    }

    /** Getter */
    public Select getSelectInfo() {
        return selectInfo;
    }

}
