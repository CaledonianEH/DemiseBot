package xyz.caledonian.utils;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.privmsgs.DevMessageLogger;

import java.awt.*;

public class PremadeEmbeds {

    private static DemiseBot main;
    private static JDA jda;

    public PremadeEmbeds(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public static EmbedBuilder error(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Unexpected error!");
        eb.setColor(new Color(242, 78, 78));
        eb.setDescription(String.format("Looks like %s has encountered an unknown error. Please report this to the developers in our [main discord](%s)\n\n```diff\n- %s\n```", jda.getSelfUser().getName(),
                main.getConfig().getString("support-discord"),
                cause));
        eb.setThumbnail("https://i.imgur.com/LzRqVIy.png");
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder warning(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Wait a minute!");
        eb.setColor(new Color(242, 78, 78));
        eb.setDescription(String.format("Hey, wait a minute! Are you using this thing right? Get support in our [main discord](%s)\n\n```diff\n- %s\n```",
                main.getConfig().getString("support-discord"),
                cause));
        eb.setThumbnail("https://i.imgur.com/LzRqVIy.png");
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder success(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Success!");
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("%s",
                cause));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder success(String mesage, String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Success!");
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("%s\n\n```diff\n+ %s\n```",
                mesage, cause));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }
}
