package com.example.presentator.model.entities;

import java.util.Date;
import java.util.Objects;

public class User {
    private String name;
    private String nick;
    private Gender gender;
    private String imageURL;
    private Date birthday;

    public User(String name, String nick, Gender gender, String imageURL) {
        this.name = name;
        this.nick = nick;
        this.gender = gender;
        this.imageURL = imageURL;
    }

    public User(String name, String nick, Gender gender) {
        this.name = name;
        this.nick = nick;
        this.gender = gender;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(nick, user.nick) &&
                gender == user.gender &&
                Objects.equals(imageURL, user.imageURL) &&
                Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nick, gender, imageURL);
    }

    public enum Gender {MALE, FEMALE}
}
