package com.example.javassist;

public abstract class CatSay {

    public CatSay(){
        say();
    }

    private static String voice = "miaomiaomiao";

    public static String getVoice() {
        return voice;
    }

    public static void setVoice(String voice) {
        CatSay.voice = voice;
    }

    protected String say(){
        return "miaomiaomiao";
    }

}
