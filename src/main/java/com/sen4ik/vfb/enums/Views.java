package com.sen4ik.vfb.enums;

public enum Views {

    error("error"),
    admin("admin"),
    home("home");

    public final String value;

    Views(String value) {
        this.value = value;
    }

    final String value() {
        return value;
    }

}
