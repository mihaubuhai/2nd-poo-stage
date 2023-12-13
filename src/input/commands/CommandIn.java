package input.commands;

import fileio.input.SongInput;

import java.util.ArrayList;

/**
 *      Aceasta clasa este conceputa pentru a citi comenzile
 * */
public class CommandIn {
    private String command;
    private String username;
    private int timestamp;
    private String type;
    private Filters filters;
    private int itemNumber;
    private int seed;
    private int playlistId;
    private String playlistName;
    private int age;
    private String city;
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<SongInput> songs;
    private String date;
    private int price;

    /** Setter  */
    public void setName(String name) {
        this.name = name;
    }
    /** Getter  */
    public String getName() {
        return name;
    }

    /** Setter  */
    public void setDescription(String description) {
        this.description = description;
    }
    /** Getter  */
    public String getDescription() {
        return description;
    }

    /** Setter  */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    /** Getter  */
    public int getReleaseYear() {
        return releaseYear;
    }

    /** Setter  */
    public void setSongs(ArrayList<SongInput> songs) {
        this.songs = songs;
    }
    /** Getter  */
    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    /** Setter  */
    public void setCommand(final String command) {
        this.command = command;
    }
    /** Getter */
    public String getCommand() {
        return command;
    }

    /** Setter  */
    public void setUsername(final String username) {
        this.username = username;
    }
    /** Getter */
    public String getUsername() {
        return username;
    }

    /** Setter  */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }
    /** Getter */
    public int getTimestamp() {
        return timestamp;
    }

    /** Setter  */
    public void setType(final String type) {
        this.type = type;
    }
    /** Getter */
    public String getType() {
        return type;
    }

    /** Setter  */
    public void setFilters(final Filters filters) {
        this.filters = filters;
    }
    /** Getter */
    public Filters getFilters() {
        return filters;
    }

    /** Setter  */
    public void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }
    /** Getter */
    public int getItemNumber() {
        return itemNumber;
    }

    /** Getter */
    public int getSeed() {
        return seed;
    }
    /** Setter */
    public void setSeed(final int seed) {
        this.seed = seed;
    }

    /** Setter  */
    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }
    /** Getter */
    public int getPlaylistId() {
        return playlistId;
    }

    /** Setter  */
    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }
    /** Getter */
    public String getPlaylistName() {
        return playlistName;
    }

    /** Setter */
    public void setAge(final int age) {
        this.age = age;
    }
    /** Getter */
    public int getAge() {
        return age;
    }

    /** Setter */
    public void setCity(final String city) {
        this.city = city;
    }
    /** Getter */
    public String getCity() {
        return city;
    }

    /** Setter */
    public void setDate(final String date) {
        this.date = date;
    }

    /** Getter */
    public String getDate() {
        return date;
    }

    /** Setter */
    public void setPrice(final int price) {
        this.price = price;
    }

    /** Getter */
    public int getPrice() {
        return price;
    }

}
