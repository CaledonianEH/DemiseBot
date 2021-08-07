package xyz.caledonian.commands.punishments;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.PremadeEmbeds;

import java.awt.*;
import java.util.HashMap;

public class BanCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public BanCommand(DemiseBot main, JDA jda){
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
                case "ban":
                    //e.deferReply().queue();
                    User user = e.getUser();
                    Guild guild = e.getGuild();
                    Member member = guild.getMember(user);

                    Member player = e.getOption("user").getAsMember();
                    String reason = null;
                    try{
                        reason = e.getOption("reason").getAsString();
                    }catch (Exception ex){
                        reason = "Unspecified";
                    }

                    if(!member.hasPermission(Permission.BAN_MEMBERS)){
                        e.replyEmbeds(PremadeEmbeds.warning("You do not have enough permissions!").build()).setEphemeral(true).queue();
                        return;
                    }

                    try{
                        guild.ban(player.getUser(), 7).queue();
                    }catch (Exception ex){
                        e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).setEphemeral(true).queue();
                    }

                    e.replyEmbeds(PremadeEmbeds.success(String.format("Successfully banned %s from the discord server!", player.getUser().getAsMention()), reason)
                    .build()).queue();
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
        }
    }
}
