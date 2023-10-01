package com.example.demo.exception;

import lombok.Getter;

@Getter
public class ErrorDetailEntity {

    private String code;
    private String message;
    private String location;

    public ErrorDetailEntity() { }

    public ErrorDetailEntity(String message) {
        this.message = message;
        this.code = null;
        this.location = null;
    }

    public ErrorDetailEntity(String message, String location) {
        this.message = message;
        this.location = location;
        this.code = null;
    }

    public ErrorDetailEntity(String message, String location, String code) {
        this.message = message;
        this.location = location;
        this.code = code;
    }

}
