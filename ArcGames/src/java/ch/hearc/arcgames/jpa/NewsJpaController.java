/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.jpa;

import ch.hearc.arcgames.entities.NewsPK;
import ch.hearc.arcgames.entities.News;
import ch.hearc.arcgames.entities.User;
import ch.hearc.arcgames.exceptions.NonexistentEntityException;
import ch.hearc.arcgames.exceptions.PreexistingEntityException;
import ch.hearc.arcgames.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author mirco
 */
public class NewsJpaController implements Serializable {

    public NewsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(News news) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (news.getNewsPK() == null) {
            news.setNewsPK(new NewsPK());
        }
        news.getNewsPK().setUserid(news.getUser().getId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = news.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                news.setUser(user);
            }
            em.persist(news);
            if (user != null) {
                user.getNewsCollection().add(news);
                user = em.merge(user);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findNews(news.getNewsPK()) != null) {
                throw new PreexistingEntityException("News " + news + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(News news) throws NonexistentEntityException, RollbackFailureException, Exception {
        news.getNewsPK().setUserid(news.getUser().getId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            News persistentNews = em.find(News.class, news.getNewsPK());
            User userOld = persistentNews.getUser();
            User userNew = news.getUser();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                news.setUser(userNew);
            }
            news = em.merge(news);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getNewsCollection().remove(news);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getNewsCollection().add(news);
                userNew = em.merge(userNew);
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
                NewsPK id = news.getNewsPK();
                if (findNews(id) == null) {
                    throw new NonexistentEntityException("The news with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(NewsPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            News news;
            try {
                news = em.getReference(News.class, id);
                news.getNewsPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The news with id " + id + " no longer exists.", enfe);
            }
            User user = news.getUser();
            if (user != null) {
                user.getNewsCollection().remove(news);
                user = em.merge(user);
            }
            em.remove(news);
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

    public List<News> findNewsEntities() {
        return findNewsEntities(true, -1, -1);
    }

    public List<News> findNewsEntities(int maxResults, int firstResult) {
        return findNewsEntities(false, maxResults, firstResult);
    }

    private List<News> findNewsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(News.class));
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

    public News findNews(NewsPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(News.class, id);
        } finally {
            em.close();
        }
    }

    public int getNewsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<News> rt = cq.from(News.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
