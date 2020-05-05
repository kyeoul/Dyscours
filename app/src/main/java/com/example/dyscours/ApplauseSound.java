package com.example.dyscours;

public class ApplauseSound {
    private int resId;
    private int id;
    private String name;

    private static ApplauseSound[] sounds = new ApplauseSound[] {
            new ApplauseSound(Settings.SOUND_CLAP, R.raw.clapping1, "Default Cheer"),
            new ApplauseSound(Settings.SOUND_PLUMBER, R.raw.plumbercheer, "Plumber Cheer"),
            new ApplauseSound(Settings.SOUND_RANDOM, 0,"Random")
    };

    public static int getResIdFromId(int id){
        if (id == Settings.SOUND_RANDOM){
            return sounds[(int) (Math.random() * (sounds.length - 1))].getResId();
        }
        for (int i = 0; i < sounds.length; i++){
            if (sounds[i].getId() == id){
                return sounds[i].getResId();
            }
        }
        return 0;
    }

    ApplauseSound(int id, int resId, String name){
        this.id = id;
        this.resId = resId;
        this.name = name;
    }

    public String toString(){
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ApplauseSound[] getSounds() {
        return sounds;
    }

    public static void setSounds(ApplauseSound[] sounds) {
        ApplauseSound.sounds = sounds;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
