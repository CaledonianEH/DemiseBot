package xyz.caledonian.commands.punishments;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.PremadeEmbeds;

public class KickCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public KickCommand(DemiseBot main, JDA jda){
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
                case "kick":
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
                    e.replyEmbeds(PremadeEmbeds.success(String.format("Successfully kicked %s from the discord server!", player.getUser().getAsMention()), reason)
                    .build()).queue();

                    guild.kick(player).queue();
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
            DevMessageLogger.sendErrorLog(e.getGuild(), ex.getMessage());
        }
    }
}
