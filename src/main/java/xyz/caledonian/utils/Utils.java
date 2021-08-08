package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.caledonian.DemiseBot;

import java.lang.reflect.Array;
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
        for(String id : developers()){
            return user.getId().equals(id);
        }
        return false;
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
}
