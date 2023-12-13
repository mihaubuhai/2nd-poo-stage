package main.users.pages;

import input.commands.CommandIn;
import main.users.Artist;
import main.users.Host;
import main.users.NormalUser;
import main.users.UserInfo;
import output.result.ResultOut;
import player.commands.Like;
import search.bar.Select;

import java.util.ArrayList;

public class Page {
    protected static StringBuilder pageContent;
    protected UserInfo usersPage;
    private PageType pageType;

    public Page(final PageType type) {
        pageType = type;
    }

    public Page(final UserInfo usersPage, final PageType type) {
        this(type);
        this.usersPage = usersPage;
    }

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
    public ResultOut getPage(final NormalUser user, final CommandIn cmd,
                                    final ArrayList<Like> topLikedSongs) {

        if (user.getCurrentPage().getPageType() == PageType.HOME) {
            return ((HomePage) this).printPage(user, topLikedSongs, cmd);
        } else {
            /* Cazurile ARTIST si HOST */
            if (usersPage.isArtist()) {
                return ((ArtistPage) this).printPage((Artist) usersPage, cmd);
            }
            return ((HostPage) this).printPage((Host) usersPage, cmd);
        }
    }

    /** Setter */
    public void setUsersPage(final UserInfo user) {
        usersPage = user;
    }

    /** Getter */
    public PageType getPageType() {
        return pageType;
    }
}
