package output.result;

import input.commands.CommandIn;

import java.util.ArrayList;

/**
 *  Aceasta clasa extinde clasa "ResultOut" pentru comanda "search"
 */
public class ResultOutSearch extends ResultOut {
    private ArrayList<String> results = new ArrayList<>();

    public ResultOutSearch(final CommandIn command, final String msg) {
        super(command, msg);
    }

    public ResultOutSearch(final CommandIn command) {
        super(command);
    }

    /** Getter */
    public ArrayList<String> getResults()  {
        return results;
    }

    /** Metoda care adauga in "results" un rezultat al cautarii */
    public void addResult(final String result) {
        results.add(result);
    }

}
