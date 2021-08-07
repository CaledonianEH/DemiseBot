package xyz.caledonian.commands.user;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.CustomEmotes;
import xyz.caledonian.utils.Formatting;
import xyz.caledonian.utils.PremadeEmbeds;

import java.awt.*;

public class SuggestCommand extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public SuggestCommand(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;

        //setupCommands();
    }

    @SneakyThrows
    private void setupCommands(){
        Guild guild = jda.getGuildById(main.getConfig().getJSONObject("development").getString("discord-id"));

        CommandListUpdateAction commands = guild.updateCommands();

        commands.addCommands(new CommandData(
                "suggest", "Suggest a new feature or change to the guild, and let the rest of the guild vote on it!"
        ).addOption(OptionType.STRING, "suggestion", "Your suggestion")).queue();

        commands.queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getGuild() == null){
            return;
        }

        try{
            switch (e.getName()){
                case "suggest":
                    //e.deferReply().queue();
                    TextChannel tc = jda.getTextChannelById(main.getConfig().getJSONObject("channels").getLong("suggestions"));
                    String title = e.getOption("title").getAsString();
                    String suggestion = e.getOption("suggestion").getAsString();

                    e.replyEmbeds(PremadeEmbeds.success(String.format("Successfully added your suggestion to %s! Other players will be able to vote on it.",
                            "<#" + main.getConfig().getJSONObject("channels").getLong("suggestions") + ">"),
                            suggestion).build()).queue();

                    // Channel
                    try{
                        sendSuggestion(tc, e.getUser(), title, suggestion);
                    }catch (Exception ex){
                        e.getChannel().sendMessageEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
                    }
            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
        }
    }

    @SneakyThrows
    private void sendSuggestion(TextChannel tc, User user, String title, String message){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setColor(new Color(61, 216, 143));
        eb.setDescription(String.format("**From:** %s **on** %s\n\n%s", user.getAsMention(), Formatting.getTimeFormat(), message));
        eb.setThumbnail(user.getAvatarUrl());
        eb.setFooter(main.getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        tc.sendTyping().queue();
        tc.sendMessageEmbeds(eb.build()).queue(msg -> {
           msg.addReaction(CustomEmotes.getEmoteFromKey("no")).queue();
        });
    }
}
