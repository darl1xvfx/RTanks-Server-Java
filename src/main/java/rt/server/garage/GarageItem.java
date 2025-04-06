package rt.server.garage;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import rt.server.garage.modification.ModificationInfo;
import rt.server.utils.JSON;

public class GarageItem {

	 public String id;
     public String name;
     public String description;
     public int rank;
     public int index;
     public List<PropertyItem> propetys;
     public List<ModificationInfo> modifications;
     public int modificationId;
     public int price;
     public int next_price;
     public int next_rank;
     public boolean isInventory;
     public int previewId;
	 public int type;

     public GarageItem(String id, String name, String description, int type, int rank, int index, int modificationId, int price, int next_price, int next_rank, boolean isInventory, int previewId) {
    	 this.id = id;
    	 this.name = name;
    	 this.description = description;
    	 this.type = type;
    	 this.rank = rank;
    	 this.index = index;
    	 this.modificationId = modificationId;
    	 this.price = price;
    	 this.next_price = next_price;
    	 this.next_rank = next_rank;
    	 this.isInventory = isInventory;
    	 this.previewId = previewId;
     }

     public JSONObject toItemObject() {
    	 JSONObject object = new JSONObject();
    	 object.put("properts", JSON.parseProperts(this.propetys));
    	 object.put("description", this.description);
    	 object.put("index", this.index);
    	 object.put("discount", 0);
    	 object.put("type", this.type);
    	 object.put("modification", JSON.parseModifications(this.modifications));
    	 object.put("modificationID", this.modificationId);
    	 object.put("next_rank", this.next_rank);
    	 object.put("price", this.price);
    	 object.put("next_price", this.next_price);
    	 object.put("name", this.name);
    	 object.put("rank", this.rank);
    	 object.put("id", this.id);
    	 object.put("count", 9999);
    	 object.put("isInventory", this.isInventory);
    	 return object;
     }
}
