package top5;

import fileio.input.LibraryInput;
import javassist.compiler.ast.Visitor;
import output.result.ResultGetTop5;
import users.NormalUser;

import java.util.Collections;

public class topCreatorVisitor extends Visitor {
    public ResultGetTop5 visit(final getTopAlbums toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());

        /* Sortam lista de albume */
        toVisit.getTopAlbums().sort((o1, o2) -> {
            if (o1.getTotalLikes() == o2.getTotalLikes()) {
                return o2.getName().compareTo(o1.getName());
            }
            return o2.getTotalLikes() - o1.getTotalLikes();
        });

        /* Adaugam albumele in rezultat */
        toVisit.getTopAlbums().forEach(album -> result.getResult().add(album.getName()));

        return result;
    }

    public ResultGetTop5 visit(final getTopPlaylists toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());
        /* Sortam playlist-urile dupa campul "followers" (in principiu) */
        Collections.sort(toVisit.getTopFwsPlaylists());

        final int maxSize = 5;
        /* Verificam daca sunt mai mult de 5 elemente in top-ul playlist-urilor */
        if (toVisit.getTopFwsPlaylists().size() > maxSize) {
            for (int i = 0; i < maxSize; i++) {
                result.getResult().add(toVisit.getTopFwsPlaylists().get(i).getPlaylistName());
            }
        } else {
            toVisit.getTopFwsPlaylists().forEach(memb ->
                    result.getResult().add(memb.getPlaylistName()));
        }

        return result;
    }

    public ResultGetTop5 visit(final getTopSongs toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());
        /* Sortam melodiile dupa campul "users" */
        Collections.sort(toVisit.getTopLikedSongs());

        final int maxSize = 5;
        /* Verificam daca topul contine mai mult de 5 piese */
        if (toVisit.getTopLikedSongs().size() > maxSize) {
            /* In acest caz, toate melodiile sunt sortate aferent, nu exista probleme */
            for (int i = 0; i < maxSize; i++) {
                result.getResult().add(toVisit.getTopLikedSongs().get(i).getSongName());
            }
        } else {
            /* Se adauga melodiile care au cel putin un follower (acestea sunt sortate corespunzator) */
            toVisit.getTopLikedSongs().forEach(song -> {
                if (song.getUsers() > 0) {
                    result.getResult().add(song.getSongName());
                }
            });
            /* In acest moment,  exista locuri goale (cele care au followers = 0) */

            int idxOfZero = result.getResult().size();
            // ^--- Indicele (din top5Songs) al primei melodii neadaugate in "result"
            int initSize = result.getResult().size();
            for (int i = 0; i < maxSize - initSize; ++i) {
                String tempRef;
                if (i <= idxOfZero) {
                    tempRef = toVisit.getLibrary().getSongs().get(i).getName();
                } else {
                    tempRef = toVisit.getLibrary().getSongs().get(idxOfZero).getName();
                }
                result.getResult().add(tempRef);
            }
        }

        return result;
    }

    public ResultGetTop5 visit(final getTopOfUsers toVisit) {
        ResultGetTop5 output = new ResultGetTop5(toVisit.getCmd());

        if (toVisit.getType()) {
            toVisit.getUsers().forEach(utilizator -> {
                /* Verificam daca sunt user-i normali, apoi daca sunt online */
                if (utilizator.isNormalUser()) {
                    if (((NormalUser) utilizator).getState()) {
                        output.getResult().add(utilizator.getUsername());
                    }
                }
            });
        } else {
            toVisit.getUsers().forEach(user -> {
                if (user.isNormalUser()) {
                    output.getResult().add(user.getUsername());
                }
            });
            toVisit.getUsers().forEach(user -> {
                if (user.isArtist()) {
                    output.getResult().add(user.getUsername());
                }
            });
            toVisit.getUsers().forEach(user -> {
                if (user.isHost()) {
                    output.getResult().add(user.getUsername());
                }
            });
        }

        return output;
    }

}
