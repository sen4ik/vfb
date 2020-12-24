package com.sen4ik.vfb.entities;

public enum Role {

    USER("USER"), ADMIN("ADMIN");

    private final String name;

    Role(String s){
        name = s;
    }

    public String toString(){
        return this.name;
    }
}
