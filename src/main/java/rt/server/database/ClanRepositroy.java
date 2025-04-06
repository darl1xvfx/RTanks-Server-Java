package rt.server.database;

import org.hibernate.Session;
import rt.server.clans.Clan;

public class ClanRepositroy {
	public void createClan(Clan clan) {
        try(Session session = HibernateUtils.getSession()) {
    	    session.getTransaction().begin();
    	    session.persist(clan.tag, clan);
    	    session.getTransaction().commit();
        }
	}
	
	public Clan getClanByCreator(String username) {
        try(Session session = HibernateUtils.getSession()) {
    	    return session
    	            .createQuery("FROM Clan WHERE creatorId = :creatorId", Clan.class)
    	            .setParameter("creatorId", username)
    	            .getResultList()
    	            .stream()
    	            .findFirst()
    	            .orElse(null);
        }
	}
	
	public Clan getClanByTag(String tag) {
        try(Session session = HibernateUtils.getSession()) {
    	    return session
    	            .createQuery("FROM Clan WHERE tag = :tag", Clan.class)
    	            .setParameter("tag", tag)
    	            .getResultList()
    	            .stream()
    	            .findFirst()
    	            .orElse(null);
        }
	}
}
