package rt.server.database;

import org.hibernate.*;

import rt.server.garage.OwnedGarageItem;
import rt.server.logger.Logger;
import rt.server.services.ban.block.BlockGame;
import rt.server.user.Quest;
import rt.server.user.User;

public class UserRepository {

	public void createUser(User user) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				session.beginTransaction();
				session.persist(user);
				session.getTransaction().commit();
				user.addItem(new OwnedGarageItem("hunter", 0, user));
				user.addItem(new OwnedGarageItem("smoky", 0, user));
				user.addItem(new OwnedGarageItem("green", 0, user));
				user.addItem(new OwnedGarageItem("holiday", 0, user));
				user.addItem(new OwnedGarageItem("lootbox", 0, user));
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void deleteUserQuest(Quest userQuest) {
        try {
            Session session = HibernateUtils.getSession();
            Transaction tx = session.beginTransaction();
            session.delete(userQuest);
            tx.commit();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

	public User getUser(String username) {
		try (Session session = HibernateUtils.getSession()) {
			Logger.log(Logger.INFO, "Fetching user by username: " + username);
			session.beginTransaction();
			User user = session.createQuery("FROM User WHERE username = :username", User.class)
					.setParameter("username", username)
					.uniqueResultOptional()
					.orElse(null);
			session.getTransaction().commit();
			Logger.log(Logger.INFO, "Fetched user: " + user.username);
			return user;
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to fetch user by username: " + username + ", Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public int getBanReasonId(String username) {
		try (Session session = HibernateUtils.getSession()) {
			BlockGame blockGame = session
					.createQuery("FROM BlockGame WHERE username = :username", BlockGame.class)
					.setParameter("username", username)
					.getResultList()
					.stream()
					.findFirst()
					.orElse(null);
			if (blockGame == null) {
				Logger.log(Logger.INFO, "No ban record found for username: " + username);
				return -1; // Или другое значение по умолчанию
			}
			return blockGame.reason;
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to get ban reason for username: " + username + ", Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	public boolean userIsBlocked(String username) {
        try(Session session = HibernateUtils.getSession()) {
            String hql = "SELECT COUNT(b) FROM BlockGame b WHERE b.username = :username";
            jakarta.persistence.Query query = session.createQuery(hql, Long.class);
            query.setParameter("username", username);
            return (Long) query.getSingleResult() != 0;
        }
	}
	
	public User getUserByHash(String hash) {
	    User user = null;
	    try (Session session = HibernateUtils.getSession()) {
	        session.beginTransaction();
	        user = session.createQuery("FROM User WHERE hash = :hash", User.class)
	                .setParameter("hash", hash)
	                .uniqueResultOptional()
	                .orElse(null);
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return user;
	}

	public void updateUser(User user) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				Logger.log(Logger.INFO, "Updating user: " + user.username);
				session.beginTransaction();
				session.merge(user);
				session.getTransaction().commit();
				Logger.log(Logger.INFO, "Successfully updated user: " + user.username);
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Failed to update user: " + user.username + ", Error: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session while updating user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	public boolean hasUser(String user) {
        try(Session session = HibernateUtils.getSession()) {
            String hql = "SELECT COUNT(b) FROM User b WHERE b.username = :username";
            jakarta.persistence.Query query = session.createQuery(hql, Long.class);
            query.setParameter("username", user);
            return (Long) query.getSingleResult() != 0;
        }
	}

	public void banUser(User user, int reason) {
		BlockGame bg = new BlockGame(user.username, reason);
        try(Session session = HibernateUtils.getSession()) {
        	session.beginTransaction();
    	    session.persist(bg);
    	    session.getTransaction().commit();
        }		
	}

	public void unbanUser(User user) {
	    try (Session session = HibernateUtils.getSession()) {
	        session.beginTransaction();
	        BlockGame q = session
    	            .createQuery("FROM BlockGame WHERE username = :username", BlockGame.class)
    	            .setParameter("username", user.username)
    	            .getResultList()
    	            .stream()
    	            .findFirst()
    	            .orElse(null);
	        if (q != null) {
	            session.remove(q);
	        }
	        session.getTransaction().commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }		
	}
}
