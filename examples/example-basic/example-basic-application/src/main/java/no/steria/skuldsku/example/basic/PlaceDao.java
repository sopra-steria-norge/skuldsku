package no.steria.skuldsku.example.basic;

import java.util.List;

/**
 * Example DAO. Added to the example in order to explain
 * how Java interface recording works.
 * 
 * @see PlaceServlet
 */
public interface PlaceDao {
    public void addPlace(String name);
    public List<String> findMatches(String part);
}
