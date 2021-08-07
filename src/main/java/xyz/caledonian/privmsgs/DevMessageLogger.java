package xyz.caledonian.privmsgs;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.Utils;

import java.awt.*;

public class DevMessageLogger {

    private static DemiseBot main;
    private static JDA jda;

    public DevMessageLogger(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    public static void sendErrorLog(Guild guild, String cause){
        for(String userId : Utils.developers()){
            User dev = jda.getUserById(userId);

            dev.openPrivateChannel().queue((channel) -> {
                channel.sendMessageEmbeds(error(guild, cause).build()).queue();
            });
        }
    }

    public static void sendStartupLog(){
        for(String userId : Utils.developers()){
            User dev = jda.getUserById(userId);

            dev.openPrivateChannel().queue((channel) -> {
                channel.sendMessageEmbeds(startup().build()).queue();
            });
        }
    }

    @SneakyThrows
    private static EmbedBuilder error(Guild guild, String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Error detected!");
        eb.setColor(new Color(242, 78, 78));
        eb.setDescription(String.format("Looks like %s has encountered an unknown error in another discord.\n\n```diff\n- %s\n```",
                jda.getSelfUser().getName(),
                cause));
        eb.addField("Members", String.valueOf(guild.getMembers().size()), true);
        eb.addField("Info", String.format("Name: %s\nID: %s", guild.getName(), guild.getId()), true);
        eb.setThumbnail(guild.getIconUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }

    @SneakyThrows
    private static EmbedBuilder startup(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Bot starting...");
        eb.setColor(new Color(93, 245, 108));
        eb.setDescription(String.format("The bot %s has been marked as booting. You should be able to ignore this message.", jda.getSelfUser().getAsTag()));
        eb.setThumbnail(jda.getSelfUser().getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }
}
