package rt.server.utils;

import java.util.HashMap;
import rt.server.client.ClientEntity;
import rt.server.client.Dependency;

public class DependencyUtils {
	
	private int id;
	private HashMap<Integer, Dependency> dependencies;
	private ClientEntity client;
	
	public DependencyUtils(ClientEntity c) {
	     id = 0;
	     this.client = c;
		 dependencies = new HashMap<Integer, Dependency>();
	}
	
    public static DependencyUtils getInstance(ClientEntity client) {
    	return (new DependencyUtils(client));
    }
    
    public void loadDependency(String file, Runnable afterLoad) {
    	Dependency dependency = Dependency.create();
    	id++;
    	dependency.loadDependency(this.client, id, file, afterLoad);
    	dependencies.put(id, dependency);
    }
    
    public void loadDependencyFromString(String file, Runnable afterLoad) {
    	Dependency dependency = Dependency.create();
    	id++;
    	dependency.loadDependencyFromString(this.client, id, file, afterLoad);
    	dependencies.put(id, dependency);
    }
    
    public void mark(int id) {
    	dependencies.get(id).markDependency();
    }
}
