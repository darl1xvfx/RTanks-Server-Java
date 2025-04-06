package rt.server.user.permissions;

public enum Permissions {
    DEFAULT(0),
    CHATADMINISTRATOR(1),
    CHATMODERATOR(2),
    CHATCANDIDATE(3),
    BATTLEADMINISTRATOR(4),
    BATTLEMODERATOR(5),
    BATTLECANDIDATE(6),
    COMMUNITYMANAGER(7),
    EVENTSCANDIDATE(8),
    EVENTSHELPER(9),
    EVENTSADMINISTRATOR(10);
	
	public int id;
	
	Permissions(int id) {
		this.id = id;
	}
	
	public int toInt() {
		return this.id;
	}
}
