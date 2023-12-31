package users.pages;

import fileio.input.SongInput;
import input.commands.CommandIn;
import users.NormalUser;
import output.result.ResultOut;
import songcollections.collections.Playlist;

import java.util.ArrayList;

/** Clasa care implementeaza pagina Home a unui user normal */
public class HomePage extends Page {
    public HomePage(final PageType type) {
        super(type);
    }

    private final int upperBound = 5;

    /**
     *      <p>
     *      Metoda returneaza rezultatul comenzii "printCurrentPage"
     *      </p>
     *      Primeste ca parametrii user-ul care a invocat comanda
     * */
    public ResultOut printPage(final NormalUser user, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        pageContent = new StringBuilder("Liked songs:\n\t[");

        ArrayList<SongInput> tempLikes = new ArrayList<>(user.getLikedSongs());

        tempLikes.sort((o1, o2) -> o2.retrieveNrLikes() - o1.retrieveNrLikes());

        /* Verificam daca sunt mai putin de 5 melodii in lista de mai sus */
        int tempSize = upperBound;
        if (tempSize > tempLikes.size()) {
            /* Nu putem afisa mai mult de 5 melodii, intrucat nu sunt atatea apreciate */
            tempSize = tempLikes.size();
        }

        for (int i = 0; i < tempSize; ++i) {
            pageContent.append(tempLikes.get(i).getName()).append(", ");
        }

        if (tempSize > 0) {
            /* v--- va aparea o virgula in plus la finalul for-ului, trebuie stearsa */
            pageContent.replace(pageContent.length() - 2, pageContent.length(), "]");
        } else {
            pageContent.append("]");
        }
        pageContent.append("\n\nFollowed playlists:\n\t[");

        tempSize = upperBound;

        ArrayList<Playlist> tempPlaylists = new ArrayList<>(user.getFwdPlaylits());
        tempPlaylists.sort((o1, o2) -> o2.getTotalLikes() - o1.getTotalLikes());

        if (tempSize > tempPlaylists.size()) {
            tempSize = tempPlaylists.size();
        }

        for (int i = 0; i < tempSize; ++i) {
            pageContent.append(tempPlaylists.get(i).getName()).append(", ");
        }

        if (tempSize > 0) {
            pageContent.replace(pageContent.length() - 2, pageContent.length(), "]");
        } else {
            pageContent.append("]");
        }
        result.setMessage(pageContent.toString());

        return result;
    }
}
