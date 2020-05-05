package com.example.dyscours;

public class Settings {
    private boolean isColorRed;
    private boolean isApplauseOn;
    private int applauseSound;

    public static final int SOUND_CLAP = 0;
    public static final int SOUND_PLUMBER = 1;
    public static final int SOUND_RANDOM = -1;


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
