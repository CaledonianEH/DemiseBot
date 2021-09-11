package xyz.caledonian.commands.tickets;

import lombok.SneakyThrows;
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
import xyz.caledonian.utils.GuildRoles;
import xyz.caledonian.utils.PremadeEmbeds;
import xyz.caledonian.utils.Utils;

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
                e.getHook().editOriginalEmbeds(PremadeEmbeds.success("Currently closing the application... please wait").build())
                        .setActionRow(Button.danger("ticketCloseBtn", "Deny Application")
                                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                                .withDisabled(true)
                        ).queue();
                e.getTextChannel().delete().queue();
            }else{
                e.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You are not allowed to accept this application. You must be an admin.").build()).setEphemeral(true).queue();
            }
        }else if(e.getComponentId().equals("applicationAcceptBtn")) {
            e.deferEdit().queue();
            if(e.getMember().getRoles().contains(GuildRoles.support())){
                e.deferReply().queue();
                e.getTextChannel().getManager().setName(String.format("accepted-%s", e.getUser().getName())).queue();
                e.getHook().editOriginalEmbeds(PremadeEmbeds.success("This application has been accepted. Do not close this application.").build())
                        .setActionRow(
                                Button.success("applicationAcceptBtn", "Application Accepted")
                                        .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("create"))).withDisabled(true)
                                , Button.danger("applicationDenyBtn", "Delete Application")
                                        .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close"))).withDisabled(false)
                        ).queue();
            }else{
                e.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You are not allowed to accept this application. You must be an admin.").build()).setEphemeral(true).queue();
            }
        }else if(e.getComponentId().equals("applicationCreateBtn")){
            e.deferReply().setEphemeral(true).queue();
            ticket.createUserApplicationButton(e.getUser(), e, e.getGuild());
            Utils.sendConsoleLog("[TICKET] Created ticket for %s", e.getUser().getAsTag());
        }
    }
}
