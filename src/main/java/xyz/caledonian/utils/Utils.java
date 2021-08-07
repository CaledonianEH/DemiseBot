package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.caledonian.DemiseBot;

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
        for(Object jsonArray : main.getConfig().getJSONObject("development")
        .getJSONArray("developers")){
            String line = jsonArray.toString();

            return user.getId().equals(line);
        }

        return false;
    }

    @SneakyThrows
    public static List<String> developers(){
        for(Object jsonArray : main.getConfig().getJSONObject("development")
                .getJSONArray("developers")){

            developers().add(jsonArray.toString());
        }

        return null;
    }
}
