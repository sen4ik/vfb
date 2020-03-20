package com.sen4ik.vfb.enums;

public enum Views {

    confirm("confirm"),
    login("login"),
    error("error"),
    register("register"),
    admin("admin/index"),
    home("index");

    public final String value;

    Views(String value) {
        this.value = value;
    }

    final String value() {
        return value;
    }

}
