package xyz.caledonian.commands.developer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.PremadeEmbeds;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public EvalCommand(DemiseBot main, JDA jda){
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
                case "eval":
                    //e.deferReply().queue();
                    User user = e.getUser();
                    Guild guild = e.getGuild();
                    Member member = guild.getMember(user);
                    String eval = e.getOption("code").getAsString();

                    if(!member.hasPermission(Permission.BAN_MEMBERS)){
                        e.replyEmbeds(PremadeEmbeds.warning("You do not have enough permissions!").build()).setEphemeral(true).queue();
                        return;
                    }

                    ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
                    se.put("bot", main);
                    se.put("event", e);
                    se.put("jda", jda);
                    se.put("guild", e.getGuild());
                    se.put("channel", e.getChannel());

                    try{
                        e.replyEmbeds(PremadeEmbeds.success("Successfully evaluated command", String.valueOf(se.eval(eval))).build()).queue();
                    }catch (Exception ex){
                        e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
                    }
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
        }
    }
}
