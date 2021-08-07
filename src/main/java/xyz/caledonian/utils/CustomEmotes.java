package xyz.caledonian.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import xyz.caledonian.DemiseBot;

public class CustomEmotes {

    private static DemiseBot main;
    private static JDA jda;

    public CustomEmotes(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }


    @SneakyThrows
    public static Emoji getYes(){
        return Emoji.fromMarkdown(getEmoteFromKey("yes"));
    }

    @SneakyThrows
    public static Emoji getNo(){
        return Emoji.fromMarkdown(getEmoteFromKey("no"));
    }

    // Getters
    @SneakyThrows
    public static String getEmoteFromKey(String key){
        return main.getConfig().getJSONObject("emotes").getString(key);
    }
}
