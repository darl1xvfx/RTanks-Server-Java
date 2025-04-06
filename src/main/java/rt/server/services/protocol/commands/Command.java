package rt.server.services.protocol.commands;

import rt.server.ServerProperties;
import rt.server.client.ClientEntity;

public class Command {
   private Commands command;
   private Object[] args;
   
   public Command(Commands command, Object... args) {
	   this.command = command;
	   this.args = args;
   }
   
   public void send(ClientEntity client) {
	   StringBuilder sb = new StringBuilder();
	   sb.append(this.command.concat());
	   sb.append(ServerProperties.DELIM_ARGUMENTS_SYMBOL);
	   if (this.args != null && this.args.length > 0) {
	      for (int i = 0; i < this.args.length; i++) {
		    sb.append(this.args[i]);
		    sb.append(ServerProperties.DELIM_ARGUMENTS_SYMBOL);
	      }
	   } 
	   client.send(sb.toString());
   }
}
