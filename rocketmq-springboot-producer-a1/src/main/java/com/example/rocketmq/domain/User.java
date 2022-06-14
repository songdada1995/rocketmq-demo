package com.example.rocketmq.domain;

public class User {
    private String userName;
    private Byte userAge;
    private String msg;

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Byte getUserAge() {
        return userAge;
    }

    public User setUserAge(Byte userAge) {
        this.userAge = userAge;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public User setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userAge=" + userAge +
                ", msg='" + msg + '\'' +
                '}';
    }
}