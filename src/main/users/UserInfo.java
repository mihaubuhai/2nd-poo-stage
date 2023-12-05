package main.users;

import fileio.input.UserInput;

/** Aceasta clasa contine campul comun tuturor user-ilor programului */
public class UserInfo {
    private UserInput userInfo;     //<-- detalii despre user

    /** Getter */
    public String getUsername() {
        return userInfo.getUsername();
    }

    /** Setter */
    public void setUserInfo(final UserInput stats) {
        userInfo = stats;
    }
}
