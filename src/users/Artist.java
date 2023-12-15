package users;

import fileio.input.SongInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;
import songcollections.collections.Album;
import songcollections.collections.SongsCollFactory;

import java.util.ArrayList;

/**
 *      Clasa care infatiseaza un artist
 * */
public final class Artist extends UserInfo {
    private final ArrayList<Album> albums;
    private final ArrayList<String> merchNames;
    private final ArrayList<String> merchDescription;
    private final ArrayList<Integer> merchPrice;
    private final ArrayList<String> eventsName;
    private final ArrayList<String> eventsDescription;
    private final ArrayList<String> eventsDates;
    private int totalAlbumsLikes;

    public Artist(final UserInput userInfo) {
        setUserInfo(userInfo);
        userType = UserType.ARTIST;
        albums = new ArrayList<>();
        merchNames = new ArrayList<>();
        merchDescription = new ArrayList<>();
        merchPrice = new ArrayList<>();
        eventsName = new ArrayList<>();
        eventsDescription = new ArrayList<>();
        eventsDates = new ArrayList<>();
    }

    /** Metoda care implementeaza comanda "addAlbum"
     *    <p>
     *    Aceasta primeste ca paramentru detaliile comenzii "addAlbum"
     *    </p>
     *    Returneaza un obiect de tipul rezultatului corespunzator comenzii
     * */
    public ResultOut addAlbum(final CommandIn cmd, final ArrayList<Album> topAlbums,
                              final UserInfo user) {
        ResultOut result = new ResultOut(cmd);

        if (!user.isArtist()) {
            /* Verificam daca user-ul care a apelat aceasta metoda este artist */
            result.setMessage(user.getUsername() + " is not an artist.");
            return result;
        }

        Artist artist = (Artist) user;

        /* Verificam daca artistul are in colectia sa de albume inca unul cu acelasi nume */
        String currAlbum = cmd.getName();
        for (Album album: artist.getAlbums()) {
            if (album.getName().equals(currAlbum)) {
                result.setMessage(artist.getUsername() + " has another album with the same name.");
                return result;
            }
        }

        /*
            Vom adauga, pe rand, melodiile din comanda in album, apoi vom face verificare
            daca se mai gaseste, dupa nume, melodia in album
         */
        Album newAlbum = (Album) SongsCollFactory.getCollection(cmd);
        for (SongInput song: cmd.getSongs()) {
            if (!newAlbum.isInAlbum(song.getName())) {
                newAlbum.getSongs().add(song);
                song.changeState();
                song.addTimestampAdded(cmd.getTimestamp());
            } else {
                result.setMessage(artist.getUsername()
                        + " has the same song at least twice in this album.");
                return result;
            }
        }

        /* Ajunsi aici, albumul nu prezinta nici o problema, va fi adaugat in lista de albume */
        newAlbum.setOwner(artist);
        artist.getAlbums().add(newAlbum);
        topAlbums.add(newAlbum);
        result.setMessage(artist.getUsername() + " has added new album successfully.");
        return result;
    }

    /**
     *      Metoda implementeaza comanda "removeAlbum"<p>
     *      Primeste ca parametru comanda invocata si user-ul care a invocat-o <p>
     *      Returneaza un obiect pe tiparul rezultatului comenzii
     * */
    public ResultOut removeAlbum(final CommandIn cmd, final UserInfo user) {
        ResultOut result = new ResultOut(cmd);
        Album toRemoveAlbum = null;

        if (!user.isArtist()) {
            result.setMessage(user.getUsername() + " is not an artist.");
        } else {
            Artist artist = (Artist) user;
            /* Cautam albumul specificat in comanda si retinem referinta catre acesta */
            for (Album album : artist.getAlbums()) {
                if (album.getName().equals(cmd.getName())) {
                    toRemoveAlbum = album;
                    break;
                }
            }

            /* Verificam daca artistul are un album cu numele dat in cmd */
            if (toRemoveAlbum == null) {
                result.setMessage(artist.getUsername()
                        + " doesn't have an album with the given name.");
            } else {
                /* Albumul exista, verificam daca sunt ascultatori al lui / al vreunei piese */
                if (toRemoveAlbum.getNrListeners() > 0 || toRemoveAlbum.getNrSongsUsed() > 0) {
                    result.setMessage(artist.getUsername() + " can't delete this album.");
                } else {
                    /* Albumul poate fi sters */
                    artist.getAlbums().remove(toRemoveAlbum);
                    result.setMessage(artist.getUsername() + " deleted the album successfully.");
                }
            }
        }

        return result;
    }

