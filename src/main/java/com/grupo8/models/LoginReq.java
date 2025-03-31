package com.grupo8.models;

public class LoginReq {
    private String nickname;
    private String pass;

    public LoginReq() {

    }
    public LoginReq(String nickname, String pass) {
        this.nickname = nickname;
        this.pass = pass;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    
}
