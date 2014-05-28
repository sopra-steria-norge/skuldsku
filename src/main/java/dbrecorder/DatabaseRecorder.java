package dbrecorder;

import java.io.PrintWriter;

/**
 * Service for monitoring database changes.
 */
public interface DatabaseRecorder {

    /**
     * Initializes the recorder. The initialization only need to be performed
     * once on each database unless {@link #tearDown()} is invoked to remove
     * the recorder completely from the database.
     */
    public void setup();
    
    /**
     * Starts recording database changes.
     */
    public void start();
    
    /**
     * Stops recording database changes.
     */
    public void stop();
   
    /**
     * Exports recorded database changes.
     * 
     * @param out The output stream to which the database changes will
     *          be written.
     */
    public void exportTo(final PrintWriter out);
    
    /**
     * Removes the recorder for the database by removing any recording tables,
     * triggers and recorded data.
     */
    public void tearDown();
}