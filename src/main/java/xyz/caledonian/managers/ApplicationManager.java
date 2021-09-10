package xyz.caledonian.managers;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.ChannelManager;
import xyz.caledonian.DemiseBot;
import xyz.caledonian.utils.PremadeEmbeds;

public class ApplicationManager {

    private DemiseBot main;
    private JDA jda;

    public ApplicationManager(DemiseBot main, JDA jda) {
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public void createUserApplication(User user, SlashCommandEvent event, Guild guild){
        TextChannel commandChannel = event.getTextChannel();
        String nameFormat = String.format("application-%s", user.getName());

        if(guild.getCategoryById(getTicketCategoryString()) == null){
            event.replyEmbeds(PremadeEmbeds.warning("The requested application channel was not found.").build()).queue();
            return;
        }
        for(GuildChannel channel : guild.getChannels()){
            if(channel.getName().equalsIgnoreCase(nameFormat)){
                event.replyEmbeds(PremadeEmbeds.warning("You already have an application open. Please close that one first.").build()).queue();
                return;
            }
        }

        TextChannel ticket = guild.createTextChannel(nameFormat, guild.getCategoryById(getTicketCategoryString())).complete();
        ChannelManager ticketManager = ticket.getManager().putPermissionOverride(guild.getMember(user), 3072L, 8192L)
                .putPermissionOverride(guild.getRolesByName("@everyone", true).get(0), 0L, 1024L);
        if(guild.getRoleById(getSupportRoleString()) != null){
            ticketManager = ticketManager.putPermissionOverride(getSupportRole(), 3072L, 8192L);
        }
        ticketManager.queue();

        event.replyEmbeds(PremadeEmbeds.success(String.format("Successfully created your application! You can see it in <#%s>",
                ticket.getId())).build()).queue();

        ticket.sendMessageEmbeds(PremadeEmbeds.success("Thank you for creating an application! Our team should be here as soon as possible.\n\nYou can help us out by informing what you're in need of!").build())
                .setActionRow(Button.danger("applicationDenyBtn", "Deny Application")
                                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                        , Button.success("applicationAcceptBtn", "Accept Application")
                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("create")))).queue();

        ticket.sendMessageEmbeds(PremadeEmbeds.success("**Angels of Demise Application**\nThanks for applying to Angels of Demise. Please enter your information, using the information below." +
                "\n\n`` 1. `` **Network level:**\n`` 2. `` **Timezone:**\n`` 3. `` **Age:**\n`` 4. `` **Bedwars stats, Wins & FKDR:**\n`` 5. `` **Link to Plancke Stats:**\n`` 6. `` **Playtime / Day:**")
                .build())
                .setActionRow(Button.link("https://hypixel.net/threads/angels-of-demise-demise-bedwars-25-guild-level-138-79-bedwars-wins-applications-open.4528422/", "Forum Post")
                .withEmoji(Emoji.fromMarkdown("<:hypixel:885741628768284673>"))).queue();
        return;
    }

    @SneakyThrows
    public void createUserApplicationButton(User user, ButtonClickEvent event, Guild guild){
        TextChannel commandChannel = event.getTextChannel();
        String nameFormat = String.format("ticket-%s", user.getName());

        if(guild.getCategoryById(getTicketCategoryString()) == null){
            event.getHook().sendMessageEmbeds(PremadeEmbeds.warning("The requested application channel was not found.").build()).setEphemeral(true).queue();
            return;
        }
        for(GuildChannel channel : guild.getChannels()){
            if(channel.getName().equalsIgnoreCase(nameFormat)){
                event.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You already have an application open. Please close that one first.").build()).setEphemeral(true).queue();
                return;
            }
        }

        TextChannel ticket = guild.createTextChannel(nameFormat, guild.getCategoryById(getTicketCategoryString())).complete();
        ChannelManager ticketManager = ticket.getManager().putPermissionOverride(guild.getMember(user), 3072L, 8192L)
                .putPermissionOverride(guild.getRolesByName("@everyone", true).get(0), 0L, 1024L);
        if(guild.getRoleById(getSupportRoleString()) != null){
            ticketManager = ticketManager.putPermissionOverride(getSupportRole(), 3072L, 8192L);
        }
        ticketManager.queue();

        event.getHook().sendMessageEmbeds(PremadeEmbeds.success(String.format("Successfully created your application! You can see it in <#%s>",
                ticket.getId())).build()).setEphemeral(true).queue();

        ticket.sendMessageEmbeds(PremadeEmbeds.success("Thank you for creating an application! Our team should be here as soon as possible.\n\nYou can help us out by informing what you're in need of!").build())
                .setActionRow(Button.danger("applicationDenyBtn", "Deny Application")
                                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("close")))
                        , Button.success("applicationAcceptBtn", "Accept Application")
                                .withEmoji(Emoji.fromMarkdown(main.getConfig().getJSONObject("emotes").getString("create")))).queue();
        return;
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