    /**
     *      <p>
     *      Metoda care adauga un eveniment
     *      </p>
     *      <p>
     *          Primeste ca parametru user-ul care a invocat comanda si comanda in sine
     *      </p>
     *      Returneaza un obiect pe tiparul rezultatului comenzii
     * */
    public ResultOut addEvent(final UserInfo user, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        /* Se verifica daca user este artist, intrucat cazul in care nu exista este acoperit deja*/
        if (!user.isArtist()) {
            result.setMessage(user.getUsername() + " is not an artist.");
        } else {
            /* Verificam daca artistul mai are un eveniment cu acelasi nume */
            Artist artist = (Artist) user;
            String eventName = cmd.getName();
            if (artist.getEventsName().contains(eventName)) {
                result.setMessage(artist.getUsername() + " has another event with the same name.");
            } else {
                /* Artistul nu are acest eveniment, verificam daca data este valida */
                String date = cmd.getDate();
                String[] info = date.split("-");

                final int day = Integer.parseInt(info[0]);
                final int month = Integer.parseInt(info[1]);
                final int year = Integer.parseInt(info[2]);

                final int lastMonth = 12;
                final int earliestYear = 1900;
                final int currYear = 2023;
                final int lastDay = 31;
                final int specialDay = 28;

                if (month > lastMonth || year < earliestYear || year > currYear || day > lastDay
                        || (month == 2 && day > specialDay)) {
                    result.setMessage("Event for " + artist.getUsername()
                            + " does not have a valid date.");
                } else {
                    /* Evenimentul nu prezinta probleme, va fi adaugat */
                    artist.eventsName.add(eventName);
                    artist.eventsDescription.add(cmd.getDescription());
                    artist.eventsDates.add(cmd.getDate());
                    result.setMessage(artist.getUsername() + " has added new event successfully.");
                }
            }
        }

        return result;
    }


    /** Metoda implementeaza comanda "removeEvent"<p>
     *   Primeste ca parametrii user-ul care a invocat comanda si comanda in sine <p>
     *   Returneaza un obiect pe tiparul rezultatului comenzii
     * */
    public ResultOut removeEvent(final UserInfo user, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        /* Se verifica daca user-ul este artist */
        if (!user.isArtist()) {
            result.setMessage(user.getUsername() + " is not an artist.");
        } else {
            Artist artist = (Artist) user;
            String eventName = cmd.getName();

            /* Se verifica daca artistul are un eveniment cu numele dat in cmd */
            if (!artist.getEventsName().contains(eventName)) {
                result.setMessage(artist.getUsername()
                        + " doesn't have an event with the given name.");
            } else {
                /* Evenimentul se poate sterge cu succes */
                int idx = artist.getEventsName().indexOf(eventName);

                artist.getEventsName().remove(eventName);
                artist.getEventsDates().remove(idx);
                artist.getEventsDescription().remove(idx);
                result.setMessage(artist.getUsername() + " deleted the event successfully.");
            }
        }

        return result;
    }

    /**     <p>
     *      Metoda care adauga un merch
     *      </p>
     *      <p>
     *      Primeste ca parametrii user-ul care a invocat comanda si comanda
     *          </p>
     *          Returneaza un obiect de forma mesajului de output al comenzii
     *      </p>
     *
     * */
    public ResultOut addMerch(final UserInfo user, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        /* Verificam daca user-ul care a dat comanda este artist */
        if (!user.isArtist()) {
            result.setMessage(user.getUsername() + " is not an artist.");
        } else {
            /* Verificam daca mai exista un merch cu acelasi nume */
            Artist artist = (Artist) user;
            String merchName = cmd.getName();
            if (artist.getMerchNames().contains(merchName)) {
                result.setMessage(artist.getUsername() + " has merchandise with the same name.");
            } else {
                /* Verificam daca pretul este pozitiv */
                if (cmd.getPrice() < 0) {
                    result.setMessage("Price for merchandise can not be negative.");
                } else {
                    /* Merch-ul nu prezinta probleme, va fi adaugat */
                    artist.getMerchNames().add(merchName);
                    artist.getMerchDescription().add(cmd.getDescription());
                    artist.getMerchPrice().add(cmd.getPrice());
                    result.setMessage(artist.getUsername()
                            + " has added new merchandise successfully.");
                }
            }
        }

        return result;
    }

    /** Getter */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /** Getter */
    public  ArrayList<String> getMerchNames() {
        return merchNames;
    }

    /** Getter */
    public ArrayList<String> getMerchDescription() {
        return merchDescription;
    }

    /** Getter */
    public ArrayList<Integer> getMerchPrice() {
        return merchPrice;
    }

    /** Getter */
    public ArrayList<String> getEventsName() {
        return eventsName;
    }

    /** Getter */
    public ArrayList<String> getEventsDescription() {
        return eventsDescription;
    }

    /** Getter */
    public ArrayList<String> getEventsDates() {
        return eventsDates;
    }

    /** Metoda care calculeaza campul totalAlbumsLikes */
    public void findTotalLikes() {
        for (Album album : albums) {
            totalAlbumsLikes += album.getTotalLikes();
        }
    }

    /** Getter */
    public int getTotalAlbumsLikes() {
        return totalAlbumsLikes;
    }


}
