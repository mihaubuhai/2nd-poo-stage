package tops;

import fileio.input.LibraryInput;
import javassist.compiler.ast.Visitor;
import output.result.ResultGetTop5;
import users.Artist;
import users.NormalUser;

import java.util.ArrayList;
import java.util.Collections;


/** Aceasta clasa contine toti algoritmii care implementeaza "getTop*" */
public class TopCreatorVisitor extends Visitor {
    private final int upperbound = 5;
    private int maxSize = upperbound;

    /** Implementarea comenzii "getTop5Albums" <p>
     *    Primeste ca parametru clasa TopAlbums ce contine lista totala de albume <p>
     *    Returneaza rezultatul comenzii invocate (cum se doreste in fisierele .json)
     * */
    public ResultGetTop5 visit(final TopAlbums toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());

        /* Sortam lista de albume */
        toVisit.getTopAlbums().sort((o1, o2) -> {
            if (o1.getTotalLikes() == o2.getTotalLikes()) {
                return o1.getName().compareTo(o2.getName());
                /* Sortam crescator lexicografic ^^ */
            }
            /* Sortam descrescator dupa numarul total de aprecieri */
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

    /** Metoda implementeaza "getTop5Playlists" <p>
     *    Primeste ca parametru clasa TopPlaylists care contine lista de statistici
     *    ale tuturor playlist-urilor <p>
     *    Returneaza rezultatul comenzii, un obiect pe tiparul cerut
     * */
    public ResultGetTop5 visit(final TopPlaylists toVisit) {
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

    /** Metoda implementeaza "getTop5Songs" <p>
     *  Primeste ca parametru clasa TopSongs care contine lista de melodii apreciate pe tot
     *  programul <p>
     *  Returneaza un obiect pe tiparul rezultatului comenzii
     * */
    public ResultGetTop5 visit(final TopSongs toVisit) {
        ResultGetTop5 result = new ResultGetTop5(toVisit.getCmd());

        /* Nu au rost melodiile cu 0 like-uri; le eliminam */
        toVisit.getTopLikedSongs().removeIf(song -> song.getNrLikes() == 0);

        /*
        *   Melodiile sunt sortate astfel:
        *       --> descrescator dupa numar de like-uri (daca difera)
        *       --> crescator, daca sunt din biblioteca
        *       --> sunt adaugate in ordinea in care au fost adaugate in program prin albume,
        *               daca piesele nu sunt din biblioteca
        * */
        Collections.sort(toVisit.getTopLikedSongs());


        if (maxSize > toVisit.getTopLikedSongs().size()) {
            maxSize = toVisit.getTopLikedSongs().size();
        }

       for (int i = 0; i < maxSize; ++i) {
           result.getResult().add(toVisit.getTopLikedSongs().get(i).getSongName());
       }

       /* Posibil locuri goale in rezultat; Adaugam din biblioteca */
        maxSize = upperbound;
        int lastSize = result.getResult().size();
        int idxOfLast = lastSize - 1;
        LibraryInput lib = toVisit.getLibrary();
        /* Comparatiile se fac, identic, ca la sortarea de mai sus */
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

    /** Metoda implementeaza toate cazurile de "get[CEVA]User / Artist" <p>
     *  Primeste ca parametru clasa TopOfUsers care contine lista cu toti useri programului <p>
     *   Returneaza un obiect de tipul rezultatului comenzii
     * */
    public ResultGetTop5 visit(final TopOfUsers toVisit) {
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
            default -> {
                maxSize = upperbound;
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
