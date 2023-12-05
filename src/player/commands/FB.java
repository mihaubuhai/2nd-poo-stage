package player.commands;

import input.commands.CommandIn;

/**
 *      Clasa "ForwardBackward" implementeaza metoda abstracta .
 *      .. "checkValidity" pentru comenzile forward si backward
 * */
public class FB extends FBNP {
    protected final int diff = 90;    // <-- evitare magic number

    /** Metoda verifica aplicabilitatea comenzii raportat la user-ul care a invocat-o */
    public boolean checkValidity(final CommandIn command) {
        /* Se verifica daca s-a selectat ceva sau daca player-ul ruleaza  */
        if (currentPlayer == null || !currentPlayer.getLoadInfo().getLoaded()) {
            setResult(command, "Please load a source before attempting to forward.");
            return false;
        }
        /* Se verifica daca player-ul ruleaza un podcast */
        if (currentPlayer.getLoadInfo().getSelectInfo().getResultType() != 2) {
            setResult(command, "The loaded source is not a podcast.");
            return false;
        }
        return true;
    }
}
