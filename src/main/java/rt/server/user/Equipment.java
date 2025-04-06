package rt.server.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import rt.server.garage.OwnedGarageItem;
import rt.server.garage.mountable.Resistance;

@Entity
public class Equipment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	public String turretId;

	@Column
	public String hullId;

	@Column
	public String colormapId;

	@OneToMany(mappedBy = "equip", cascade = CascadeType.ALL, targetEntity = Resistance.class, fetch = FetchType.EAGER)
	@Column
	public List<Resistance> mountedResistances;

	public Equipment() {}

	public Equipment(String turret, String hull, String colormap) {
		this.turretId = turret;
		this.hullId = hull;
		this.colormapId = colormap + "_m0";
		this.mountedResistances = new ArrayList<>();
	}

	public String getTurretName() {
		return this.turretId.split("_")[0];
	}

	public String getHullName() {
		return this.hullId.split("_")[0];
	}

	public String getColorName() {
		return this.colormapId.split("_")[0];
	}


	public String getHullModification() {
		String[] parts = this.hullId.split("_");
		if (parts.length > 1) {
			return parts[1];
		}
		return "m0";
	}
}