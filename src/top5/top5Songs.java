package top5;

import fileio.input.LibraryInput;
import input.commands.CommandIn;
import output.result.ResultGetTop5;
import player.commands.Like;

import java.util.ArrayList;
import java.util.Collections;

/**
 *      Clasa a fost creata pentru a executa comanda "getTop5Songs"
 * */
public final class top5Songs {
    private static top5Songs instance = null;

    private top5Songs() {

    }

    /** Clasa este singletton, se returneaza o instanta a acesteia */
    public static top5Songs getInstance() {
        if (instance == null) {
            instance = new top5Songs();
        }
        return instance;
    }

    /** Metoda de mai jos asigura executarea comenzii "getTop5Songs" */
    public ResultGetTop5 getTop5Songs(final CommandIn command,final ArrayList<Like> topLikedSongs,
                                      final LibraryInput library) {
        ResultGetTop5 result = new ResultGetTop5(command);
        /* Sortam melodiile dupa campul "users" */
        Collections.sort(topLikedSongs);

        final int maxSize = 5;
        /* Verificam daca topul contine mai mult de 5 piese */
        if (topLikedSongs.size() > maxSize) {
            /* In acest caz, toate melodiile sunt sortate aferent, nu exista probleme */
            for (int i = 0; i < maxSize; i++) {
                result.getResult().add(topLikedSongs.get(i).getSongName());
            }
        } else {
            /* Se adauga melodiile care au cel putin un follower (acestea sunt sortate corespunzator) */
            topLikedSongs.forEach(song -> {
                if (song.getUsers() > 0) {
                    result.getResult().add(song.getSongName());
                }
            });
            /* In acest moment,  exista locuri goale(cele care au followers = 0) */
            int idxOfZero = result.getResult().size();      // <--- Indicele (din top5Songs) al primei melodii neadaugate in "result"
            int initSize = result.getResult().size();
            for (int i = 0; i < maxSize - initSize; ++i) {
                result.getResult().add(checkValidity(library, idxOfZero, i));
            }
        }

        return result;
    }

    /**
     *      Metoda compara indicii unor melodii din biblioteca si returneaza numele melodiei
     *      in functie de ordinea lor in bibilioteca.
     * */
    private String checkValidity(final LibraryInput library, final int idxOfZero, final int idxLib) {
        if (idxLib <= idxOfZero) {
            return library.getSongs().get(idxLib).getName();
        } else {
            return library.getSongs().get(idxOfZero).getName();
        }
    }

}
