package output;

import input.commands.CommandIn;
import output.result.ResultGetTop5;
import output.result.ResultShowPlaylists;
import output.result.ResultStatus;
import output.result.ResultOutSearch;
import output.result.Output;
import output.result.ResultOut;
/**
 *          <p>
 *          Aceasta clasa contine o singura metoda care va intoarce un obiect pentru
 *       afisarea rezultatului unei comenzi corespunzator comenzii care se executa
 *          </p>
 *          <p>
 *          Intrucat toate clasele pomenite mai sus mostenesc clasa "Output", prin aceasta
 *      clasa finalizam design pattern-ul Factory pentru clasele de rezultate ale comenzilor
 *          </p>
 * */
public final class OutputClassFactory {
    /** Constructor privat, intrucat nu instantiem aceasta clasa nicaieri */
    private OutputClassFactory() {
    }

    public enum UserState {
        NOEXIST,
        OFFLINE
    }

    /** Metoda returneaza o referinta de tipul superclasei extinsa de toate clasele "de output" */
    public static Output getOutput(final CommandIn command, final UserState state) {
        String failMsg;
        switch (state) {
            case NOEXIST -> failMsg = "The username " + command.getUsername() + " doesn't exist.";
            case OFFLINE -> failMsg = command.getUsername() + " is offline.";
            default -> failMsg = null;
        }

        /* Intai se trateaza cazuri speciale de comenzi, intrucat unele au campuri diferite */
        if (command.getCommand().contains("tops")) {
            return new ResultGetTop5(command);
        } else if (command.getCommand().equals("showPlaylists")) {
            return new ResultShowPlaylists(command);
        } else if (command.getCommand().equals("status")) {
            return new ResultStatus(command);
        } else if (command.getCommand().equals("search")) {
            return new ResultOutSearch(command, failMsg);
        }

        /* Majoritatea rezultatelor comenzilor impartasesc aceleasi campuri */
        return new ResultOut(command, failMsg);
    }
}
