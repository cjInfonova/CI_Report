package com.infonova.jenkins;

/**
 * Created by christian.jahrbacher on 28.07.2015.
 */
public class Usersettings {

    private String username;
    private String password;

    public Usersettings(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Usersettings() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
