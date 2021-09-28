package xyz.caledonian.commands.tickets;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.ChannelManager;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.managers.ApplicationManager;
import xyz.caledonian.managers.TicketManager;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.Formatting;
import xyz.caledonian.utils.GuildRoles;
import xyz.caledonian.utils.PremadeEmbeds;
import xyz.caledonian.utils.Utils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ApplicationCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;
    private ApplicationManager ticket;

    public ApplicationCommand(DemiseBot main, JDA jda, ApplicationManager ticket){
        this.main = main;
        this.jda = jda;
        this.ticket = ticket;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getGuild() == null){
            return;
        }

        try{
            switch (e.getName()){
                case "apply":
                    //e.deferReply().queue();
                    User user = e.getUser();
                    Guild guild = e.getGuild();
                    Member member = guild.getMember(user);

                    ticket.createUserApplication(user, e, guild);
                    Utils.sendConsoleLog("[APPLICATION] Created application for %s", user.getAsTag());
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
            DevMessageLogger.sendErrorLog(e.getGuild(), ex.getMessage());
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onButtonClick(ButtonClickEvent e){
        System.out.println("button press");
        System.out.println(e.getComponentId());
        if(e.getComponentId().equalsIgnoreCase("applicationDenyBtn")){
            e.deferEdit().queue();
            if(e.getMember().getRoles().contains(GuildRoles.support())){
                e.getHook().editOriginalEmbeds(PremadeEmbeds.success(String.format("This application was closed on %s, by %s.\nIt will be automatically deleted in three days.",
                                Formatting.getTimeFormat(), e.getUser())).build())
                        .setActionRow(Button.danger("ticketCloseBtn", "Deny Application")
                                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                                .withDisabled(true),
                                Button.danger("applicationDenyBtn", "Delete Application")
                                        .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                        ).queue();
                String currentName = e.getTextChannel().getName();
                e.getTextChannel().getManager().setName(currentName.replace("application", "denied")).queue();

                e.getChannel().sendMessage(String.format("%s", Formatting.getTimeFormat()))
                        .setEmbeds(accepted(e.getUser()).build()).queue();
                e.getTextChannel().delete().queueAfter(1, TimeUnit.DAYS);
            }else{
                e.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You are not allowed to accept this application. You must be an admin.").build()).setEphemeral(true).queue();
            }
        }else if(e.getComponentId().equals("applicationAcceptBtn")) {
            e.deferEdit().queue();
            if(e.getMember().getRoles().contains(GuildRoles.support())){
                e.deferReply().queue();
                String currentName = e.getTextChannel().getName();
                e.getTextChannel().getManager().setName(currentName.replace("application", "accepted")).queue();
                e.getHook().editOriginalEmbeds(PremadeEmbeds.success("This application has been accepted. Do not close this application.").build())
                        .setActionRow(
                                Button.success("applicationAcceptBtn", "Application Accepted")
                                        .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("create"))).withDisabled(true)
                                , Button.danger("applicationDenyBtn", "Delete Application")
                                        .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close"))).withDisabled(false)
                        ).queue();

                e.getChannel().sendMessage(String.format("%s", Formatting.getTimeFormat()))
                        .setEmbeds(accepted(e.getUser()).build()).queue();
            }else{
                e.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You are not allowed to accept this application. You must be an admin.").build()).setEphemeral(true).queue();
            }
        }else if(e.getComponentId().equals("applicationCreateBtn")){
            e.deferReply().setEphemeral(true).queue();
            ticket.createUserApplicationButton(e.getUser(), e, e.getGuild());
            Utils.sendConsoleLog("[TICKET] Created ticket for %s", e.getUser().getAsTag());
        }
    }

    @SneakyThrows
    private EmbedBuilder accepted(User user){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Application Accepted!");
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("This application has been accepted by %s, on %s. Please confirm you have your messages open.\n\nWe will be contacting you soon for more information." +
                "\nFrom the entire Demise Guild, Congratulations.",
                user.getAsMention(), Formatting.getTimeFormat()));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), user.getAvatarUrl());

        return eb;
    }

    @SneakyThrows
    private EmbedBuilder denied(User user){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Application Denied!");
        eb.setColor(new Color(242, 78, 78));
        eb.setDescription(String.format("This application has been denied by %s, on %s. Please confirm you have your messages open.\n\nYou can re-apply once you meet requirements.",
                user.getAsMention(), Formatting.getTimeFormat()));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), user.getAvatarUrl());

        return eb;
    }
}
