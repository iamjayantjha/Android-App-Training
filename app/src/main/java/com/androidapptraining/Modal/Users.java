package com.androidapptraining.Modal;

public class Users {
    String email, name, password, id, image_url, bio, phone, userName;

    public Users(String email, String name, String password, String id, String image_url, String bio, String phone, String userName) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.id = id;
        this.image_url = image_url;
        this.bio = bio;
        this.phone = phone;
        this.userName = userName;
    }

    public Users() {
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
