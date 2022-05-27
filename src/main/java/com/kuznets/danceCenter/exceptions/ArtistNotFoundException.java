package com.kuznets.danceCenter.exceptions;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(Long id) { super("Artist with id \""+id+"\" not found!"); }

}