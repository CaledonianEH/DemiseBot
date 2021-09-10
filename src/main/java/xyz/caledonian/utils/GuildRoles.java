package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import xyz.caledonian.DemiseBot;

public class GuildRoles {

    static DemiseBot main;
    static JDA jda;
    public GuildRoles(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public static Role support(){
        Guild guild = jda.getGuildById(main.getConfig().getJSONObject("development").getString("discord-id"));
        return guild.getRoleById(main.getConfig().getJSONObject("tickets").getLong("application-role-id"));
    }
}
