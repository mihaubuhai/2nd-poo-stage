package main.users.pages;

import input.commands.CommandIn;
import main.users.Artist;
import output.result.ResultOut;

/** Aceasta clasa contine metoda care creeaza pagina unui artist */
public class ArtistPage extends Page {
    private ArtistPage() {
    }

    /**
     *      Metoda de mai jos implementeaza "printPage" pentru artist
     *      <p>
     *      Primeste ca parametrii artistul a carei pagina sa se afiseze si comanda care a invocat metoda </p>
     *      Returneaza un obiect de forma mesajului de output al comenzii
     * */
    public static ResultOut printPage(final Artist artist, final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);
        pageContent = new StringBuilder("Albums:\n\t[");
        int pageLength = 0;

        /* Vom afisa toate albumele (daca exista vreunul) */
        int tempSize = artist.getAlbums().size();
        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            artist.getAlbums().forEach(album -> pageContent.append(album.getName()).append(", "));
            pageLength = pageContent.length();
            /* La finalul functiei lambda, va fi ",  " in plus; trebuie eliminat */
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        pageContent.append("\n\nMerch:\n\t[");
        tempSize = artist.getMerchNames().size();
        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            for (int i = 0; i < tempSize; ++i) {
                pageContent.append(artist.getMerchNames().get(i));
                pageContent.append(" - ");
                pageContent.append(artist.getMerchPrice().get(i).toString());
                pageContent.append(":\n\t");
                pageContent.append(artist.getMerchDescription().get(i));
                pageContent.append(", ");
            }

            pageLength = pageContent.length();
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        pageContent.append("\n\nEvents:\n\t[");
        tempSize = artist.getEventsName().size();
        /* Verificam daca sunt evenimente adaugate */
        if (tempSize == 0) {
            pageContent.append("]");
        } else {
            for (int i = 0; i < tempSize; ++i) {
                pageContent.append(artist.getEventsName().get(i));
                pageContent.append(" - ");
                pageContent.append(artist.getEventsDates().get(i));
                pageContent.append(":\n\t");
                pageContent.append(artist.getEventsDescription().get(i));
                pageContent.append(", ");
            }

            pageLength = pageContent.length();
            /* La finalul for-ului, in plus vor fi caracterele ", " care trebuie eliminate */
            pageContent.replace(pageLength - 2, pageLength, "]");
        }

        result.setMessage(pageContent.toString());

        return result;
    }

}
