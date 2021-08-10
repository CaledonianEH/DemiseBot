package xyz.caledonian.commands.developer;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
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
import xyz.caledonian.utils.Utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;

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

                    if(!Utils.isDeveloper(user)){
                        e.replyEmbeds(PremadeEmbeds.warning("Only bot developers have access to this command").build()).setEphemeral(true).queue();
                        return;
                    }

                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

                    engine.put("main", main);
                    engine.put("event", e);
                    engine.put("jda", jda);
                    engine.put("guild", e.getGuild());
                    engine.put("channel", e.getChannel());

                    try{

                        engine.eval("var imports = new JavaImporter(" +
                                "java.io," +
                                "java.lang," +
                                "java.util," +
                                "Packages.net.dv8tion.jda.api," +
                                "Packages.net.dv8tion.jda.api.entities," +
                                "Packages.net.dv8tion.jda.api.entities.impl," +
                                "Packages.net.dv8tion.jda.api.managers," +
                                "Packages.net.dv8tion.jda.api.managers.impl," +
                                "Packages.net.dv8tion.jda.api.utils);");

                        Object out = engine.eval(
                                "(function() {" +
                                        "with (imports) {" +
                                        eval + "}"
                                        + "})();");

                        //Object out = engine.eval(eval);

                        e.replyEmbeds(evalSuccess(eval, out == null ? "Failed to find anything to return" : out.toString()).build()).queue();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
                    }
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
            DevMessageLogger.sendErrorLog(e.getGuild(), ex.getMessage());
        }
    }


    @SneakyThrows
    private EmbedBuilder evalSuccess(String input, String output){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Successful evaluation");
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("**Input**\n```java\n%s\n```\n\n**Output**\n```java\n%s\n```",
                input, output));
        eb.setThumbnail("https://i.imgur.com/YPlowtt.png");
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        return eb;
    }
}
