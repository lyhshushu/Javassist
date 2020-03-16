package com.example.javassist;

public class DogSay {

    public DogSay(){
        say();
    }
    public static String getVoice() {
        return voice;
    }

    public static void setVoice(String voice) {
        DogSay.voice = voice;
    }

    private static String voice = "wangwangwang";

    protected String say(){
        return "wangwangwang";
    }
}
