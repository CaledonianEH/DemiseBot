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
        System.out.println("[BOT] Loading DemiseBot v1.0 by Caledonian");

        // Loading configuration to a JSON object
        try{
            Objects.requireNonNull(getConfig()).getString("token");
            System.out.printf("[CFG] Successfully loaded config.json into memory. Took %sms",
                    System.currentTimeMillis() - time);
        }catch (Exception ex){
            System.out.println("[CFG] Failed to find config.json, cannot start bot.");
            return;
        }

        System.out.println("[BOT] Connecting to the bot using the provided token.");
        time = System.currentTimeMillis();
        try{
            JDABuilder builder = JDABuilder.createDefault(getConfig().getString("token"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            // Setup sharding
            for(int i = 0; i < 10; i++){
                builder.useSharding(i, 10);
            }
            jda = builder.build().awaitReady();
            // .build().awaitReady()
            System.out.printf("[BOT] Successfully connected to %s with a valid token. Took %sms", jda.getSelfUser().getAsTag(),
                    System.currentTimeMillis() - time);
        }catch (LoginException ex){
            System.out.printf("[BOT] [FAILED] [LoginException] Failed to validate the provided token. Failed in %sms", System.currentTimeMillis() - time);
        }catch (InterruptedException ex){
            System.out.printf("[BOT] [FAILED] [InterruptedExcpetion] Task was interrupted whilst connecting. Failed in %sms", System.currentTimeMillis() - time);
        }

        // Activities
        System.out.printf("[BOT] Registering cosmetic status for %s", jda.getSelfUser().getAsTag());
        time = System.currentTimeMillis();
        jda.getPresence().setStatus(OnlineStatus.fromKey(getConfig().getJSONObject("activity").getString("status")));
        jda.getPresence().setActivity(Activity.competing(getConfig().getJSONObject("activity").getString("activity")));
        System.out.printf("[BOT] Successfully set status for %s", jda.getSelfUser().getAsTag());

        // Registering Events & Managers
        System.out.println("[BOT] Registering command classes");
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
        System.out.printf("[BOT] Successfully registered command classes. Took %sms", System.currentTimeMillis() - time);
        registerEvents();

        System.out.printf("[BOT] The bot %s is fully running. Took %sms", jda.getSelfUser().getAsTag(), System.currentTimeMillis() - time);
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

        eb.setTitle("Demise Tickets");
        eb.setColor(new Color(249, 79, 91));
        eb.setDescription("Click the button to create a ticket, and get in contact with our support team. Make sure to provide a reason when it is first created!" +
                "\n\n**Reasons to create a ticket**\n- Applications\n- Schedule unavailability notices\n- Report bugs\n");

        eb.setThumbnail(jda.getSelfUser().getAvatarUrl());
        eb.setFooter(getConfig().getString("footer-link"), "https://i.imgur.com/xIIl8Np.png");

        channel.sendMessageEmbeds(eb.build())
                .setActionRow(Button.success("ticketCreateBtn", "Create a ticket")
                .withEmoji(Emoji.fromMarkdown(getConfig().getJSONObject("emotes").getString("create")))
                ).queue();
    }

    private void sendStartWelcome(){
        System.out.printf("=============================================");
        System.out.printf("Welcome, %s, to DemiseUtils");
        System.out.printf("");
        System.out.printf("- Connected to %s guilds, totaling NaN members", jda.getGuilds().size());
        System.out.printf("- Shards: Running");
        System.out.printf("    - Shard count: %s", jda.getShardInfo().getShardTotal());
        System.out.printf("    - Current shard: %s", jda.getShardInfo().getShardId());
        System.out.printf("    - Avg Gateway Ping: %s", jda.getShardManager().getAverageGatewayPing());
        System.out.printf("=============================================");
    }
}
