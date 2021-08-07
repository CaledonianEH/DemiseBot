package xyz.caledonian.utils;

public class Formatting {

    public static String getTimeFormat(){
        return String.format("<t:%s>", System.currentTimeMillis() / 1000);
    }
}
