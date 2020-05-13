package com.example.dyscours;

/**
 * A data structure used to describe the settings for a given Dyscours user
 */
public class Settings {
    private boolean isColorRed; // Is color of the user's own text bubbles red? True: red, false: blue.
    private boolean isApplauseOn; // Is applause on? That is, can you hear applause
    private int applauseSound; // Which applause sound is being used

    public static final int SOUND_CLAP = 0;
    public static final int SOUND_PLUMBER = 1;
    public static final int SOUND_RANDOM = -1; // randomly switches between the other applause sounds for each separate debate

    public Settings(boolean isColorRed, boolean isApplauseOn, int applauseSound){
        this.isColorRed = isColorRed;
        this.isApplauseOn = isApplauseOn;
        this.applauseSound = applauseSound;
    }

    public Settings(){
        isColorRed = true;
        isApplauseOn = true;
        applauseSound = SOUND_CLAP;
    }

    public boolean isColorRed() {
        return isColorRed;
    }

    public void setColorRed(boolean colorRed) {
        isColorRed = colorRed;
    }

    public boolean isApplauseOn() {
        return isApplauseOn;
    }

    public void setApplauseOn(boolean applauseOn) {
        isApplauseOn = applauseOn;
    }

    public int getApplauseSound() {
        return applauseSound;
    }

    public void setApplauseSound(int applauseSound) {
        this.applauseSound = applauseSound;
    }
}
