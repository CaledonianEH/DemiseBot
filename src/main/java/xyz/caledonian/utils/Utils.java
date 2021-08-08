package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.caledonian.DemiseBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    private static DemiseBot main;
    private static JDA jda;

    public Utils(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public static boolean isDeveloper(User user){
        JSONObject jsonObject = main.getConfig().getJSONObject("development").getJSONObject("developers");
        Iterator keys = jsonObject.keys();

        while(keys.hasNext()){
            Object key = keys.next();
            JSONObject value = jsonObject.getJSONObject((String) key);

            if(user.getId().equals(value)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    @SneakyThrows
    public static List<String> developers(){
        for(Object jsonArray : main.getConfig().getJSONObject("development")
                .getJSONArray("developers")){
            ArrayList<String> devs = new ArrayList<>();

            devs.add(jsonArray.toString());
            List<String> finalDevs = devs;

            return finalDevs;
        }
        return null;
    }
}
