package rt.server.database;

import org.hibernate.Session;
import rt.server.logger.Logger;
import rt.server.user.User;

public class Repositories {
	public static UserRepository userRepository = new UserRepository();
	public static PromocodeRepository promocodeRepository = new PromocodeRepository();
	public static ClanRepositroy clanRepository = new ClanRepositroy();

	public static void init() {
		promocodeRepository.init();
		Logger.log(Logger.INFO, "Repositories initialized!");
	}

	public static void persistObject(Object object) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				session.beginTransaction();
				session.persist(object);
				session.getTransaction().commit();
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Failed to persist object: " + object + ", Error: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session while persisting object: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static <T> T mergeObject(T object) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				session.beginTransaction();
				T mergedObject = (T) session.merge(object);
				session.getTransaction().commit();
				return mergedObject;
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Failed to merge object: " + object + ", Error: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session while merging object: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public static void removeObject(Object object) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				session.beginTransaction();
				session.remove(object);
				session.getTransaction().commit();
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Failed to remove object: " + object + ", Error: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session while removing object: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void executeInTransaction(java.util.function.Consumer<Session> action) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				session.beginTransaction();
				action.accept(session);
				session.getTransaction().commit();
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Transaction failed: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void updateUser(User user) {
		try (Session session = HibernateUtils.getSession()) {
			try {
				Logger.log(Logger.INFO, "Updating user: " + user);
				session.beginTransaction();
				session.merge(user);
				session.getTransaction().commit();
			} catch (Exception e) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
				}
				Logger.log(Logger.ERROR, "Failed to update user: " + user + ", Error: " + e.getMessage());
				e.printStackTrace();
				throw e;
			}
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Error closing session while updating user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
}