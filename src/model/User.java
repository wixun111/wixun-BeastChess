package model;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {
    private final String name;
    private final String account;
    private final String password;
    private int score;

    public User(String name, String account, String password, int score) {
        this.name = name;
        this.account = account;
        this.password = password;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name + " " + account + " " + password + " " + score;
    }

    @Override
    public int compareTo(User user) {
        return user.score - this.score;
    }
}
