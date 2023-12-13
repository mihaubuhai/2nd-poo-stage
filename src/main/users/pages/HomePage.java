package main.users.pages;

import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultGetTop5;
import output.result.ResultOut;
import player.commands.Like;
import playlist.commands.collections.Playlist;

import java.util.ArrayList;

/** Clasa care implementeaza pagina Home a unui user normal */
public class HomePage extends Page {
    private HomePage() {
    }

    /**
     *      <p>
     *      Metoda returneaza rezultatul comenzii "printCurrentPage"
     *      </p>
     *      Primeste ca parametrii user-ul care a invocat comanda
     * */
    public static ResultOut printPage(final NormalUser user, final ArrayList<Like> topLikedSongs, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        pageContent = new StringBuilder("Liked songs:\n\t[");

        /* Compunem lista de clase Like corespunzatoare melodiilor apreciate de user curent */
        ArrayList<Like> tempLikes = new ArrayList<>();
        for (Like song: topLikedSongs) {
            for (String userLikes: user.getLikedSongs()) {
                if (userLikes.equals(song.getSongName())) {
                    tempLikes.add(song);
                }
            }
        }

        /* Le sortam descrescator */
        tempLikes.sort((o1, o2) -> o2.getUsers() - o1.getUsers());

        /* Verificam daca sunt mai putin de 5 melodii in lista de mai sus */
        int tempSize = 5;
        if (tempSize > tempLikes.size()) {
            /* Nu putem afisa mai mult de 5 melodii, intrucat nu sunt atatea apreciate */
            tempSize = tempLikes.size();
        }

        for (int i = 0; i < tempSize; ++i) {
            pageContent.append(tempLikes.get(i).getSongName()).append(", ");
        }

        if (tempSize > 0) {
            /* v--- va aparea o virgula in plus la finalul for-ului, trebuie stearsa */
            pageContent.replace(pageContent.length() - 2, pageContent.length(), "]");
        } else {
            pageContent.append("]");
        }
        pageContent.append("\n\nFollowed playlists:\n\t[");

        tempSize = 5;

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
