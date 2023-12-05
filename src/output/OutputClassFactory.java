package output;

import input.commands.CommandIn;
import output.result.*;

/**
 *          Aceasta clasa contine o singura metoda care va intoarce un obiect pentru ..
 *      .. afisarea rezultatului unei comenzi corespunzator comenzii care se executa
 *
 *          Intrucat toate clasele pomenite mai sus mostenesc clasa "Output", prin aceasta ..
 *      .. clasa finalizam design pattern-ul Factory pentru clasele de rezultate a comenzilor
 * */
public class OutputClassFactory {
    /** Constructor privat, intrucat nu instantiem aceasta clasa nicaieri */
    private OutputClassFactory() {
    }

    public enum UserState {
        NOEXIST,
        OFFLINE
    }

    /** Metoda returneaza o referinta de tipul superclasei extinsa de toate clasele "de output" */
    public static Output getOutput(final CommandIn command, final UserState state) {
        String failMessage;
        switch (state) {
            case NOEXIST -> failMessage = "The username " + command.getUsername() + " doesn't exist.";
            case OFFLINE -> failMessage = command.getUsername() + " is offline.";
            default -> failMessage = null;
        }

        /* Intai se trateaza cazuri speciale de comenzi, intrucat unele au campuri diferite */
        if (command.getCommand().contains("top5")) {
            return new ResultGetTop5(command);
        } else if (command.getCommand().equals("showPlaylists")) {
            return new ResultShowPlaylists(command);
        } else if (command.getCommand().equals("status")) {
            return new ResultStatus(command);
        } else if (command.getCommand().equals("search")) {
            return new ResultOutSearch(command, failMessage);
        }

        /* Majoritatea rezultatelor comenzilor impartasesc aceleasi campuri */
        return new ResultOut(command, failMessage);
    }
}
