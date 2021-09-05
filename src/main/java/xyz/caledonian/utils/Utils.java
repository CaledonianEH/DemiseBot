package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.caledonian.DemiseBot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static DemiseBot main;
    private static JDA jda;

    public Utils(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public static boolean isDeveloper(User user){
        return developers().contains(user.getId());
    }

    @SneakyThrows
    public static List<String> developers(){
        ArrayList<String> listdata = new ArrayList<>();
        JSONArray jsonArray = main.getConfig().getJSONObject("development").getJSONArray("developers");
        if(jsonArray != null){
            for (int i=0;i<jsonArray.length();i++){
                listdata.add(jsonArray.getString(i));
            }
        }

        return listdata;
    }

    public static void deleteAfter(Message message, int delay){
        message.delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public static String getLogTime(){
        String timeStamp;
        timeStamp = new SimpleDateFormat("M/d/yy hh:mm:ss:SSSS z").format(new Date());

        return String.format("[%s] ", timeStamp);
    }

    public static void sendConsoleLog(String message, Object... components){
        System.out.printf("%s %s%n", getLogTime(), String.format(message, components));
    }
}
