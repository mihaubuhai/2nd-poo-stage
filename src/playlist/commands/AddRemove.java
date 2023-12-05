package playlist.commands;

import fileio.input.SongInput;
import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultOut;
import playlist.commands.collections.Playlist;

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
        final int songId = 1;
        /*Verificam daca player-ul ruleaza o melodie (prin resultType al selectInfo ne dam seama)*/
        if (user.getPlayer().getLoadInfo().getSelectInfo().getResultType() != songId) {
            result.setMessage("The loaded source is not a song.");
            return result;
        }
        /* Verificam daca prin comanda s-a dat un playlistId invalid */
        if (user.getPlaylists().size() < command.getPlaylistId() - 1) {
            result.setMessage("The specified playlist does not exist.");
            return result;
        }

        Playlist playlist = user.getPlaylists().get(command.getPlaylistId() - 1);
        String currentSong = user.getPlayer().getLoadInfo().getSelectInfo().getSong().getName();
        /*
            Iteram prin lista de melodii ale playlist-ului si
            cautam melodia care este incarcata in player
        */
        for (SongInput song: playlist.getSongs()) {
            if (song.getName().contains(currentSong)) {
                playlist.getSongs().remove(song);
                result.setMessage("Successfully removed from playlist.");
                return result;
            }
        }

        /* Ajunsi aici inseamna ca melodia nu se gaseste in playlist, asa ca trebuie adaugata */
        for (SongInput song: librarySongs) {
            if (song.getName().contains(currentSong)) {
                playlist.getSongs().add(song);
                break;
            }
        }
        result.setMessage("Successfully added to playlist.");
        return result;
    }
}
