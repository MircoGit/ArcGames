/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames;

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
public class UserhasGameJpaController implements Serializable {

    public UserhasGameJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserhasGame userhasGame) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (userhasGame.getUserhasGamePK() == null) {
            userhasGame.setUserhasGamePK(new UserhasGamePK());
        }
        userhasGame.getUserhasGamePK().setUserid(userhasGame.getUser().getId());
        userhasGame.getUserhasGamePK().setGameid(userhasGame.getGame().getId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Game game = userhasGame.getGame();
            if (game != null) {
                game = em.getReference(game.getClass(), game.getId());
                userhasGame.setGame(game);
            }
            User user = userhasGame.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                userhasGame.setUser(user);
            }
            em.persist(userhasGame);
            if (game != null) {
                game.getUserhasGameCollection().add(userhasGame);
                game = em.merge(game);
            }
            if (user != null) {
                user.getUserhasGameCollection().add(userhasGame);
                user = em.merge(user);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUserhasGame(userhasGame.getUserhasGamePK()) != null) {
                throw new PreexistingEntityException("UserhasGame " + userhasGame + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserhasGame userhasGame) throws NonexistentEntityException, RollbackFailureException, Exception {
        userhasGame.getUserhasGamePK().setUserid(userhasGame.getUser().getId());
        userhasGame.getUserhasGamePK().setGameid(userhasGame.getGame().getId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            UserhasGame persistentUserhasGame = em.find(UserhasGame.class, userhasGame.getUserhasGamePK());
            Game gameOld = persistentUserhasGame.getGame();
            Game gameNew = userhasGame.getGame();
            User userOld = persistentUserhasGame.getUser();
            User userNew = userhasGame.getUser();
            if (gameNew != null) {
                gameNew = em.getReference(gameNew.getClass(), gameNew.getId());
                userhasGame.setGame(gameNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                userhasGame.setUser(userNew);
            }
            userhasGame = em.merge(userhasGame);
            if (gameOld != null && !gameOld.equals(gameNew)) {
                gameOld.getUserhasGameCollection().remove(userhasGame);
                gameOld = em.merge(gameOld);
            }
            if (gameNew != null && !gameNew.equals(gameOld)) {
                gameNew.getUserhasGameCollection().add(userhasGame);
                gameNew = em.merge(gameNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getUserhasGameCollection().remove(userhasGame);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getUserhasGameCollection().add(userhasGame);
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
                UserhasGamePK id = userhasGame.getUserhasGamePK();
                if (findUserhasGame(id) == null) {
                    throw new NonexistentEntityException("The userhasGame with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserhasGamePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            UserhasGame userhasGame;
            try {
                userhasGame = em.getReference(UserhasGame.class, id);
                userhasGame.getUserhasGamePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userhasGame with id " + id + " no longer exists.", enfe);
            }
            Game game = userhasGame.getGame();
            if (game != null) {
                game.getUserhasGameCollection().remove(userhasGame);
                game = em.merge(game);
            }
            User user = userhasGame.getUser();
            if (user != null) {
                user.getUserhasGameCollection().remove(userhasGame);
                user = em.merge(user);
            }
            em.remove(userhasGame);
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

    public List<UserhasGame> findUserhasGameEntities() {
        return findUserhasGameEntities(true, -1, -1);
    }

    public List<UserhasGame> findUserhasGameEntities(int maxResults, int firstResult) {
        return findUserhasGameEntities(false, maxResults, firstResult);
    }

    private List<UserhasGame> findUserhasGameEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserhasGame.class));
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

    public UserhasGame findUserhasGame(UserhasGamePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserhasGame.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserhasGameCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserhasGame> rt = cq.from(UserhasGame.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
