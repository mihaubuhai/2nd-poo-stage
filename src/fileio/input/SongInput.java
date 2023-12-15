package fileio.input;

import java.util.ArrayList;

public final class SongInput {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private boolean isInAlbumClass;
    //^-- Daca se instantiaza un album si melodia este apartenenta
    private int songLikes;
    private int timestampAdded;
    // ^--- Retine la ce timestamp a fost adaugata piesa

    public SongInput() {
        isInAlbumClass = false;
    }

    /** Metoda care flip-uieste campul isInAlbum */
    public void changeState() {
        isInAlbumClass ^= true;
    }

    /** Metoda care intoarce numarul de like-uri al unei melodii */
    public int retrieveNrLikes() {
        return songLikes;
    }

    /** Metoda care incrementeaza numarul de like-uri al unei melodii */
    public void incNrLikes() {
        songLikes++;
    }

    /** Metoda care decrementeaza numarul de like-uri al unei melodii */
    public void decNrLikes() {
        if (songLikes > 0) {
            songLikes--;
        }
    }

    public boolean getIsInAlbum() {
        return isInAlbumClass;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void addTimestampAdded(final int time) {
        timestampAdded = time;
    }

    public int retrieveTimestampAdded() {
        return timestampAdded;
    }

    @Override
    public String toString() {
        return "SongInput{"
                + "name='" + name + '\''
                + ", duration=" + duration
                + ", album='" + album + '\''
                + ", tags=" + tags
                + ", lyrics='" + lyrics + '\''
                + ", genre='" + genre + '\''
                + ", releaseYear='" + releaseYear + '\''
                + ", artist='" + artist + '\''
                + '}';
    }
}
