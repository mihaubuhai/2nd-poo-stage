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
public class Artist extends UserInfo {
    private ArrayList<Album> albums;

    public Artist(final UserInput userInfo) {
        setUserInfo(userInfo);
        albums = new ArrayList<>();
        userType = UserType.ARTIST;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /** Metoda care implementeaza comanda "addAlbum"
     *    <p>
     *    Aceasta primeste ca paramentru detaliile comenzii "addAlbum"
     *    </p>
     *    Returneaza un obiect de tipul rezultatului corespunzator comenzii
     * */
    public ResultOut addAlbum(final CommandIn command, final LibraryInput lib) {
        ResultOut result = new ResultOut(command);

        /* Verificam daca user-ul care a apelat aceasta metoda este artist */
        if (!isArtist()) {
            result.setMessage(getUsername() + " is not an artist.");
            return result;
        }

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
            } else {
                result.setMessage(getUsername() +
                        " has the same song at least twice in this album.");
                return result;
            }
        }

        /* Ajunsi aici, albumul nu prezinta nici o problema, va fi adaugat in lista de albume */
        albums.add(newAlbum);
        /*
            Pentru a facilita cautarea unei melodii, cele din albume se vor adauga in librarie
        */
        newAlbum.getSongs().forEach(song -> lib.getSongs().add(song));
        result.setMessage(getUsername() + " has added new album successfully.");
        return result;
    }

}
