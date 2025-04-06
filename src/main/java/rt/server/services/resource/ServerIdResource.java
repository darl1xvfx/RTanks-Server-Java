package rt.server.services.resource;

public class ServerIdResource {
   public long id;
   public long version;
   
   public ServerIdResource(long id, long vers) {
	   this.id = id;
	   this.version = vers;
   }
}