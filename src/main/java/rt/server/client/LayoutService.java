package rt.server.client;

import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class LayoutService {
	private ClientEntity client;
	
	public LayoutService(ClientEntity client) {
		this.client = client;
	}
	
	public void changeLayoutSwitch(String layout) {
		this.client.currentLayout = layout;
		new Command(Commands.StartLayoutSwitch, layout).send(this.client);
	}
	
	public void endLayoutSwitch(String layout1, String layout) {
		new Command(Commands.EndLayoutSwitch, layout1, layout).send(this.client);
	}

}
