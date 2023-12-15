package songcollections.commands;

import fileio.input.SongInput;
import input.commands.CommandIn;
import users.Artist;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.Album;
import songcollections.collections.Playlist;
import songcollections.collections.SongsCollection;
import users.UserInfo;

import java.util.ArrayList;

/** Aceasta clasa are singurul ideal de a executa comanda "addRemove" pentru playlist */
public final class AddRemove {
    /* Constructor nefolosit, il privatizam */
    private AddRemove() {
    }

    /** Aceasta metoda implementeaza comanda "addRemove" */
    public static ResultOut addRemoveInPlaylist(final NormalUser user, final CommandIn command,
                                                final ArrayList<UserInfo> users) {
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
        SongsCollection col = user.getSelectInfo().getSongsCollection();
        if (user.getSelectInfo().getResultType() == podcastId || (col != null && !col.isAlbum())) {
            result.setMessage("The loaded source is not a song.");
            return result;
        }

        /* Verificam daca prin comanda s-a dat un playlistId invalid */
        if (user.getPlaylists().size() < command.getPlaylistId() - 1) {
            result.setMessage("The specified playlist does not exist.");
            return result;
        }

        Playlist playlist = user.getPlaylists().get(command.getPlaylistId() - 1);
        SongInput loadedSong = findSong(user);
        Album album = findAlbum(loadedSong, users);
        /*
            Iteram prin lista de melodii ale playlist-ului si
            cautam melodia care este incarcata in player
        */
        for (SongInput song: playlist.getSongs()) {
            if (song.equals(loadedSong)) {
                playlist.getSongs().remove(song);
                if (song.getIsInAlbum()) {
                    album.decrementNrSongsUsed();
                }
                result.setMessage("Successfully removed from playlist.");
                return result;
            }
        }

        /* Ajunsi aici inseamna ca melodia nu se gaseste in playlist, asa ca trebuie adaugata */
        if (user.getSelectInfo().getArtistHostName() == null) {
            /* Este incarcata o melodie din librarie */
            playlist.getSongs().add(user.getPlayer().getLoadInfo().getSelectInfo().getSong());
        } else {
            /* Este incarcata o melodie dintr-un album */
            for (SongInput song : album.getSongs()) {
                if (song.equals(loadedSong)) {
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

    private static Album findAlbum(final SongInput song, final ArrayList<UserInfo> users) {
        for (UserInfo user : users) {
            if (user.isArtist()) {
                for (Album album : ((Artist) user).getAlbums()) {
                    if (album.getName().equals(song.getAlbum())) {
                        return album;
                    }
                }
            }
        }
        return null;
    }

    private static SongInput findSong(final NormalUser user) {
        if (user.getSelectInfo().getSongsCollection() == null) {
            return user.getSelectInfo().getSong();
        }
        for (SongInput song : user.getSelectInfo().getSongsCollection().getSongs()) {
            if (song.getName().equals(user.getPlayer().getStats().getName())) {
                return song;
            }
        }
        return new SongInput();
    }

}
