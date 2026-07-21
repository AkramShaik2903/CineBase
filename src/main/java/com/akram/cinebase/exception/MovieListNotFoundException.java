package com.akram.cinebase.exception;

public class MovieListNotFoundException extends RuntimeException{
    public MovieListNotFoundException(String message){
        super(message);
    }
}
