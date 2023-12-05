package input.commands;

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
    public void setSeed(int seed) {
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

}
