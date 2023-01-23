package core;

import models.Doodle;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoodleSearchImpl implements DoodleSearch {
    private Map<String, Doodle> doodlesById;
    private Map<String, Doodle> doodlesByTitles;
    private Map<String, Doodle> addsDoodlesById;

    private double totalRevenueFromDoodleAds;

    public DoodleSearchImpl() {
        this.doodlesById = new LinkedHashMap<>();
        this.doodlesByTitles = new LinkedHashMap<>();
        this.addsDoodlesById = new LinkedHashMap<>();

    }

    @Override
    public void addDoodle(Doodle doodle) {
        this.doodlesById.put(doodle.getId(), doodle);
        this.doodlesByTitles.put(doodle.getTitle(), doodle);

        if (doodle.getIsAd()) {
            addsDoodlesById.put(doodle.getId(), doodle);
        }
    }

    @Override
    public void removeDoodle(String doodleId) {
        Doodle doodle = this.doodlesById.remove(doodleId);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }

        this.doodlesByTitles.remove(doodle.getTitle());

        if (doodle.getIsAd()) {
            this.addsDoodlesById.remove(doodle.getId());
        }
    }

    @Override
    public int size() {
        return this.doodlesById.size();
    }

    @Override
    public boolean contains(Doodle doodle) {
        return this.doodlesById.containsKey(doodle.getId());
    }

    @Override
    public Doodle getDoodle(String id) {
        Doodle doodle = this.doodlesById.get(id);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }

        return doodle;
    }

    @Override
    public double getTotalRevenueFromDoodleAds() {
        return this.addsDoodlesById.values()
                .stream()
                .mapToDouble(doodle -> doodle.getRevenue() * doodle.getVisits())
                .sum();
    }

    @Override
    public void visitDoodle(String title) {
        Doodle doodle = doodlesByTitles.get(title);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }

        doodle.setVisits(doodle.getVisits() + 1);
    }

    @Override
    public Iterable<Doodle> searchDoodles(String searchQuery) {
        return doodlesByTitles
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(searchQuery))
                .map(Map.Entry::getValue)
                .sorted((d1, d2) -> {
                    int result = Boolean.compare(d2.getIsAd(), d1.getIsAd());
                    if (result == 0) {
                        int d1Relevance = d1.getTitle().indexOf(searchQuery);
                        int d2Relevance = d2.getTitle().indexOf(searchQuery);
                        result = Integer.compare(d1Relevance, d2Relevance);
                    }
                    if (result == 0) {
                        result = Integer.compare(d2.getVisits(), d1.getVisits());
                    }

                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getDoodleAds() {
        return this.addsDoodlesById.values()
                .stream()
                .sorted(Comparator.comparingDouble(Doodle::getRevenue)
                        .thenComparingInt(Doodle::getVisits).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getTop3DoodlesByRevenueThenByVisits() {
        return this.doodlesById.values().stream()
                .sorted(Comparator.comparingDouble(Doodle::getRevenue)
                        .thenComparingInt(Doodle::getVisits).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}
