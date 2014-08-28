package no.steria.skuldsku.recorder.httprecorder;

/**
 * Whenever the ServletFilter processes a ServletRequest or a ServletResponse, it will create a ReportObject, and call
 * on a CallReporter to handle the actual recording of the data.
 */
public interface CallReporter {
	
	/**
	 * This method is called once, and should be used to prepare resources for persisting.
	 */
	public void initialize();
	
	/**
	 * Persists the ReportObject (how it is persisted varies with implementation)
	 * @param reportObject The report of the http interactions, the object to be persisted.
	 */
    public void reportCall(ReportObject reportObject);
}
