package no.steria.skuldsku.example.basicservlet;

import java.util.ArrayList;
import java.util.List;

public class MemoryPlaceDao implements PlaceDao {
    private static List<String> places = new ArrayList<>();

    private static void add(String name) {
        synchronized (places) {
            places.add(name);
        }
    }

    private static List<String> all() {
        synchronized (places) {
            return new ArrayList<>(places);
        }
    }

    @Override
    public void addPlace(String name) {
        MemoryPlaceDao.add(name);
    }

    @Override
    public List<String> findMatches(String part) {
        if (part == null) {
            return all();
        }
        ArrayList<String> result = new ArrayList<>();
        for (String place : all()) {
            if (place.toUpperCase().contains(part.toUpperCase())) {
                result.add(place);
            }
        }
        return result;
    }
}
