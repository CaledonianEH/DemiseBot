package xyz.caledonian.commands;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.PremadeEmbeds;

public class Commands extends ListenerAdapter {

    private DemiseBot main;
    private JDA jda;

    public Commands(DemiseBot main, JDA jda){
        this.main = main;
        this.jda = jda;

        setupCommands();
    }

    @SneakyThrows
    private void setupCommands(){
        Guild guild = jda.getGuildById(main.getConfig().getJSONObject("development").getString("discord-id"));

//        CommandListUpdateAction commands = guild.updateCommands();
//
//        commands.addCommands(new CommandData(
//                "testslash", "Testing slash commands ignore me"
//        )).queue();
//
//        commands.addCommands(new CommandData(
//                "suggest", "Suggest a new feature or change to the guild, and let the rest of the guild vote on it!"
//        ).addOptions(new OptionData(OptionType.STRING, "title", "Your suggestion in a nutshell.").setRequired(true))
//                .addOptions(new OptionData(OptionType.STRING, "suggestion", "Your suggestion. Please be detailed, and link pictures if possible").setRequired(true))).queue();
//
//        // Report cmd
//        commands.addCommands(new CommandData(
//                "report", "Report a guild member for cheating or other reasons"
//        ).addOptions(new OptionData(OptionType.STRING, "player", "The guild member's username you wish to report").setRequired(true))
//                .addOptions(new OptionData(OptionType.STRING, "reason", "The reason why you are reporting this player. Please be as detailed and through as possible.").setRequired(true))
//                .addOptions(new OptionData(OptionType.STRING, "replay-id", "Attach a REPLAY ID if you have one").setRequired(false))
//        ).queue();
//
//        // Embed cmd
//        commands.addCommands(new CommandData(
//                        "embed", "Send a message or announcement through an embed"
//                ).addOptions(new OptionData(OptionType.STRING, "message", "Your message you wish to send in an embed").setRequired(true))
//                        .addOptions(new OptionData(OptionType.CHANNEL, "channel", "Where should the message be sent?").setRequired(true))
//                        .addOptions(new OptionData(OptionType.BOOLEAN, "ping", "Should the bot ping @everyone").setRequired(true))
//        ).queue();
//
//        // PUNISHMENT COMMANDS *----------*----------*----------*
//        // Ban cmd
//        commands.addCommands(new CommandData(
//                        "ban", "Ban a discord user from this discord"
//                ).addOptions(new OptionData(OptionType.USER, "user", "The user you wish to ban").setRequired(true))
//                        .addOptions(new OptionData(OptionType.STRING, "reason", "What should the reason be").setRequired(false))
//        ).queue();
//
//        // Kick cmd
//        commands.addCommands(new CommandData(
//                        "kick", "Kick a user from this discord"
//                ).addOptions(new OptionData(OptionType.USER, "user", "The user you wish to kick").setRequired(true))
//                        .addOptions(new OptionData(OptionType.STRING, "reason", "What should the reason be").setRequired(false))
//        ).queue();
//
//        // Mute cmd
//        commands.addCommands(new CommandData(
//                        "mute", "Mute a user, and remove their ability to send messages"
//                ).addOptions(new OptionData(OptionType.USER, "user", "The user you wish to mute").setRequired(true))
//                        .addOptions(new OptionData(OptionType.STRING, "reason", "What should the reason be").setRequired(false))
//        ).queue();
//
//        // Eval cmd
//        commands.addCommands(new CommandData(
//                        "eval", "Evaluate JavaScript code using the Nashorn engine."
//                ).addOptions(new OptionData(OptionType.STRING, "code", "DemiseBot bot, MessageReceivedEvent event, JDA jda, Guild guild, MessageChannel channel").setRequired(true))
//        ).queue();
//
//        commands.queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getGuild() == null){
            return;
        }

        System.out.println("slash command");

        try{
            switch (e.getName()){
                case "testslash":
                    e.deferReply();
                    e.reply("[DEBUG] DeferReply functioning properly")
                            .addActionRow(Button.primary("blue1", "Blue Button").withEmoji(Emoji.fromMarkdown("<a:blobDance:807671473060839475>")))
                            .addActionRow(Button.success("green1", "Green Button"))
                            .addActionRow(Button.secondary("gray1", "Gray Button"))
                            .addActionRow(Button.danger("red1", "Red Button"))
                            .addActionRow(SelectionMenu.create("selection1")
                            .addOption("Option 1", "o1", Emoji.fromMarkdown("<a:blobDance:807671473060839475>"))
                            .addOption("Option 2", "o2").build())
                            .queue();
                    break;

            }
        }catch (Exception ex){
            e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
        }
    }
}
