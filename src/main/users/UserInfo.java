package main.users;

import fileio.input.UserInput;
import input.commands.CommandIn;
import output.OutputClassFactory;
import output.result.ResultOut;

/** Aceasta clasa contine campul comun tuturor user-ilor programului */
public class UserInfo {
    private UserInput userInfo;         // <-- detalii despre user
    protected UserType userType;    // <-- tipul user-ului

    protected enum UserType {
        NORMALUSER,
        ARTIST,
        HOST
    }

    /** Getter */
    public String getUsername() {
        return userInfo.getUsername();
    }

    /** Getter */
    public UserType getUserType() {
        return userType;
    }

    /** Setter */
    public void setUserInfo(final UserInput stats) {
        userInfo = stats;
    }

    /** Aceasta metoda verifica daca referinta refera catre un user normal */
    public boolean isNormalUser() {
        return userType == UserType.NORMALUSER;
    }

    /** Aceasta metoda verifica daca referinta refera catre un artist */
    public boolean isArtist() {
        return userType == UserType.ARTIST;
    }

    /** Aceasta metoda verifica daca referinta refera catre un host */
    public boolean isHost() {
        return userType == UserType.HOST;
    }

    /**
     *      Aceasta metoda implementeaza comanda "switchConnectionStatus"
     *      Aceasta este apelata de user-ul care a invocat comanda.
     */
    public ResultOut changeConnectionStatus(CommandIn command) {
        ResultOut result = new ResultOut(command);

        /*
            Cazul cu "user does not exist" este acoperit de
            metoda "analyzeUser" din clasa "AnalyzeCommands"
        */
        if (!isNormalUser()) {
            result.setMessage(getUsername() + " is not a normal user.");
        } else {
            /* User-ul este unul normal, efectuam schimbarea de stare */
            ((NormalUser) this).changeState();
            result.setMessage(getUsername() + " has changed status successfully.");
        }

        return result;
    }
}
