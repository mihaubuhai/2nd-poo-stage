package main.users.pages;

import input.commands.CommandIn;
import main.users.Artist;
import main.users.NormalUser;
import output.result.ResultOut;
import player.commands.Like;
import search.bar.Select;

import java.util.ArrayList;

public class Page {
    protected static StringBuilder pageContent;

    public enum PageType {
        HOME,
        LIKEDCONTENT,
        ARTIST,
        HOST,
    }

    /**
     *      Metoda returneaza rezultatul comenzii "printPage"
     *      <p>
     *      Primeste ca paremetrii:
     *      <p>
     *      --> user-ul care a invocat comanda
     *      </p>
     *      --> comanda
     *      <p>
     *      --> lista de melodii apreciate de toti users
     *      </p>
     *      --> lista de selectii pentru fiecare user
     *      </p>
     * */
    public static ResultOut getPage(final NormalUser user, final CommandIn cmd,
                                    final ArrayList<Like> topLikedSongs,
                                    final ArrayList<Select> selects) {

        switch (user.getCurrentPage()) {
            case HOME -> {
                return HomePage.printPage(user, topLikedSongs, cmd);
            }

            case ARTIST -> {
                /* Se cauta informatiile de selectie ale user-ului "user" */
                Select currUserSelectInfo = null;
                for (Select info : selects) {
                    if (info.getUser().equals(user.getUsername())) {
                        currUserSelectInfo = info;
                        break;
                    }
                }

                /* Se garanteaza ca acestea exista.. */
                if (currUserSelectInfo != null) {
                    return ArtistPage.printPage((Artist) currUserSelectInfo.getArtistHostName(),
                            cmd);
                } else {
                    return new ResultOut(cmd);
                }
            }


            default -> {
                return new ResultOut(cmd);
            }
        }
    }

}
