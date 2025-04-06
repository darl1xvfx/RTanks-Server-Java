package rt.server.platform;

public class Model {
   public int high;
   public int low;
   public String name;
   
   public Model(int h, int l, String n) {
	   this.high = h;
	   this.low = l;
	   this.name = n;
   }
   
   public static void pop(Model model) {
	   model.high = 0;
	   model.low = 0;
	   model.name = null;
	   model = null;
   }
}
