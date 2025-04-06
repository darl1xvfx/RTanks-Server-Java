package rt.server.garage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import rt.server.user.User;

@Entity
@Table(name = "owned_items")
public class OwnedGarageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "itemId", nullable = false)
    public String itemId;

    @Column(name = "modificationIndex", nullable = false)
    public int modification;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public OwnedGarageItem() {
    }

    public OwnedGarageItem(String itemId, int modification, User user) {
        this.itemId = itemId;
        this.modification = modification;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getModification() {
        return modification;
    }

    public void setModification(int modification) {
        this.modification = modification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "OwnedGarageItem{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", modification=" + modification +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnedGarageItem that = (OwnedGarageItem) o;
        return id == that.id &&
                modification == that.modification &&
                java.util.Objects.equals(itemId, that.itemId) &&
                java.util.Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, itemId, modification, user);
    }
}