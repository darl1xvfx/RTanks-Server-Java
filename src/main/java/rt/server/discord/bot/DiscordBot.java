package rt.server.discord.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import rt.server.services.OnlineService;
import rt.server.ServerProperties;
import javax.security.auth.login.LoginException;

public class DiscordBot extends ListenerAdapter {
   public static void start() {
       try {
           JDABuilder builder = JDABuilder.createDefault(ServerProperties.DISCORD_TOKEN_BOT);
           builder.addEventListeners(new DiscordBot());
           builder.build();
       } catch (LoginException e) {
           e.printStackTrace();
       }
   }
   
   @Override
   public void onMessageReceived(MessageReceivedEvent event) {
       if (event.getAuthor().isBot()) return;
       String msg = event.getMessage().getContentRaw();
       if (msg.startsWith("!")) {
           String temp = msg.replace('!', ' ').trim();
           String[] arguments = temp.split(" ");
           switch (arguments[0]) {
              case "online": {
                  event.getChannel().sendMessage(OnlineService.getOnlineMessage()).queue();
                  return;
              }
              case "ticket": {
            	  event.getChannel().sendMessage("🇷🇺 Нажмите на кнопку ниже, чтобы создать тикет\n🇬🇧 Click the button below to create a ticket").setActionRow(Button.success("create_ticket", "Создать тикет"),Button.danger("create_ticket_en", "Create Ticket")).queue();
            	  return;
              }
           }
       }
   }
   
   private void createTicket(String userId, Guild guild, Member member, String lang) {
       TextChannel ticketChannel = guild.createTextChannel("ticket-" + userId).complete();
       ticketChannel.upsertPermissionOverride(guild.getPublicRole())
               .setDenied(net.dv8tion.jda.api.Permission.VIEW_CHANNEL)
               .queue();
       
       ticketChannel.upsertPermissionOverride(member)
               .setAllowed(net.dv8tion.jda.api.Permission.VIEW_CHANNEL)
               .queue();
       
       ticketChannel.upsertPermissionOverride(guild.getRoleById(1272598991124697095L))
               .setAllowed(net.dv8tion.jda.api.Permission.VIEW_CHANNEL)
               .queue();
       ticketChannel.upsertPermissionOverride(guild.getRoleById(1272598991124697094L))
               .setAllowed(net.dv8tion.jda.api.Permission.VIEW_CHANNEL)
               .queue();
       ticketChannel.sendMessage(lang.equals("ru") ? "<@&1272598991124697095> и <@&1272598991124697094> свяжутся с вами в ближайшее время." : "<@&1272598991124697095> and <@&1272598991124697094> will be with you shortly.").queue();
   }
   
   @Override
   public void onButtonInteraction(ButtonInteractionEvent event) {
       if (event.getButton().getId().equals("create_ticket") || event.getButton().getId().equals("create_ticket_en")) {
           createTicket(event.getUser().getId(), event.getGuild(), event.getMember(), event.getButton().getId().equals("create_ticket") ? "ru" : "en");
           event.reply(event.getButton().getId().equals("create_ticket") ? "Тикет создан!" : "Ticket created!").setEphemeral(true).queue();
       }
   }
}
