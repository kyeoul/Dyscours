package com.example.dyscours;

/**
 * This is a data structure that holds information about an applause sound.
 */
public class ApplauseSound {
    private int resId;
    private int id;
    private String name;

    /**
     * This is an array that holds all of the various sounds used in the app
     */
    private static ApplauseSound[] sounds = new ApplauseSound[] {
            new ApplauseSound(Settings.SOUND_CLAP, R.raw.clapping1, "Default Cheer"),
            new ApplauseSound(Settings.SOUND_PLUMBER, R.raw.plumbercheer, "Plumber Cheer"),
            new ApplauseSound(Settings.SOUND_RANDOM, 0,"Random")
    };

    /**
     * Determines the res id of an ApplauseSound from its id
     * @param id The id of the ApplauseSound
     * @return The res id of the resource that corresponds to the applause sound
     */
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

    /**
     * Constructor
     * @param id The id of the Applause sound, standard across all devices in the Dyscours network
     * @param resId The res id of the sound corresponding to the Applause sound
     * @param name The name of the Applause sound, as displayed to the user
     */
    ApplauseSound(int id, int resId, String name){
        this.id = id;
        this.resId = resId;
        this.name = name;
    }

    /**
     * Gives the name of the Applause Sound
     * @return
     */
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
