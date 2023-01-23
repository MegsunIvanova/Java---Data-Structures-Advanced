package core;

import models.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDatabaseImpl implements MovieDatabase {
    private Map<String, Movie> moviesById;
    private Map<String, LinkedHashSet<Movie>> moviesByActors;

    public MovieDatabaseImpl() {
        this.moviesById = new LinkedHashMap<>();
        this.moviesByActors = new LinkedHashMap<>();
    }

    @Override
    public void addMovie(Movie movie) {
        if (moviesById.containsKey(movie.getId())) {
            throw new IllegalArgumentException();
        }

        this.moviesById.put(movie.getId(), movie);

        for (String actor : movie.getActors()) {
            moviesByActors.putIfAbsent(actor, new LinkedHashSet<>());
            moviesByActors.get(actor).add(movie);
        }

    }

    @Override
    public void removeMovie(String movieId) {
        Movie movie = this.moviesById.remove(movieId);
        if (movie == null) {
            throw new IllegalArgumentException();
        }

        for (String actor : movie.getActors()) {
            moviesByActors.get(actor).remove(movie);
        }
    }

    @Override
    public int size() {
        return moviesById.size();
    }

    @Override
    public boolean contains(Movie movie) {
        return moviesById.containsKey(movie.getId());
    }

    @Override
    public Iterable<Movie> getMoviesByActor(String actorName) {
        LinkedHashSet<Movie> movies = moviesByActors.get(actorName);
        if (movies == null) {
            throw new IllegalArgumentException();
        }

        return movies.stream().sorted(Comparator.comparingDouble(Movie::getRating)
                        .thenComparingInt(Movie::getReleaseYear).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesByActors(List<String> actors) {
        List<Movie> result = moviesById.values()
                .stream()
                .filter(m -> m.getActors().containsAll(actors))
                .sorted(Comparator.comparingDouble(Movie::getRating)
                        .thenComparingInt(Movie::getReleaseYear).reversed())
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public Iterable<Movie> getMoviesByYear(Integer releaseYear) {
        return moviesById.values()
                .stream()
                .filter(m -> m.getReleaseYear() == releaseYear)
                .sorted(Comparator.comparingDouble(Movie::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesInRatingRange(double lowerBound, double upperBound) {
        return moviesById.values()
                .stream()
                .filter(m -> m.getRating() >= lowerBound && m.getRating() <= upperBound)
                .sorted(Comparator.comparingDouble(Movie::getRating).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getAllMoviesOrderedByActorPopularityThenByRatingThenByYear() {

        return moviesById.values()
                .stream()
                .sorted((m1, m2) -> {
                    int m1ActorsTotalMovies = getActorsTotalMovies(m1);
                    int m2ActorsTotalMovies = getActorsTotalMovies(m2);
                    if (m1ActorsTotalMovies != m2ActorsTotalMovies) {
                        return (m2ActorsTotalMovies - m1ActorsTotalMovies);
                    }

                    if (m1.getRating() != m2.getRating()) {
                        return Double.compare(m2.getRating(), m1.getRating());
                    }

                    return Integer.compare(m2.getReleaseYear(), m1.getReleaseYear());
                })
                .collect(Collectors.toList());
    }

    private int getActorsTotalMovies(Movie movie) {
        int result = 0;
        for (String actor : movie.getActors()) {
            result += moviesByActors.get(actor).size();
        }

        return result;

    }
}
