package core;

import models.Track;

import java.util.*;
import java.util.stream.Collectors;

public class RePlayerImpl implements RePlayer {
    //ID -> Track
    private final Map<String, Track> tracksByIDs;
    //Album -> Map <Title -> Track>
    private final Map<String, Map<String, Track>> albumsWithTracksByTitles;
    //Artist -> Map <Album, List <Track>>
    private final Map<String, Map<String, List<Track>>> artistsWithTracksByAlbums;
    //Album -> Set<Track
    private Map<String, Set<Track>> sortedTracks;
    //Duration -> Set<Track
    private TreeMap<Integer, Set<Track>> tracksByDuration;
    private Deque<Track> listeningQueue;


    public RePlayerImpl() {
        this.tracksByIDs = new HashMap<>();
        this.albumsWithTracksByTitles = new HashMap<>();
        this.artistsWithTracksByAlbums = new HashMap<>();
        this.listeningQueue = new ArrayDeque<>();
        this.sortedTracks = new TreeMap<>();
        this.tracksByDuration = new TreeMap<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        this.tracksByIDs.put(track.getId(), track);

        addToIndices(track, album);
    }

    private void addToIndices(Track track, String album) {
        this.albumsWithTracksByTitles
                .computeIfAbsent(album, k -> new HashMap<>())
                .put(track.getTitle(), track);

        this.artistsWithTracksByAlbums
                .computeIfAbsent(track.getArtist(), k -> new HashMap<>())
                .computeIfAbsent(album, k -> new ArrayList<>())
                .add(track);

        this.sortedTracks
                .computeIfAbsent(album, k -> new TreeSet<Track>(this.trackComparatorByDurationRevThanPlaysRev()))
                .add(track);

        this.tracksByDuration
                .computeIfAbsent(track.getDurationInSeconds(), k -> new TreeSet<>(this.trackComparatorByPlaysRev()))
                .add(track);
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        Map<String, Track> albumTracksByTitle = this.albumsWithTracksByTitles.get(albumName);
        if (albumTracksByTitle == null) {
            throw new IllegalArgumentException();
        }

        Track track = albumTracksByTitle.remove(trackTitle);
        if (track == null) {
            throw new IllegalArgumentException();
        }

        this.tracksByIDs.remove(track.getId());
        this.listeningQueue.remove(track);

        Map<String, List<Track>> artistTracks = this.artistsWithTracksByAlbums.get(track.getArtist());
        artistTracks.get(albumName).remove(track);

        this.sortedTracks.get(albumName).remove(track);

        this.tracksByDuration.get(track.getDurationInSeconds()).remove(track);
    }

    @Override
    public boolean contains(Track track) {
        return tracksByIDs.containsKey(track.getId());
    }

    @Override
    public int size() {
        return this.tracksByIDs.size();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        Map<String, Track> albumWithTracks = albumsWithTracksByTitles.get(albumName);
        if (albumWithTracks == null) {
            throw new IllegalArgumentException();
        }

        Track track = albumWithTracks.get(title);
        if (track == null) {
            throw new IllegalArgumentException();
        }

        return track;
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        Map<String, Track> albumTracksByTitle = this.albumsWithTracksByTitles.get(albumName);
        if (albumTracksByTitle == null) {
            throw new IllegalArgumentException();
        }

        List<Track> result = albumTracksByTitle.values()
                .stream()
                .sorted(Comparator.comparingInt(Track::getPlays).reversed())
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        Track track = getTrack(trackName, albumName);
        this.listeningQueue.add(track);
    }

    @Override
    public Track play() {
        Track track = listeningQueue.poll();
        if (track == null) {
            throw new IllegalArgumentException();
        }

        track.setPlays(track.getPlays() + 1);

        return track;
    }

    @Override
    public Iterable<Track> getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(int lowerBound,
                                                                                          int upperBound) {
        NavigableMap<Integer, Set<Track>> result = tracksByDuration.subMap(lowerBound, true, upperBound, true);

        if (lowerBound > upperBound && tracksByDuration.isEmpty()) {
            return Collections.emptyList();
        }

        return result.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Track> getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending() {
        return sortedTracks.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Track>> getDiscography(String artistName) {
        Map<String, List<Track>> result = new LinkedHashMap<>();

        Map<String, List<Track>> albumsWithTracks = artistsWithTracksByAlbums.get(artistName);
        if (albumsWithTracks == null) {
            throw new IllegalArgumentException();
        }

        for (Map.Entry<String, List<Track>> entry : albumsWithTracks.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    private Comparator<Track> trackComparatorByDurationRevThanPlaysRev() {
        return (t1, t2) -> {
            int result = Integer.compare(t2.getPlays(), t1.getPlays());

            if (result == 0) {
                result = Integer.compare(t2.getDurationInSeconds(), t1.getDurationInSeconds());
            }

            return result;
        };
    }

    private Comparator<Track> trackComparatorByPlaysRev() {
        return (t1, t2) -> Integer.compare(t2.getPlays(), t1.getPlays());
    }
}
