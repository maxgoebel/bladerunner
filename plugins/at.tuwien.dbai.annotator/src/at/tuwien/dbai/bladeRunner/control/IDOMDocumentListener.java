package at.tuwien.dbai.bladeRunner.control;

public interface IDOMDocumentListener {

	/*
	 * Used to notify when the DOM document in about to change. Could use
	 * this interface to show progress bar.
	 */
	void newDocumentLoading( int current, int total );
	
	/*
	 * Used to notify that a new DOM document is fully loaded and available.
	 * 
	 * Listener should use the IDOMDocumentContainer interface to get the new
	 * document.
	 */
	void newDocumentLoaded();
}
