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
    protected UserInfo usersPage;   // Referinta catre artist / host al carei pagina sa se afiseze
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

        if (pageType == PageType.HOME) {
            return ((HomePage) this).printPage(user, topLikedSongs, cmd);
        } else if (pageType == PageType.LIKEDCONTENT) {
            return ((LikedContentPage) this).printPage(user, cmd);
        } else {
            /* Cazurile ARTIST si HOST */
            if (usersPage.isArtist()) {
                return ((ArtistPage) this).printPage((Artist) usersPage, cmd);
            }
            return ((HostPage) this).printPage((Host) usersPage, cmd);
        }
    }

    /**
     * <p>
     * Aceasta metoda implementeaza comanda "changePage"
     * <p>
     *  Va fi apelata de catre user-ul care a invocat comanda
     *  <p>
     *  Primeste ca parametrii comanda si user-ul
     *  </p>
     *  Returneaza un obiect ce este rezultatul comenzii
     * */
    public ResultOut changePage(final CommandIn cmd, final NormalUser user) {
        ResultOut result = new ResultOut(cmd);

        switch (cmd.getNextPage()) {
            case "Home" -> {
                user.setCurrentPage(new HomePage(PageType.HOME));
                result.setMessage(user.getUsername() + " accessed " + cmd.getNextPage() + " successfully.");
            }
            case "LikedContent" -> {
                user.setCurrentPage(new LikedContentPage(PageType.LIKEDCONTENT));
                result.setMessage(user.getUsername() + " accessed " + cmd.getNextPage() + " successfully.");
            }
            default -> result.setMessage(user + " is trying to access a non-existent page.");
        }

        return result;
    }

    /** Getter */
    public UserInfo getUsersPage() {
        return usersPage;
    }

    /** Getter */
    public PageType getPageType() {
        return pageType;
    }
}
