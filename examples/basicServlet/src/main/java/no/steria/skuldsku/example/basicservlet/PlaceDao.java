package no.steria.skuldsku.example.basicservlet;

import java.util.List;

public interface PlaceDao {
    public void addPlace(String name);
    public List<String> findMatches(String part);
}
