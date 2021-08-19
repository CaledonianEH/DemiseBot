package xyz.caledonian.commands.user;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.Formatting;
import xyz.caledonian.utils.PremadeEmbeds;

import java.awt.*;

public class MapHeightCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public MapHeightCommand(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getGuild() == null){
            return;
        }

        try{
            switch (e.getName()){
                case "mapheight":
                    //e.deferReply().queue();
                    TextChannel tc = jda.getTextChannelById(main.getConfig().getJSONObject("channels").getLong("reports"));
                    String player = e.getOption("player").getAsString();
                    String reason = e.getOption("reason").getAsString();
                    String replay = null;
                    try{
                        replay = e.getOption("replay-id").getAsString();
                    }catch (Exception ex){
                        replay = "Unspecified";
                    }

                    e.replyEmbeds(PremadeEmbeds.success(String.format("Successfully reported that player, and staff have been alerted!"))
                            .build()).queue();

                    // Channel
                    try{
                        sendReport(tc, e.getUser(), player, reason, replay);
                    }catch (Exception ex){
                        e.getChannel().sendMessageEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
                    }
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
        }
    }

    @SneakyThrows
    private void sendReport(TextChannel tc, User user, String player, String reason, String replay){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("New guild member report!");
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("%s\n\n%s", Formatting.getTimeFormat(), reason));
        eb.addField("Member reported", player, true);
        eb.addField("Reporter", user.getAsMention(), true);
        eb.addField("Replay", replay, false);
        eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        tc.sendTyping().queue();
        tc.sendMessageEmbeds(eb.build()).queue();
    }
}
