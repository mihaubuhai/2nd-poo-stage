package main.users;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;
import playlist.commands.collections.Album;
import playlist.commands.collections.SongsCollFactory;

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
    public ResultOut addAlbum(final CommandIn command) {
        ResultOut result = new ResultOut(command);

        /* Verificam daca artistul are in colectia sa de albume inca unul cu acelasi nume */
        String currAlbum = command.getName();
        for (Album album: albums) {
            if (album.getName().equals(currAlbum)) {
                result.setMessage(getUsername() + " has another album with the same name.");
                return result;
            }
        }

        /*
            Vom adauga, pe rand, melodiile din comanda in album, apoi vom face verificare
            daca se mai gaseste, dupa nume, melodia in album
         */
        Album newAlbum = (Album) SongsCollFactory.getCollection(command);
        for (SongInput song: command.getSongs()) {
            if (!newAlbum.isInAlbum(song)) {
                newAlbum.getSongs().add(song);
                song.changeState();
            } else {
                result.setMessage(getUsername() +
                        " has the same song at least twice in this album.");
                return result;
            }
        }

        /* Ajunsi aici, albumul nu prezinta nici o problema, va fi adaugat in lista de albume */
        newAlbum.setOwner(this.getUsername());
        albums.add(newAlbum);
        result.setMessage(getUsername() + " has added new album successfully.");
        return result;
    }

    // TODO Adaug si daca e vreo melodie folosita intr-un playlist
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
                result.setMessage(artist.getUsername() + " doesn't have an album with the given name.");
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
                // TO DO //

                /* Evenimentul nu prezinta probleme, va fi adaugat */
                artist.eventsName.add(eventName);
                artist.eventsDescription.add(cmd.getDescription());
                artist.eventsDates.add(cmd.getDate());
                result.setMessage(artist.getUsername() + " has added new event successfully.");
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
                    result.setMessage(artist.getUsername() + " has added new merchandise successfully.");
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

    /** Setter */
    public void addMerchName(final String merchName) {
        merchNames.add(merchName);
    }

    /** Setter */
    public void addMerchDescription(final String description) {
        merchDescription.add(description);
    }

    /** Setter */
    public void addMerchPrice(final int price) {
        merchPrice.add(price);
    }

    /** Setter */
    public void addEventsName(final String event) {
        eventsName.add(event);
    }


}
