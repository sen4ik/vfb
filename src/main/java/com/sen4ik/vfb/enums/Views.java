package com.sen4ik.vfb.enums;

public enum Views {

    error("error"),
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
