package com.kuznets.danceCenter.exceptions;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Long id) { super("Song with id \""+id+"\" not found!"); }

}