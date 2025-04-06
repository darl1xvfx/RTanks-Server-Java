package rt.server.garage.mountable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rt.server.user.Equipment;

@Entity
@Table(name="mounted_resistances")
public class Resistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    public String resistance;
    @ManyToOne
    private Equipment equip;

    public Resistance() {

    }

    public Resistance(String resistance, Equipment user) {
        this.resistance = resistance;
        this.equip = user;
    }

    public String[] getParts() {
        return resistance.split("_m");
    }
}