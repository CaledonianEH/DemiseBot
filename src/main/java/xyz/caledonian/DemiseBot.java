package xyz.caledonian;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.JSONObject;
import xyz.caledonian.commands.developer.EvalCommand;
import xyz.caledonian.commands.punishments.BanCommand;
import xyz.caledonian.commands.staff.EmbedCommand;
import xyz.caledonian.commands.tickets.TicketCommand;
import xyz.caledonian.commands.user.ReportCommand;
import xyz.caledonian.commands.user.SuggestCommand;
import xyz.caledonian.commands.Commands;
import xyz.caledonian.listener.GuildJoinListener;
import xyz.caledonian.managers.TicketManager;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.CustomEmotes;
import xyz.caledonian.utils.PremadeEmbeds;
import xyz.caledonian.utils.Utils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class DemiseBot {

    private JDA jda;
    private TicketManager ticket;
    private long time = 0;
    private static long startTime;

    public static void main(String[] args){
        startTime = System.currentTimeMillis();
        new DemiseBot().startBot();
    }

    @SneakyThrows
    public void startBot(){
        time = System.currentTimeMillis();
        Utils.sendConsoleLog("[BOT] Loading DemiseBot v1.0 by Caledonian");

        // Loading configuration to a JSON object
        try{
            Objects.requireNonNull(getConfig()).getString("token");
            Utils.sendConsoleLog("[CFG] Successfully loaded config.json into memory. Took %sms",
                    System.currentTimeMillis() - time);
        }catch (Exception ex){
            Utils.sendConsoleLog("[CFG] Failed to find config.json, cannot start bot.");
            return;
        }

        Utils.sendConsoleLog("[BOT] Connecting to the bot using the provided token.");
        time = System.currentTimeMillis();
        try{
            JDABuilder builder = JDABuilder.createDefault(getConfig().getString("token"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            // Setup sharding
//            for(int i = 0; i < 10; i++){
//                builder.useSharding(i, 10);
//            }
            jda = builder.build().awaitReady();
            // .build().awaitReady()
            Utils.sendConsoleLog("[BOT] Successfully connected to %s with a valid token. Took %sms", jda.getSelfUser().getAsTag(),
                    System.currentTimeMillis() - time);
        }catch (LoginException ex){
            Utils.sendConsoleLog("[BOT] [FAILED] [LoginException] Failed to validate the provided token. Failed in %sms", System.currentTimeMillis() - time);
        }catch (InterruptedException ex){
            Utils.sendConsoleLog("[BOT] [FAILED] [InterruptedExcpetion] Task was interrupted whilst connecting. Failed in %sms", System.currentTimeMillis() - time);
        }

        // Activities
        Utils.sendConsoleLog("[BOT] Registering cosmetic status for %s", jda.getSelfUser().getAsTag());
        time = System.currentTimeMillis();
        jda.getPresence().setStatus(OnlineStatus.fromKey(getConfig().getJSONObject("activity").getString("status")));
        jda.getPresence().setActivity(Activity.competing(getConfig().getJSONObject("activity").getString("activity")));
        Utils.sendConsoleLog("[BOT] Successfully set status for %s", jda.getSelfUser().getAsTag());

        // Registering Events & Managers
        Utils.sendConsoleLog("[BOT] Registering command classes");
        time = System.currentTimeMillis();
        new Utils(this, jda);
        ticket = new TicketManager(this, jda);
        new DevMessageLogger(this, jda);
        new CustomEmotes(this, jda);
        new PremadeEmbeds(this, jda);
        new SuggestCommand(this, jda);
        new ReportCommand(this, jda);
        new EmbedCommand(this, jda);
        new BanCommand(this, jda);
        new EvalCommand(this, jda);
        new TicketCommand(this, jda, ticket);
        new GuildJoinListener(this, jda);
        //new TestCommand(this, jda);
        Utils.sendConsoleLog("[BOT] Successfully registered command classes. Took %sms", System.currentTimeMillis() - time);
        registerEvents();

        Utils.sendConsoleLog("[BOT] The bot %s is fully running. Took %sms", jda.getSelfUser().getAsTag(), System.currentTimeMillis() - time);
        DevMessageLogger.sendStartupLog();
        sendStartWelcome();
    }

    private void registerEvents(){
        jda.addEventListener(new Commands(this, jda));
        jda.addEventListener(new SuggestCommand(this, jda));
        jda.addEventListener(new ReportCommand(this, jda));
        jda.addEventListener(new EmbedCommand(this, jda));
        jda.addEventListener(new BanCommand(this, jda));
        jda.addEventListener(new EvalCommand(this, jda));
        jda.addEventListener(new TicketCommand(this, jda, ticket));
        jda.addEventListener(new GuildJoinListener(this, jda));
    }

    public JSONObject getConfig() throws IOException {
        try{
            JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
            return config;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void updateCommands(){
        Commands commands = new Commands(this, jda);
        commands.setupCommands();
    }

    public void updateStatus(String activity){
        jda.getPresence().setActivity(Activity.competing(activity));
    }

    public void shutdown(){
        jda.shutdown();
    }

    @SneakyThrows
    public void sendTicketChannel(MessageChannel channel){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Demise Support & Applications");
        eb.setColor(new Color(249, 79, 91));
        eb.setDescription("Click the button to create a ticket, and get in contact with our support team. Make sure to provide a reason when it is first created!" +
                "\n\n**Reasons to create a ticket**\n- Applications\n- Schedule unavailability notices\n- Report bugs\n");

        eb.setThumbnail(jda.getSelfUser().getAvatarUrl());
        eb.setFooter(getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        channel.sendMessageEmbeds(eb.build())
                .setActionRow(Button.success("ticketCreateBtn", "Create a ticket")
                .withEmoji(Emoji.fromMarkdown(getConfig().getJSONObject("emotes").getString("create"))))
                .queue();
    }

    private void sendStartWelcome(){
        System.out.printf("=============================================%n");
        System.out.printf("Welcome, %s, to DemiseUtils%n", jda.getSelfUser().getAsTag());
        System.out.printf("%n");
        System.out.printf("- Connected to %s guilds, totaling NaN members%n", jda.getGuilds().size());
        System.out.printf("- Shards: Running%n");
        System.out.printf("    - Shard count: %s%n", jda.getShardInfo().getShardTotal());
        System.out.printf("    - Current shard: %s%n", jda.getShardInfo().getShardId());
        System.out.printf("=============================================%n");
    }
