package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.Skuldsku;


/**
 * Accessor for methods available only when testing.
 * 
 * @see Skuldsku
 */
public final class SkuldskuAccessor {

    /**
     * @see Skuldsku#reset()
     */
    public static void reset() {
        Skuldsku.reset();
    }
}
