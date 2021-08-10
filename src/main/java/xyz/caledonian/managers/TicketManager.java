package xyz.caledonian.managers;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.PremadeEmbeds;

public class TicketManager {

    private DemiseBot main;
    private JDA jda;

    public TicketManager(DemiseBot main, JDA jda) {
        this.main = main;
        this.jda = jda;
    }

    public void createUserTicket(User user, SlashCommandEvent event, Guild guild){
        TextChannel commandChannel = event.getTextChannel();
        String nameFormat = String.format("ticket-%s", user.getAsTag());

        if(guild.getCategoriesByName(getTicketCategoryString(), true).size() > 0){
            commandChannel.sendMessageEmbeds(PremadeEmbeds.warning("The requested ticket channel was not found.").build()).queue();
            return;
        }

        TextChannel ticket = guild.createTextChannel(nameFormat, guild.getCategoriesByName(getTicketCategoryString(), true).get(0)).complete();
        ChannelManager ticketManager = ticket.getManager().putPermissionOverride(guild.getMember(user), 3072L, 8192L)
                .putPermissionOverride(guild.getRolesByName("@everyone", true).get(0), 0L, 1024L);
        if(guild.getRoleById(getSupportRoleString()) != null){
            ticketManager = ticketManager.putPermissionOverride(getSupportRole(), 3072L, 8192L);
        }
        ticketManager.queue();

        event.replyEmbeds(PremadeEmbeds.success(String.format("Successfully created your ticket! You can see it in <#%s>",
                ticket.getId())).build()).queue();

        ticket.sendMessage("@here").setEmbeds(PremadeEmbeds.success("Thank you for creating a ticket! Our team should be here as soon as possible.\n\nYou can help us out by informing what you're in need of!").build()).queue();
    }

    @SneakyThrows
    public Category getTicketCategory(){
        return jda.getCategoryById(main.getConfig().getJSONObject("tickets").getLong("category"));
    }
    @SneakyThrows
    public String getTicketCategoryString(){
        return String.valueOf(main.getConfig().getJSONObject("tickets").getLong("category"));
    }

    @SneakyThrows
    public Role getSupportRole(){
        return jda.getRoleById(main.getConfig().getJSONObject("tickets").getLong("support-role-id"));
    }

    @SneakyThrows
    public String getSupportRoleString(){
        return String.valueOf(main.getConfig().getJSONObject("tickets").getLong("support-role-id"));
    }
}
