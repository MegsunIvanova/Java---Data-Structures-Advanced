package core;

import models.Route;

import java.util.*;
import java.util.stream.Collectors;

public class MoovItImpl implements MoovIt {

    private final Map<String, Route> routesById;
    private final Set<Route> routes;

    public MoovItImpl() {
        this.routesById = new LinkedHashMap<>();
        this.routes = new HashSet<>();
    }

    @Override
    public void addRoute(Route route) {
        if (this.routes.contains(route)) {
            throw new IllegalArgumentException();
        }

        this.routesById.put(route.getId(), route);
        this.routes.add(route);
    }

    @Override
    public void removeRoute(String routeId) {
        Route result = this.routesById.remove(routeId);
        if (result == null) {
            throw new IllegalArgumentException();
        }

        this.routes.remove(result);
    }

    @Override
    public boolean contains(Route route) {
        return this.routes.contains(route);
    }

    @Override
    public int size() {
        return this.routesById.size();
    }

    @Override
    public Route getRoute(String routeId) {
        Route result = this.routesById.get(routeId);
        if (result == null) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public void chooseRoute(String routeId) {
        Route route = this.getRoute(routeId);
        Integer currentPopularity = route.getPopularity();
        route.setPopularity(currentPopularity + 1);
    }

    @Override
    public Iterable<Route> searchRoutes(String startPoint, String endPoint) {
        return this.routesById.values()
                .stream()
                .filter(r -> {
                    List<String> locationPoints = r.getLocationPoints();
                    int startIndex = locationPoints.indexOf(startPoint);
                    int endIndex = locationPoints.indexOf(endPoint);

                    return startIndex > -1 && endIndex > -1 && startIndex < endIndex;
                })
                .sorted((r1, r2) -> {
                    if (r1.getIsFavorite() && !r2.getIsFavorite()) {
                        return -1;
                    }
                    if (r2.getIsFavorite() && !r1.getIsFavorite()) {
                        return 1;
                    }

                    int r1Distance = r1.getLocationPoints().indexOf(endPoint) - r1.getLocationPoints().indexOf(startPoint);
                    int r2Distance = r2.getLocationPoints().indexOf(endPoint) - r2.getLocationPoints().indexOf(startPoint);

                    if (r1Distance != r2Distance) {
                        return r1Distance - r2Distance;
                    }

                    return r2.getPopularity() - r1.getPopularity();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Route> getFavoriteRoutes(String destinationPoint) {
        return this.routesById.values()
                .stream()
                .filter(r -> {
                    int pointIndex = r.getLocationPoints().indexOf(destinationPoint);
                    return r.getIsFavorite() && pointIndex > 0;
                })
                .sorted((r1, r2) -> {
                    if ((!r1.getDistance().equals(r2.getDistance()))) {
                        return Double.compare(r1.getDistance(), r2.getDistance());
                    }

                    return r2.getPopularity() - r1.getPopularity();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Route> getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints() {
        return this.routesById
                .values()
                .stream()
                .sorted(Comparator.comparingInt(Route::getPopularity).reversed()
                        .thenComparingDouble(Route::getDistance)
                        .thenComparingInt(Route::getCountOfLocationPoints))
                .limit(5)
                .collect(Collectors.toList());
    }
}
