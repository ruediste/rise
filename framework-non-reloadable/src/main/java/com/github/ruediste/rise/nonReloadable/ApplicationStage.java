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
    DEVELOPMENT(Stage.DEVELOPMENT, "white", "blue"),

    /**
     * As production like as possible, but don't do any dangerous interactions
     * with the environment (No mass mailing!)
     */
    TESTING(Stage.PRODUCTION, "white", "green"),

    /**
     * Production mode
     */
    PRODUCTION(Stage.PRODUCTION, "white", "red");

    final private Stage saltaStage;

    public String color;
    public String backgroundColor;

    private ApplicationStage(Stage saltaStage, String color,
            String backgroundColor) {
        this.saltaStage = saltaStage;
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    public Stage getSaltaStage() {
        return saltaStage;
    }
}
