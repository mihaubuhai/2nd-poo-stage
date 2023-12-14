package songcollections.commands;

import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.Album;
import songcollections.collections.Playlist;
import songcollections.collections.SongsCollection;

import java.util.ArrayList;

/** Aceasta clasa are singurul ideal de a executa comanda "addRemove" pentru playlist */
public final class AddRemove {
    /* Constructor nefolosit, il privatizam */
    private AddRemove() {
    }

    /** Aceasta metoda implementeaza comanda "addRemove" */
    public static ResultOut addRemoveInPlaylist(final NormalUser user, final CommandIn command,
                                                final ArrayList<SongInput> librarySongs) {
        ResultOut result = new ResultOut(command);

        /* Verificam daca player-ul ruleaza */
        if (user.getPlayer() == null) {
            String out = "Please load a source before adding to or removing from the playlist.";
            result.setMessage(out);
            return result;
        }
        final int podcastId = 2;

        /*
            Verificam daca s-a selectat un podcast sau un playlist,
            intrucat functionalitatea acestei metode nu suporta si aceste optiuni
        */
        SongsCollection col = user.getPlayer().getLoadInfo().getSelectInfo().getSongsCollection();
        if (user.getPlayer().getLoadInfo().getSelectInfo().getResultType() == podcastId ||
                (col != null && !col.isAlbum())) {
            result.setMessage("The loaded source is not a song.");
            return result;
        }

        /* Verificam daca prin comanda s-a dat un playlistId invalid */
        if (user.getPlaylists().size() < command.getPlaylistId() - 1) {
            result.setMessage("The specified playlist does not exist.");
            return result;
        }

        Playlist playlist = user.getPlaylists().get(command.getPlaylistId() - 1);
        String loadedSong = user.getPlayer().getStats().getName();
        Album album = (Album) col;
        /*
            Iteram prin lista de melodii ale playlist-ului si
            cautam melodia care este incarcata in player
        */
        for (SongInput song: playlist.getSongs()) {
            if (song.getName().contains(loadedSong)) {
                playlist.getSongs().remove(song);
                if (song.getIsInAlbum()) {
                    album.decrementNrSongsUsed();
                }
                result.setMessage("Successfully removed from playlist.");
                return result;
            }
        }

        /* Ajunsi aici inseamna ca melodia nu se gaseste in playlist, asa ca trebuie adaugata */
        if (user.getPlayer().getLoadInfo().getSelectInfo().getArtistHostName() == null) {
            /* Este incarcata o melodie din librarie */
            playlist.getSongs().add(user.getPlayer().getLoadInfo().getSelectInfo().getSong());
        } else {
            /* Este incarcata o melodie dintr-un album */
            for (SongInput song : album.getSongs()) {
                if (song.getName().equals(loadedSong)) {
                    playlist.getSongs().add(song);
                    break;
                }
            }

            /* v-- Se contorizeaza faptul ca o piesa a albumului este folosita */
            album.incrementNrSongsUsed();
        }

        result.setMessage("Successfully added to playlist.");
        return result;
    }
}
