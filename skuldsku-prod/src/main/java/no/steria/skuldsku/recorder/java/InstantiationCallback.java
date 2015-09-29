package no.steria.skuldsku.recorder.java;

import no.steria.skuldsku.recorder.Skuldsku;

/**
 * Callback for creating an instance.
 * 
 * Using a callback is necessary in order to avoid instantiation when
 * in {@link Skuldsku#isInPlayBackMode() in playback mode}
 * 
 * @param <T> The type of instance that will be created.
 */
public interface InstantiationCallback<T> {

    /**
     * Creates an instance.
     * 
     * @return The instance.
     */
    public T create();
    
}
