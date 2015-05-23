package com.github.ruediste.rise.nonReloadable;

import com.github.ruediste.salta.standard.Stage;

/**
 * Stage of the application.
 */
@NonRestartable
public enum ApplicationStage {

	/**
	 * Show full errors
	 */
	DEVELOPMENT(Stage.DEVELOPMENT),

	/**
	 * As production like as possible, but don't do any dangerous interactions
	 * with the environment (No mass mailing!)
	 */
	TESTING(Stage.PRODUCTION),

	/**
	 * Production mode
	 */
	PRODUCTION(Stage.PRODUCTION);

	final private Stage saltaStage;

	private ApplicationStage(Stage saltaStage) {
		this.saltaStage = saltaStage;
	}

	public Stage getSaltaStage() {
		return saltaStage;
	}
}
