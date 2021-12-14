package io.javabrains.moviecatalogservice.models;

public class Movie {

    private String movieId;
    private String name;

    // prevents exception - RestTemplate uses an empty constructor for creating an object and then sets it's fields values
    // com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `io.javabrains.moviecatalogservice.models.Movie` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
    public Movie() {
    }

    public Movie(String movieId, String name) {
        this.movieId = movieId;
        this.name = name;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
