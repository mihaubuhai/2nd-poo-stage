package top5;

import fileio.input.LibraryInput;
import javassist.compiler.ast.Visitor;
import output.result.ResultGetTop5;
import users.Artist;
import users.NormalUser;

import java.util.ArrayList;
import java.util.Collections;

public class topCreatorVisitor extends Visitor {
    int maxSize = 5;

    public ResultGetTop5 visit(final getTopAlbums toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());

        /* Sortam lista de albume */
        toVisit.getTopAlbums().sort((o1, o2) -> {
            if (o1.getTotalLikes() == o2.getTotalLikes()) {
                return o1.getName().compareTo(o2.getName());
            }
            return o2.getTotalLikes() - o1.getTotalLikes();
        });

        /* Adaugam albumele in rezultat */
        if (maxSize > toVisit.getTopAlbums().size()) {
            maxSize = toVisit.getTopAlbums().size();
        }

        for (int i = 0; i < maxSize; ++i) {
            result.getResult().add(toVisit.getTopAlbums().get(i).getName());
        }

        return result;
    }

    public ResultGetTop5 visit(final getTopPlaylists toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());
        /* Sortam playlist-urile dupa campul "followers" (in principiu) */
        Collections.sort(toVisit.getTopFwsPlaylists());

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
        toVisit.getTopLikedSongs().removeIf(song -> song.getNrLikes() == 0);
        /* Sortam melodiile dupa campul "users" */
        Collections.sort(toVisit.getTopLikedSongs());


        if (maxSize > toVisit.getTopLikedSongs().size()) {
            maxSize = toVisit.getTopLikedSongs().size();
        }

       for (int i = 0; i < maxSize; ++i) {
           result.getResult().add(toVisit.getTopLikedSongs().get(i).getSongName());
       }

       /*
            Ultima melodie posibil sa fie cu 0 like-uri;
            Trebuie schimbata in fct de ordinea ei in librarie
       */
        maxSize = 5;
        int lastSize = result.getResult().size();
        int idxOfLast = lastSize - 1;
        LibraryInput lib = toVisit.getLibrary();
        for (int i = 0; i < maxSize - lastSize; ++i) {
            int idxLibOfLast = toVisit.getTopLikedSongs().get(idxOfLast).getIdx();
            if (i <= idxLibOfLast) {
                result.getResult().add(lib.getSongs().get(i).getName());
                idxOfLast = i;
            } else {
                result.getResult().add(lib.getSongs().get(idxLibOfLast).getName());
            }
        }

        return result;
    }

    public ResultGetTop5 visit(final getTopOfUsers toVisit) {
        ResultGetTop5 output = new ResultGetTop5(toVisit.getCmd());

        switch (toVisit.getType()) {
            case ONLINEUSERS ->
                toVisit.getUsers().forEach(utilizator -> {
                    if (utilizator.isNormalUser()) {
                        if (((NormalUser) utilizator).getState()) {
                            output.getResult().add(utilizator.getUsername());
                        }
                    }
                });
            case ALLUSERS -> {
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
            case TOPARTIST -> {
                int maxSize = 5;
                ArrayList<Artist> artists = new ArrayList<>();
                toVisit.getUsers().forEach(user -> {
                    if (user.isArtist()) {
                        artists.add((Artist) user);
                    }
                });


                /* Sortam lista */
                artists.forEach(Artist::findTotalLikes);
                artists.sort((o1, o2) -> o2.getTotalAlbumsLikes() - o1.getTotalAlbumsLikes());

                if (maxSize > artists.size()) {
                    maxSize = artists.size();
                }

                for (int i = 0; i < maxSize; ++i) {
                    output.getResult().add(artists.get(i).getUsername());
                }
            }
        }

        return output;
    }

}
