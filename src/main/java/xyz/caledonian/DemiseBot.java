package xyz.caledonian;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.JSONObject;
import xyz.caledonian.commands.developer.EvalCommand;
import xyz.caledonian.commands.punishments.BanCommand;
import xyz.caledonian.commands.staff.EmbedCommand;
import xyz.caledonian.commands.user.ReportCommand;
import xyz.caledonian.commands.user.SuggestCommand;
import xyz.caledonian.commands.Commands;
import xyz.caledonian.privmsgs.DevMessageLogger;
import xyz.caledonian.utils.CustomEmotes;
import xyz.caledonian.utils.PremadeEmbeds;
import xyz.caledonian.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class DemiseBot {

    private JDA jda;

    public static void main(String[] args){
        new DemiseBot().startBot();
    }

    @SneakyThrows
    public void startBot(){
        System.out.println("Loading DemiseBot v1.0 by Caledonian");

        try{
            Objects.requireNonNull(getConfig()).getString("token");
        }catch (Exception ex){
            System.out.println("Failed to find config.json, cannot start bot");
            return;
        }

        jda = JDABuilder.createDefault(getConfig().getString("token"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build().awaitReady();

        System.out.println(String.format("Successfully connected to %s with a valid token.", jda.getSelfUser().getAsTag()));

        // Activities
        jda.getPresence().setStatus(OnlineStatus.fromKey(getConfig().getJSONObject("activity").getString("status")));
        jda.getPresence().setActivity(Activity.competing(getConfig().getJSONObject("activity").getString("activity")));

        // Registering Events & Managers
        new Utils(this, jda);
        new DevMessageLogger(this, jda);
        new CustomEmotes(this, jda);
        new PremadeEmbeds(this, jda);
        new SuggestCommand(this, jda);
        new ReportCommand(this, jda);
        new EmbedCommand(this, jda);
        new BanCommand(this, jda);
        new EvalCommand(this, jda);
        //new TestCommand(this, jda);
        registerEvents();

        DevMessageLogger.sendStartupLog();
    }

    private void registerEvents(){
        jda.addEventListener(new Commands(this, jda));
        jda.addEventListener(new SuggestCommand(this, jda));
        jda.addEventListener(new ReportCommand(this, jda));
        jda.addEventListener(new EmbedCommand(this, jda));
        jda.addEventListener(new BanCommand(this, jda));
        jda.addEventListener(new EvalCommand(this, jda));
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
}
