/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames;

import ch.hearc.arcgames.exceptions.IllegalOrphanException;
import ch.hearc.arcgames.exceptions.NonexistentEntityException;
import ch.hearc.arcgames.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author mirco
 */
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws RollbackFailureException, Exception {
        if (user.getUserhasGameCollection() == null) {
            user.setUserhasGameCollection(new ArrayList<UserhasGame>());
        }
        if (user.getNewsCollection() == null) {
            user.setNewsCollection(new ArrayList<News>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<UserhasGame> attachedUserhasGameCollection = new ArrayList<UserhasGame>();
            for (UserhasGame userhasGameCollectionUserhasGameToAttach : user.getUserhasGameCollection()) {
                userhasGameCollectionUserhasGameToAttach = em.getReference(userhasGameCollectionUserhasGameToAttach.getClass(), userhasGameCollectionUserhasGameToAttach.getUserhasGamePK());
                attachedUserhasGameCollection.add(userhasGameCollectionUserhasGameToAttach);
            }
            user.setUserhasGameCollection(attachedUserhasGameCollection);
            Collection<News> attachedNewsCollection = new ArrayList<News>();
            for (News newsCollectionNewsToAttach : user.getNewsCollection()) {
                newsCollectionNewsToAttach = em.getReference(newsCollectionNewsToAttach.getClass(), newsCollectionNewsToAttach.getNewsPK());
                attachedNewsCollection.add(newsCollectionNewsToAttach);
            }
            user.setNewsCollection(attachedNewsCollection);
            em.persist(user);
            for (UserhasGame userhasGameCollectionUserhasGame : user.getUserhasGameCollection()) {
                User oldUserOfUserhasGameCollectionUserhasGame = userhasGameCollectionUserhasGame.getUser();
                userhasGameCollectionUserhasGame.setUser(user);
                userhasGameCollectionUserhasGame = em.merge(userhasGameCollectionUserhasGame);
                if (oldUserOfUserhasGameCollectionUserhasGame != null) {
                    oldUserOfUserhasGameCollectionUserhasGame.getUserhasGameCollection().remove(userhasGameCollectionUserhasGame);
                    oldUserOfUserhasGameCollectionUserhasGame = em.merge(oldUserOfUserhasGameCollectionUserhasGame);
                }
            }
            for (News newsCollectionNews : user.getNewsCollection()) {
                User oldUserOfNewsCollectionNews = newsCollectionNews.getUser();
                newsCollectionNews.setUser(user);
                newsCollectionNews = em.merge(newsCollectionNews);
                if (oldUserOfNewsCollectionNews != null) {
                    oldUserOfNewsCollectionNews.getNewsCollection().remove(newsCollectionNews);
                    oldUserOfNewsCollectionNews = em.merge(oldUserOfNewsCollectionNews);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getId());
            Collection<UserhasGame> userhasGameCollectionOld = persistentUser.getUserhasGameCollection();
            Collection<UserhasGame> userhasGameCollectionNew = user.getUserhasGameCollection();
            Collection<News> newsCollectionOld = persistentUser.getNewsCollection();
            Collection<News> newsCollectionNew = user.getNewsCollection();
            List<String> illegalOrphanMessages = null;
            for (UserhasGame userhasGameCollectionOldUserhasGame : userhasGameCollectionOld) {
                if (!userhasGameCollectionNew.contains(userhasGameCollectionOldUserhasGame)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserhasGame " + userhasGameCollectionOldUserhasGame + " since its user field is not nullable.");
                }
            }
            for (News newsCollectionOldNews : newsCollectionOld) {
                if (!newsCollectionNew.contains(newsCollectionOldNews)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain News " + newsCollectionOldNews + " since its user field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<UserhasGame> attachedUserhasGameCollectionNew = new ArrayList<UserhasGame>();
            for (UserhasGame userhasGameCollectionNewUserhasGameToAttach : userhasGameCollectionNew) {
                userhasGameCollectionNewUserhasGameToAttach = em.getReference(userhasGameCollectionNewUserhasGameToAttach.getClass(), userhasGameCollectionNewUserhasGameToAttach.getUserhasGamePK());
                attachedUserhasGameCollectionNew.add(userhasGameCollectionNewUserhasGameToAttach);
            }
            userhasGameCollectionNew = attachedUserhasGameCollectionNew;
            user.setUserhasGameCollection(userhasGameCollectionNew);
            Collection<News> attachedNewsCollectionNew = new ArrayList<News>();
            for (News newsCollectionNewNewsToAttach : newsCollectionNew) {
                newsCollectionNewNewsToAttach = em.getReference(newsCollectionNewNewsToAttach.getClass(), newsCollectionNewNewsToAttach.getNewsPK());
                attachedNewsCollectionNew.add(newsCollectionNewNewsToAttach);
            }
            newsCollectionNew = attachedNewsCollectionNew;
            user.setNewsCollection(newsCollectionNew);
            user = em.merge(user);
            for (UserhasGame userhasGameCollectionNewUserhasGame : userhasGameCollectionNew) {
                if (!userhasGameCollectionOld.contains(userhasGameCollectionNewUserhasGame)) {
                    User oldUserOfUserhasGameCollectionNewUserhasGame = userhasGameCollectionNewUserhasGame.getUser();
                    userhasGameCollectionNewUserhasGame.setUser(user);
                    userhasGameCollectionNewUserhasGame = em.merge(userhasGameCollectionNewUserhasGame);
                    if (oldUserOfUserhasGameCollectionNewUserhasGame != null && !oldUserOfUserhasGameCollectionNewUserhasGame.equals(user)) {
                        oldUserOfUserhasGameCollectionNewUserhasGame.getUserhasGameCollection().remove(userhasGameCollectionNewUserhasGame);
                        oldUserOfUserhasGameCollectionNewUserhasGame = em.merge(oldUserOfUserhasGameCollectionNewUserhasGame);
                    }
                }
            }
            for (News newsCollectionNewNews : newsCollectionNew) {
                if (!newsCollectionOld.contains(newsCollectionNewNews)) {
                    User oldUserOfNewsCollectionNewNews = newsCollectionNewNews.getUser();
                    newsCollectionNewNews.setUser(user);
                    newsCollectionNewNews = em.merge(newsCollectionNewNews);
                    if (oldUserOfNewsCollectionNewNews != null && !oldUserOfNewsCollectionNewNews.equals(user)) {
                        oldUserOfNewsCollectionNewNews.getNewsCollection().remove(newsCollectionNewNews);
                        oldUserOfNewsCollectionNewNews = em.merge(oldUserOfNewsCollectionNewNews);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<UserhasGame> userhasGameCollectionOrphanCheck = user.getUserhasGameCollection();
            for (UserhasGame userhasGameCollectionOrphanCheckUserhasGame : userhasGameCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the UserhasGame " + userhasGameCollectionOrphanCheckUserhasGame + " in its userhasGameCollection field has a non-nullable user field.");
            }
            Collection<News> newsCollectionOrphanCheck = user.getNewsCollection();
            for (News newsCollectionOrphanCheckNews : newsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the News " + newsCollectionOrphanCheckNews + " in its newsCollection field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
