package core;

import models.Doodle;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class DoodleSearchTests {
    private interface InternalTest {
        void execute();
    }

    private DoodleSearch doodleSearch;

    private Doodle getRandomDoodle() {
        return new Doodle(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 2_000),
                ((int) Math.min(1, Math.random() * 2_000_000_000) % 2 == 1),
                Math.min(1, Math.random() * 1000));
    }

    @Before
    public void setup() {
        this.doodleSearch = new DoodleSearchImpl();
    }

    public void performCorrectnessTesting(InternalTest[] methods) {
        Arrays.stream(methods)
                .forEach(method -> {
                    this.doodleSearch = new DoodleSearchImpl();

                    try {
                        method.execute();
                    } catch (IllegalArgumentException ignored) {
                    }
                });

        this.doodleSearch = new DoodleSearchImpl();
    }

    // Correctness Tests

    @Test
    public void testAddDoodle_WithCorrectData_ShouldSuccessfullyAddDoodle() {
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());

        assertEquals(2, this.doodleSearch.size());
    }

    @Test
    public void testContains_WithExistentDoodle_ShouldReturnTrue() {
        Doodle randomDoodle = this.getRandomDoodle();

        this.doodleSearch.addDoodle(randomDoodle);

        assertTrue(this.doodleSearch.contains(randomDoodle));
    }

    @Test
    public void testContains_WithNonexistentDoodle_ShouldReturnFalse() {
        Doodle randomDoodle = this.getRandomDoodle();

        this.doodleSearch.addDoodle(randomDoodle);

        assertFalse(this.doodleSearch.contains(this.getRandomDoodle()));
    }

    @Test
    public void testCount_With5Doodles_ShouldReturn5() {
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());

        assertEquals(5, this.doodleSearch.size());
    }

    @Test
    public void testCount_WithEmpty_ShouldReturnZero() {
        assertEquals(0, this.doodleSearch.size());
    }

    @Test
    public void testSearchDoodles_WithCorrectDoodles_ShouldReturnCorrectlyOrderedData() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        List<Doodle> Doodles = StreamSupport.stream(this.doodleSearch.searchDoodles("sd").spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(5, Doodles.size());
        assertEquals(Doodle5, Doodles.get(0));
        assertEquals(Doodle4, Doodles.get(1));
        assertEquals(Doodle, Doodles.get(2));
        assertEquals(Doodle3, Doodles.get(3));
        assertEquals(Doodle2, Doodles.get(4));
    }

    @Test
    public void testRemove_ShouldRemoveDoodle() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        assertEquals(6, doodleSearch.size());
        assertTrue(doodleSearch.contains(Doodle4));

        doodleSearch.removeDoodle(Doodle4.getId());

        assertEquals(5, doodleSearch.size());
        assertFalse(doodleSearch.contains(Doodle4));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testRemove_ShouldThrowIfWithNoExistingDoodle() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle6);

        doodleSearch.removeDoodle(Doodle5.getId());

    }

    @Test
    public void testGetDoodleShouldReturnCorrectDoodle() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        assertEquals(Doodle5, doodleSearch.getDoodle(Doodle5.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDoodle_ShouldThrowIfWithNoExistingDoodle() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        doodleSearch.getDoodle(Doodle3.getId());
    }

    @Test
    public void testVisitDoodle_ShouldIncreaseDoodleSVisit() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        int expected = Doodle2.getVisits() + 1;

        doodleSearch.visitDoodle(Doodle2.getTitle());

        int actual = doodleSearch.getDoodle(Doodle2.getId()).getVisits();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVisitDoodle_ShouldThrowIfWithNoExistingDoodle() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        doodleSearch.visitDoodle(Doodle2.getTitle());
    }

    @Test
    public void testGetTotalRevenueFromDoodleAds_ShouldReturnCorrectResult() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        double doodle1Rev = Doodle1.getVisits() * Doodle1.getRevenue();
        double doodle4Rev = Doodle4.getVisits() * Doodle4.getRevenue();
        double doodle5Rev = Doodle5.getVisits() * Doodle5.getRevenue();
        double expected = doodle1Rev + doodle4Rev + doodle5Rev;
        double actual = doodleSearch.getTotalRevenueFromDoodleAds();

        assertEquals(expected, actual, 0.00);

        doodleSearch.visitDoodle(Doodle4.getTitle());
        doodle4Rev = Doodle4.getVisits() * Doodle4.getRevenue();
        double expectedAfterVisit = doodle1Rev + doodle4Rev + doodle5Rev;
        double actualAfterVisit = doodleSearch.getTotalRevenueFromDoodleAds();

        assertEquals(expectedAfterVisit, actualAfterVisit, 0.00);

        doodleSearch.removeDoodle(Doodle4.getId());
        double expectedAfterRemoving = doodle1Rev + doodle5Rev;
        double actualAfterRemoving = doodleSearch.getTotalRevenueFromDoodleAds();

        assertEquals(expectedAfterRemoving, actualAfterRemoving, 0.00);
    }

    @Test
    public void testSearchDoodles_ShouldReturnEmptyCollection() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        Iterable<Doodle> actual = doodleSearch.searchDoodles("xxx");

        List<Doodle> list = new ArrayList<>();
        actual.forEach(list::add);

        assertTrue(list.isEmpty());

    }

    @Test
    public void testGetDoodleAds_ShouldReturnCorrectResult() {

        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);
        Doodle Doodle7 = new Doodle("doodle7", "ss", 4000, true, 4.8);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);
        this.doodleSearch.addDoodle(Doodle7);


        Iterable<Doodle> actual = doodleSearch.getDoodleAds();
        List<Doodle> listActual = new ArrayList<>();
        actual.forEach(listActual::add);

        List<Doodle> expected = new ArrayList<>(List.of(Doodle1, Doodle5, Doodle4, Doodle7));

        assertEquals(expected.size(), listActual.size());

        int i = 0;
        for (Doodle doodle : expected) {
            assertEquals(doodle, listActual.get(i++));
        }
    }

    @Test
        public void testGetDoodleAds_ShouldReturnEmptyCollection() {
        Doodle Doodle1 = new Doodle("doodle1", "bbbsd", 4000, false, 5.5);
        Doodle Doodle2 = new Doodle("doodle2", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("doodle3", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("doodle4", "zsd", 4000, false, 4.8);
        Doodle Doodle5 = new Doodle("doodle5", "qsd", 4001, false, 4.8);
        Doodle Doodle6 = new Doodle("doodle6", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle1);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        Iterable<Doodle> actual = doodleSearch.getDoodleAds();

        List<Doodle> list = new ArrayList<>();
        actual.forEach(list::add);

        assertTrue(list.isEmpty());

    }

}