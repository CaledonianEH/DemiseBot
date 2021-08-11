package xyz.caledonian.listener;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.caledonian.DemiseBot;

import java.awt.*;

public class GuildJoinListener extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public GuildJoinListener(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public void onGuildMemberJoin(GuildMemberJoinEvent e){
        Guild guild = e.getGuild();

        if(guild.getId().equalsIgnoreCase(main.getConfig().getJSONObject("development").getString("discord-id"))){
            TextChannel tc = guild.getTextChannelById(main.getConfig().getJSONObject("channels").getLong("welcome"));

            tc.sendMessageEmbeds(welcomeEmbed(e.getUser(), e.getGuild()).build()).queue();
        }
    }

    @SneakyThrows
    public void onGuildMemberRemove(GuildMemberRemoveEvent e){
        Guild guild = e.getGuild();

        if(guild.getId().equalsIgnoreCase(main.getConfig().getJSONObject("development").getString("discord-id"))){
            TextChannel tc = guild.getTextChannelById(main.getConfig().getJSONObject("channels").getLong("welcome"));

            tc.sendMessageEmbeds(leaveEmbed(e.getUser(), e.getGuild()).build()).queue();
        }
    }

    @SneakyThrows
    private EmbedBuilder welcomeEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(String.format("Welcome, %s to %s", user.getName(), guild.getName()));
        eb.setDescription("Hey, welcome to the guild! You can view all of our commands using slash commands.");
        eb.addField("Total members", String.format("%s including bots", guild.getMembers().size()), true);
        eb.setColor(new Color(61, 216, 143));
        eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }

    @SneakyThrows
    private EmbedBuilder leaveEmbed(User user, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(String.format("Cya, %s, nice knowing you", user.getName()));
        eb.setDescription("Thanks for stopping by!");
        eb.addField("Total members", String.format("%s including bots", guild.getMembers().size()), true);
        eb.setColor(new Color(242, 78, 78));
        eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }
}
