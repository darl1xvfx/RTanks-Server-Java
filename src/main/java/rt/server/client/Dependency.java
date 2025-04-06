package rt.server.client;

import java.util.concurrent.CompletableFuture;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;

public class Dependency {
	private CompletableFuture<Void> deferred;
	private Runnable runnable;

	public Dependency(int id, CompletableFuture<Void> deferred) {
		this.deferred = deferred;
	}

	public static Dependency create() {
		return new Dependency(0, new CompletableFuture<Void>());
	}

	public void loadDependency(ClientEntity client, int id, String file, Runnable afterLoad) {
		try {
			new Command(Commands.LoadResources, Resource.fileToString("dependencies/" + file), id).send(client);
			if (afterLoad != null) {
				this.runnable = afterLoad;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadDependencyFromString(ClientEntity client, int id, String file, Runnable afterLoad) {
		try {
			new Command(Commands.LoadResources, file, id).send(client);
			if (afterLoad != null) {
				this.runnable = afterLoad;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void markDependency() {
		if (this.runnable != null) {
			this.deferred.thenRun(this.runnable);
			this.deferred.complete(null);
		}
	}
}
