package rt.server.user;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import jakarta.persistence.*;
import rt.server.database.Repositories;
import rt.server.friends.Friend;
import rt.server.friends.IncomingFriend;
import rt.server.friends.OutgoingFriend;
import rt.server.garage.GarageItem;
import rt.server.garage.GarageItemParser;
import rt.server.garage.OwnedGarageItem;
import rt.server.utils.RankUtils;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column(name = "username", unique = true, nullable = false)
	public String username;

	@Column(name = "hash", unique = true, nullable = true)
	public String hash;

	@Column(name = "password", nullable = false)
	public String password;

	@Column(name = "country", nullable = true)
	public String country;

	@Column(name = "score", nullable = false)
	public int score;

	@Column(name = "crystalls", nullable = false)
	public int crystals;

	@Column(name = "stars", nullable = false)
	public int stars;

	@Column(name = "rank", nullable = false)
	public int rang = 0;

	@Column(name = "purchasedFirstThing", nullable = false)
	private boolean firstPurchase;

	@Column(name = "permissions", nullable = false)
	public int permissions;

	@OneToMany(mappedBy = "user", targetEntity = OwnedGarageItem.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OwnedGarageItem> items;

	@OneToMany(mappedBy = "user", targetEntity = Quest.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Quest> quests;

	@Column(name = "can_skip_quest", nullable = true)
	public Boolean canSkipQuestForFree = false;

	@Transient
	public List<Integer> receivedTiers;

	@Transient
	public int warnings;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "equipment_id")
	public Equipment equipment;

	@Column(name = "premium", nullable = false)
	public int premium;

	@OneToMany(mappedBy = "uid", targetEntity = Friend.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Friend> friends = new ArrayList<>();

	@OneToMany(mappedBy = "uid", targetEntity = OutgoingFriend.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OutgoingFriend> outgoingFriends = new ArrayList<>();

	@OneToMany(mappedBy = "uid", targetEntity = IncomingFriend.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IncomingFriend> incomingFriends = new ArrayList<>();

	public User() {
		this.warnings = 0;
		this.items = new ArrayList<>();
		this.quests = new ArrayList<>();
		this.receivedTiers = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.outgoingFriends = new ArrayList<>();
		this.incomingFriends = new ArrayList<>();
	}

	public User(String username, String password, int score, int crystals, int premium,
				Equipment equipment, int permissions) {
		this.username = username;
		this.password = password;
		this.score = score;
		this.crystals = crystals;
		this.stars = 0;
		this.rang = RankUtils.getNumberRank(RankUtils.getRankByScore(this.score));
		this.equipment = equipment;
		this.premium = premium;
		this.firstPurchase = true;
		this.items = new ArrayList<>();
		this.permissions = permissions;
		this.warnings = 0;
		this.country = null;
		this.receivedTiers = new ArrayList<>();
		this.quests = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.outgoingFriends = new ArrayList<>();
		this.incomingFriends = new ArrayList<>();
	}

	public void addFriend(String friendUsername) {
		Friend friend = new Friend(friendUsername, this.id);
		friends.add(friend);
		Repositories.persistObject(friend);
	}

	public void removeFriend(String friendUsername) {
		Friend friendToRemove = friends.stream()
				.filter(f -> f.username.equals(friendUsername))
				.findFirst()
				.orElse(null);
		if (friendToRemove != null) {
			friends.remove(friendToRemove);
			Repositories.executeInTransaction(session -> session.remove(friendToRemove));
		}
	}

	public void addOutgoingFriend(String friendUsername) {
		OutgoingFriend outgoing = new OutgoingFriend(friendUsername, this.id);
		outgoingFriends.add(outgoing);
		Repositories.persistObject(outgoing);
	}

	public void removeOutgoingFriend(String friendUsername) {
		OutgoingFriend outgoingToRemove = outgoingFriends.stream()
				.filter(f -> f.username.equals(friendUsername))
				.findFirst()
				.orElse(null);
		if (outgoingToRemove != null) {
			outgoingFriends.remove(outgoingToRemove);
			Repositories.executeInTransaction(session -> {
				// Проверяем, существует ли объект в базе перед удалением
				OutgoingFriend managed = session.find(OutgoingFriend.class, outgoingToRemove.id);
				if (managed != null) {
					session.remove(managed);
				}
			});
		}
	}

	public void addIncomingFriend(String friendUsername) {
		IncomingFriend incoming = new IncomingFriend(friendUsername, this.id);
		incomingFriends.add(incoming);
		Repositories.persistObject(incoming);
	}

	public void removeIncomingFriend(String friendUsername) {
		IncomingFriend incomingToRemove = incomingFriends.stream()
				.filter(f -> f.username.equals(friendUsername))
				.findFirst()
				.orElse(null);
		if (incomingToRemove != null) {
			incomingFriends.remove(incomingToRemove);
			Repositories.executeInTransaction(session -> {
				// Проверяем, существует ли объект в базе перед удалением
				IncomingFriend managed = session.find(IncomingFriend.class, incomingToRemove.id);
				if (managed != null) {
					session.remove(managed);
				}
			});
		}
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public List<OutgoingFriend> getOutgoingFriends() {
		return outgoingFriends;
	}

	public List<IncomingFriend> getIncomingFriends() {
		return incomingFriends;
	}

	// Остальные методы
	public int getNextScore() {
		return RankUtils.getRankByIndex(this.rang - 1).max + 1;
	}

	public List<JSONObject> getItems() {
		List<JSONObject> itemss = new ArrayList<>();
		for (OwnedGarageItem item : this.items) {
			GarageItem garageItem = GarageItemParser.items.get(item.itemId);
			if (garageItem != null) {
				JSONObject itemObject = garageItem.toItemObject();
				itemObject.put("modificationID", item.modification);
				itemObject.put("id", item.itemId);
				itemss.add(itemObject);
			}
		}
		return itemss;
	}

	public OwnedGarageItem getItem(String id) {
		for (OwnedGarageItem item : this.items) {
			if (item.getItemId().equals(id)) {
				return item;
			}
		}
		return null;
	}

	public void addQuest(Quest item) {
		quests.add(item);
		item.user = this;
		Repositories.persistObject(item);
	}

	public List<Quest> getQuests() {
		return quests;
	}

	public void setQuests(List<Quest> quests) {
		this.quests = quests;
	}

	public void addItem(OwnedGarageItem item) {
		items.add(item);
		item.user = this;
		Repositories.executeInTransaction(session -> {
			if (item.getId() == 0) {
				session.persist(item);
			} else {
				session.merge(item);
			}
		});
	}

	public List<OwnedGarageItem> getOwnedItems() {
		return items;
	}

	public void setOwnedItems(List<OwnedGarageItem> items) {
		this.items = items;
	}

	public boolean getFirstPurchase() {
		return this.firstPurchase;
	}

	public void setFirstPurchase(Boolean firstPurchase) {
		this.firstPurchase = firstPurchase;
	}

	public long getId() {
		return id;
	}

	public boolean changePassword(String oldPassword, String newPassword) {
		try {
			if (!this.password.equals(oldPassword)) {
				return false;
			}

			if (!validatePassword(newPassword)) {
				return false;
			}

			this.password = newPassword;
			Repositories.userRepository.updateUser(this);
			return true;
		} catch (Exception e) {
			System.out.println("Error changing password for user " + this.username + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean validatePassword(String password) {
		if (password == null || password.length() < 4) {
			return false;
		}
		return true;
	}
}