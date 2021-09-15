package xyz.caledonian.commands.staff;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.Formatting;
import xyz.caledonian.utils.PremadeEmbeds;
import xyz.caledonian.utils.Utils;

import java.awt.*;
import java.util.HashMap;

public class EmbedCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public EmbedCommand(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }


    MessageChannel mc;
    TextChannel tc;

    String message;
    boolean ping;

    HashMap<User, TextChannel> userTextChannelMap = new HashMap<User, TextChannel>();
    HashMap<User, String> userMessageMap = new HashMap<User, String>();
    HashMap<User, Boolean> userPingMap = new HashMap<User, Boolean>();


    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getGuild() == null){
            return;
        }

        try{
            switch (e.getName()){
                case "embed":
                    //e.deferReply().queue();
                    User user = e.getUser();
                    Guild guild = e.getGuild();
                    Member member = guild.getMember(user);
                    MessageChannel mc = e.getOption("channel").getAsMessageChannel();
                    TextChannel tc = jda.getTextChannelById(mc.getId());

                    String message = e.getOption("message").getAsString();
                    boolean ping = e.getOption("ping").getAsBoolean();

                    if(!member.hasPermission(Permission.MESSAGE_MENTION_EVERYONE)){
                        e.replyEmbeds(PremadeEmbeds.warning("You do not have enough permissions!").build()).setEphemeral(true).queue();
                        return;
                    }

                    e.replyEmbeds(PremadeEmbeds.success(String.format("Your embed is almost ready! Please choose what type of announcement it should be.", message))
                            .build())
                            .addActionRow(SelectionMenu.create("embedCmdSel")
                                    .addOption("Announcement", "embedCmdAnn", Emoji.fromMarkdown("<a:blobDance:807671473060839475>"))
                                    .addOption("Update", "embedCmdUpd", Emoji.fromMarkdown("<a:blobDance:807671473060839475>"))
                                    .addOption("Information", "embedCmdInf", Emoji.fromMarkdown("<a:blobDance:807671473060839475>"))
                                    .setPlaceholder("Choose your announcement type")
                            .build()).setEphemeral(true).queue();

                    // Channel
                    userTextChannelMap.put(user, tc);
                    userMessageMap.put(user, message);
                    userPingMap.put(user, ping);

                    try{
                        //sendReport(tc, e.getUser(), player, reason, replay);
                    }catch (Exception ex){
                        e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).setEphemeral(true).queue();
                        //e.getChannel().sendMessageEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
                    }
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).setEphemeral(true).queue();
            DevMessageLogger.sendErrorLog(e.getGuild(), ex.getMessage());
        }
    }

    @Override
    public void onSelectionMenu(SelectionMenuEvent e){
        User user = e.getUser();

        if(!(userTextChannelMap.containsKey(user)
        && userMessageMap.containsKey(user)
        && userPingMap.containsKey(user))){
            return;
        }

        TextChannel tc = userTextChannelMap.get(user);
        String message = userMessageMap.get(user);
        boolean ping = userPingMap.get(user);
        if(e.getValues().get(0).equals("embedCmdAnn")){
            if(ping){tc.sendMessage("@here").queue();}
            sendAnnouncement(tc, message, user);
        }else if(e.getValues().get(0).equals("embedCmdUpd")){
            if(ping){tc.sendMessage("@here").queue();}
            sendUpdate(tc, message, user);
        }else if(e.getValues().get(0).equals("embedCmdInf")){
            if(ping){tc.sendMessage("@here").queue();}
            sendInfo(tc, message, user);
        }

        e.replyEmbeds(PremadeEmbeds.success(String.format("Hey, %s. Your embed was successfully sent in %s\n\nSent on %s"
                , user.getAsMention(), userMessageMap.get(user), Formatting.getTimeFormat())).build()).queue();
        eraseUserData(user);
        Utils.sendConsoleLog("Sending embed [%s] in %s by %s. ping=%s", message, tc.getName(), e.getUser(), ping);
    }

    @SneakyThrows
    private void sendAnnouncement(TextChannel tc, String message, User user){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("New announcement!");
        eb.setColor(new Color(255, 64, 64));
        eb.setDescription(message);
        //eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), user.getAvatarUrl());

        tc.sendMessage(String.format("Sent by %s at %s", user.getAsMention(), Formatting.getTimeFormat())).setEmbeds(
                eb.build()
        ).queue();
    }

    @SneakyThrows
    private void sendUpdate(TextChannel tc, String message, User user){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("New update!");
        eb.setColor(new Color(64, 140, 255));
        eb.setDescription(message);
        //eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), user.getAvatarUrl());

        tc.sendMessage(String.format("Sent by %s at %s", user.getAsMention(), Formatting.getTimeFormat())).setEmbeds(
                eb.build()
        ).queue();
    }

    @SneakyThrows
    private void sendInfo(TextChannel tc, String message, User user){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Guild Information");
        eb.setColor(new Color(107, 21, 229));
        eb.setDescription(message);
        //eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), user.getAvatarUrl());

        tc.sendMessageEmbeds(eb.build()).queue();
    }

    private void eraseUserData(User user){
        try{
            userTextChannelMap.remove(user);
            userMessageMap.remove(user);
            userPingMap.remove(user);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
