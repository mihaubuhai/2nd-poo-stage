package input.commands;

import java.util.ArrayList;

public class Filters {
    private String name;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private String releaseYear;
    private String artist;
    private String owner;               // Pentru playlist / podcast
    private int notnullfields = 0;   // Pentru comanda "search"

    public Filters() {
        tags = new ArrayList<>();
    }

    /** Getter */
    public int getNonNullFields() {
            return notnullfields;
    }

    /** Setter */
    public void setAlbum(final String album) {
        this.album = album;
        notnullfields++;
    }

    /** Setter */
    public void setArtist(final String artist) {
        this.artist = artist;
        notnullfields++;
    }

    /** Setter */
    public void setGenre(final String genre) {
        this.genre = genre;
        notnullfields++;
    }

    /** Setter */
    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
        notnullfields++;
    }

    /** Setter */
    public void setOwner(final String owner) {
        this.owner = owner;
        notnullfields++;
    }

    /** Setter */
    public void setName(final String name) {
        this.name = name;
        notnullfields++;
    }

    /** Setter */
    public void setReleaseYear(final String releaseYear) {
        this.releaseYear = releaseYear;
        notnullfields++;
    }

    /** Setter */
    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
        notnullfields++;
    }

    /** Getter */
    public String getOwner() {
        return owner;
    }

    /** Getter */
    public String getArtist() {
        return artist;
    }

    /** Getter */
    public String getReleaseYear() {
        return releaseYear;
    }

    /** Getter */
    public String getGenre() {
        return genre;
    }

    /** Getter */
    public String getLyrics() {
        return lyrics;
    }

    /** Getter */
    public ArrayList<String> getTags() {
        return tags;
    }

    /** Getter */
    public String getAlbum() {
        return album;
    }

    /** Getter */
    public String getName() {
        return name;
    }
}
