package xyz.caledonian.commands.tickets;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.managers.TicketManager;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.PremadeEmbeds;

import java.util.concurrent.TimeUnit;

public class TicketCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;
    private TicketManager ticket;

    public TicketCommand(DemiseBot main, JDA jda, TicketManager ticket){
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
                case "ticket":
                    //e.deferReply().queue();
                    User user = e.getUser();
                    Guild guild = e.getGuild();
                    Member member = guild.getMember(user);

                    ticket.createUserTicket(user, e, guild);
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
        if(e.getComponentId().equalsIgnoreCase("ticketCloseBtn")){
            e.deferEdit().queue();
            e.getHook().editOriginalEmbeds(PremadeEmbeds.success("Currently closing the ticket... please wait").build())
                    .setActionRow(Button.danger("ticketCloseBtn", "Close ticket")
                            .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                            .withDisabled(true)
                    ).queue();
            e.getTextChannel().delete().queue();
        }
    }
}
