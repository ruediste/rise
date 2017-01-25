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
    DEVELOPMENT("dev", Stage.DEVELOPMENT, "white", "blue"),

    /**
     * As production like as possible, but don't do any dangerous interactions
     * with the environment (No mass mailing!)
     */
    TESTING("test", Stage.PRODUCTION, "white", "green"),

    /**
     * Production mode
     */
    PRODUCTION("prod", Stage.PRODUCTION, "white", "red");

    final private Stage saltaStage;

    /**
     * Foreground color to identify the stage. Can be modified form anywhere.
     */
    public String color;

    /**
     * Background color to identify the stage. Can be modified form anywhere.
     */
    public String backgroundColor;

    public String shortName;

    private ApplicationStage(String shortName, Stage saltaStage, String color, String backgroundColor) {
        this.shortName = shortName;
        this.saltaStage = saltaStage;
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    public Stage getSaltaStage() {
        return saltaStage;
    }
}
