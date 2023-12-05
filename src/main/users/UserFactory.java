package main.users;

import fileio.input.UserInput;

/** Clasa care completeaza design pattern-ul "Factory" ales pentru crearea user-ilor */
public final class UserFactory {
    private UserFactory() {
    }

    /**
     * Metoda care creeaza un user de tipul cerut, ramanand de facut cast
     * la tipul concret acolo unde se apeleaza metoda
     * */
    public static UserInfo getUser(final String type, final UserInput userInfo) {
        switch (type.toLowerCase()) {
            case "user" -> {
                return new NormalUser(userInfo);
            }
//            case "artist" ->
//            case "host" ->
            default -> {
                return null;
            }
        }
    }
}
