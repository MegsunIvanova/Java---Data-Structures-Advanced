package core;

import models.Track;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RePlayerTests {
    private RePlayer rePlayer;

    private Track getRandomTrack() {
        return new Track(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 1_000_000_000),
                (int) Math.min(10, Math.random() * 10_000));
    }

    @Before
    public void setup() {
        this.rePlayer = new RePlayerImpl();
    }

    @Test
    public void testAddTrack_WithExistentAlbum_ShouldSuccessfullyaddTrack() {
        this.rePlayer.addTrack(this.getRandomTrack(), "randomAlbum");
        this.rePlayer.addTrack(this.getRandomTrack(), "randomAlbum");

        assertEquals(2, this.rePlayer.size());
    }

    @Test
    public void testContains_WithExistentTrack_ShouldReturnTrue() {
        Track randomTrack = this.getRandomTrack();

        this.rePlayer.addTrack(randomTrack, "randomAlbum");

        assertTrue(this.rePlayer.contains(randomTrack));
    }

    @Test
    public void testGetTracksOrderedByMultiCriteria_WithCorrectData_ShouldReturnCorrectResults() {
        Track track = new Track("asd", "bsd", "csd", 4000, 400);
        Track track2 = new Track("dsd", "esd", "fsd", 5000, 400);
        Track track3 = new Track("hsd", "isd", "jsd", 5000, 500);
        Track track4 = new Track("ksd", "lsd", "msd", 5000, 600);
        Track track5 = new Track("nsd", "osd", "psd", 6000, 100);

        this.rePlayer.addTrack(track, "randomAlbum");
        this.rePlayer.addTrack(track2, "bandomAlbum");
        this.rePlayer.addTrack(track3, "aandomAlbum2");
        this.rePlayer.addTrack(track4, "aandomAlbum2");
        this.rePlayer.addTrack(track5, "aandomAlbum2");

        List<Track> list =
                StreamSupport.stream(this.rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending().spliterator(), false)
                        .collect(Collectors.toList());

        assertEquals(5, list.size());
        assertEquals(track5, list.get(0));
        assertEquals(track4, list.get(1));
        assertEquals(track3, list.get(2));
        assertEquals(track2, list.get(3));
        assertEquals(track, list.get(4));
    }

    @Test
    public void testContains_With1000000Results_ShouldPassInstantly() {
        int count = 1000000;

        Track trackToContain = null;

        for (int i = 0; i < count; i++) {
            Track track = new Track(i + "", "Title" + i, "Artist" + i, i * 100, i * 10);

            this.rePlayer.addTrack(track, "randomAlbum");

            if (i == 800000) {
                trackToContain = track;
            }
        }

        long start = System.currentTimeMillis();

        this.rePlayer.contains(trackToContain);

        long stop = System.currentTimeMillis();
        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 1);
    }

    @Test
    public void testRemoveTrack_With1000000ResultsAndQueue_ShouldPassQuickly() {
        int count = 1000000;

        Track actual = null;

        for (int i = count; i >= 0; i--) {
            Track track = new Track(i + "", "Title" + i, "Artist" + i, i * 1000, i * 100);

            String album = null;

            if (i <= 50000) {
                album = "randomAlbum5";
            } else if (i <= 30000) {
                album = "randomAlbum3";
            } else {
                album = "randomAlbum";
            }

            this.rePlayer.addTrack(track, album);

            if (i == 50000) {
                actual = track;
            }

            if (i <= 75000 && i >= 25000) {
                this.rePlayer.addToQueue(track.getTitle(), album);
            }
        }

        long start = System.currentTimeMillis();

        this.rePlayer.removeTrack(actual.getTitle(), "randomAlbum5");

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 5);

        while (true) {
            try {
                assertNotEquals(this.rePlayer.play(), actual);
            } catch (IllegalArgumentException e) {
                break;
            }
        }
    }

    @Test
    public void testGetTrack_ShouldReturnCorrectTrack() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 250);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        Track actual = rePlayer.getTrack(track3.getTitle(), "alternative");

        assertEquals(track3, actual);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetTrack_ShouldThrowIfNoSuchTrackInAlbum() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 250);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        Track actual = rePlayer.getTrack(track3.getTitle(), "rock");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTrack_ShouldThrowIfNoSuchAlbum() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 250);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        Track actual = rePlayer.getTrack(track3.getTitle(), "disco");

    }

    @Test
    public void testGetAlbum_ShouldReturnCorrectResult() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 250);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        List<Track> expected = new ArrayList<>(List.of(track10, track7, track3, track9));
        //5900 (10), 5500(7), 5000(3) , 4800 (9)

        Iterable<Track> alternative = rePlayer.getAlbum("alternative");

        List<Track> actual = new ArrayList<>();
        alternative.forEach(actual::add);

        assertEquals(expected.size(), actual.size());
        int i = 0;
        for (Track track : actual) {
            assertEquals(expected.get(i++), track);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAlbum_ShouldThrowIfNoSuchAlbum() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 250);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        rePlayer.getAlbum("disco");
    }

    @Test
    public void testGetTracksInDurationRangeOrderedByDurationThenByPlaysDescending_ShouldReturnCorrectResult() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 200);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        List<Track> expected = new ArrayList<>(List.of(track6, track8, track7, track9, track2, track1, track10, track3));


        Iterable<Track> tracksByDuration = rePlayer.getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(200, 500);

        List<Track> actual = new ArrayList<>();
        tracksByDuration.forEach(actual::add);

        assertEquals(expected.size(), actual.size());
        int i = 0;
        for (Track track : actual) {
            assertEquals(expected.get(i++), track);
        }
    }

    @Test
    public void testGetTracksInDurationRangeOrderedByDurationThenByPlaysDescending_ShouldReturnEmptyCollection() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 200);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "alternative");

        Iterable<Track> tracksByDuration = rePlayer.getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(2000, 5000);

        List<Track> actual = new ArrayList<>();
        tracksByDuration.forEach(actual::add);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetDiscography_ShouldReturnCorrectResult() {
        Track track1 = new Track("track1", "Ballroom Blitz", "Sweet", 4000, 400);
        Track track2 = new Track("track2", "Black Hole Sun", "Soundgarden", 5000, 400);
        Track track3 = new Track("track3", "Creep", "Radiohead", 5000, 500);
        Track track4 = new Track("track4", "Go with the Flow", "Queens of the Stone Age", 5000, 600);
        Track track5 = new Track("track5", "Highway Star", "Deep Purple", 6000, 100);
        Track track6 = new Track("track6", "Suffragette City", "David Bowie", 7000, 200);
        Track track7 = new Track("track7", "When You Were Young", "The Killers", 5500, 300);
        Track track8 = new Track("track8", "Spoonman", "Soundgarden", 4500, 200);
        Track track9 = new Track("track9", "Paranoid Android", "Radiohead", 4800, 350);
        Track track10 = new Track("track10", "Karma Police", "Radiohead", 5900, 410);

        this.rePlayer.addTrack(track1, "glam");
        this.rePlayer.addTrack(track2, "rock");
        this.rePlayer.addTrack(track3, "alternative");
        this.rePlayer.addTrack(track4, "rock");
        this.rePlayer.addTrack(track5, "rock");
        this.rePlayer.addTrack(track6, "glam");
        this.rePlayer.addTrack(track7, "alternative");
        this.rePlayer.addTrack(track8, "rock");
        this.rePlayer.addTrack(track9, "alternative");
        this.rePlayer.addTrack(track10, "rock");

        Map<String, List<Track>> expected = new HashMap<>();
        expected.put("alternative", new ArrayList<>(List.of(track3, track9)));
        expected.put("rock", new ArrayList<>(List.of(track10)));

        Map<String, List<Track>> actual = rePlayer.getDiscography("Radiohead");

        assertEquals(expected.size(), actual.size());
        expected.entrySet().forEach(expectedEntry -> {
            assertEquals(expectedEntry.getValue().size(), actual.get(expectedEntry.getKey()).size());
            for (Track expectedTrack : expectedEntry.getValue()) {
                assertTrue(actual.get(expectedEntry.getKey()).contains(expectedTrack));
            }
        });
    }

    @Test
    public void testGetTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending_ShouldReturnEmptyCollection() {
        Iterable<Track> actual = rePlayer.getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending();

        List<Track> result = new ArrayList<>();
        actual.forEach(result::add);

        assertTrue(result.isEmpty());

    }

}
