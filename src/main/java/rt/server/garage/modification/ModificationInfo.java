//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rt.server.garage.modification;

import java.util.ArrayList;
import java.util.List;
import rt.server.garage.PropertyItem;

public class ModificationInfo {
    public int previewId;
    public int price;
    public int rank;
    public List<PropertyItem> propertys;

    public ModificationInfo(int previewId, int price, int rank, List<PropertyItem> propertys) {
        this.previewId = previewId;
        this.price = price;
        this.rank = rank;
        this.propertys = propertys;
    }
}
