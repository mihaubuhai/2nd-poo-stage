package users.pages;

import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.Playlist;

public class LikedContentPage extends Page {
    public LikedContentPage(final PageType type) {
        super(type);
    }

    /**<p>
     *  Metoda construieste pagina "LikedContent"
     * <p>
     * Primeste ca parametrii user-ul care a invocat comanda de afisare a paginii si comanda
     * </p>
     *  Returneaza un obiect de tipul rezultatului aferent comenzii
     * */
    public ResultOut printPage(final NormalUser user, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        pageContent = new StringBuilder("Liked songs:\n\t[");
        int tempSize = user.getLikedSongs().size();
        int pageLength;

        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            for (SongInput song : user.getLikedSongs()) {
                pageContent.append(song.getName());
                pageContent.append(" - ");
                pageContent.append(song.getArtist());
                pageContent.append(", ");
            }

            /* La final, vor fi in plus caracterele: ", " --> le eliminam */
            pageLength = pageContent.length();
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        pageContent.append("\n\nFollowed playlists:\n\t[");
        tempSize = user.getFwdPlaylits().size();

        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            for (Playlist playlist : user.getFwdPlaylits()) {
                pageContent.append(playlist.getName());
                pageContent.append(" - ");
                pageContent.append(playlist.getOwner().getUsername());
                pageContent.append(", ");
            }

            pageLength = pageContent.length();
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        result.setMessage(pageContent.toString());

        return result;
    }
}
