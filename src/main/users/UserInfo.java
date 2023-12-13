package main.users;

import fileio.input.UserInput;
import input.commands.CommandIn;
import output.result.ResultOut;

import java.util.ArrayList;

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
            metoda "analyseUser" din clasa "AnalyseCommands"
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

    /**
            *      Aceasta metoda implementeaza comanda "addUser"
            *      <p>
            *      Returneaza un obiect de tipul rezultatului asteptat pentru comanda "addUser"
            *      @param currUser este variabila "tempReference" din metoda "analyseUser"
     * */
    public static ResultOut addUser(final ArrayList<UserInfo> users, final UserInfo currUser,
                                    final CommandIn cmd) {
        ResultOut result = new ResultOut(cmd);

        /* Verificam daca referinta este nula (caz in care user-ul nu exista) */
        if (currUser == null) {
            UserInput currUserInfo = new UserInput(cmd.getUsername(), cmd.getAge(), cmd.getCity());
            users.add(UserFactory.getUser(cmd.getType(), currUserInfo));
            result.setMessage("The username " + cmd.getUsername() +
                    " has been added successfully.");
        } else {
            /* Daca referinta nu este nula, cu siguranta "currUser" se gaseste in lista "users" */
            result.setMessage("The username " + cmd.getUsername() + " is already taken.");
        }

        return result;
    }

}
