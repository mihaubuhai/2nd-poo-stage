package player;

import input.commands.CommandIn;
import main.users.NormalUser;
import output.result.ResultStatus;

/**
 *      Aceasta clasa reprezinta player-ul unui user.
 * */
public class Player {
    private Load loadInfo;           // <-- Ajutator pentru rularea player-ului
    private Stats stats;
    // ^-- Statisicile player-ului care vor fi scrise la output-ul comenzii "status"
    private int lastLoadTime;     // <-- Tine cont de timpul ultimei comenzi

    public Player(final Load info, final int currentTime) {
        setLoadInfo(info);
        /*
            Cream prematur clasa pentru comanda "status" ..
            ..pentru facilitarea calculului campului "remainedTime".
        */
        setStats(info);
        setLastLoadTime(currentTime);
    }

    /** Setter */
    public void setLastLoadTime(final int currentTime) {
        lastLoadTime = currentTime;
    }

    /** Setter */
    public void setLoadInfo(final Load info) {
        loadInfo = info;
    }

    /** Getter */
    public Load getLoadInfo() {
        return loadInfo;
    }

    /** Setter */
    public void setStats(final Load load) {
        stats = new Stats();
        stats.setFields(load);
    }

    /** Getter */
    public Stats getStats() {
        return stats;
    }

    /**
     *      Metoda elimina statisticile unui player atunci cand ..
     *      .. s-a ajuns la finalul a ceea ce se rula
     * */
    public void removeStats() {
        stats = new Stats();
        stats.setPaused(true);
    }

    /** Metoda doar actualizeaza timpul ramas al player-ului */
    public int findRemainedTime(final int currentTime) {
        return stats.getRemainedTime() - (currentTime - lastLoadTime);
    }

}
