package com.example.dyscours;

public class Settings {
    private boolean isColorRed;
    private boolean isApplauseOn;

    public static final int SOUND_CLAP = 1;

    public Settings(boolean isColorRed, boolean isApplauseOn){
        this.isColorRed = isColorRed;
        this.isApplauseOn = isApplauseOn;
    }

    public Settings(){
        isColorRed = true;
        isApplauseOn = true;
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
}
