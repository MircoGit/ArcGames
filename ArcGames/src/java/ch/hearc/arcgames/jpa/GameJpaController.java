/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.jpa;

import ch.hearc.arcgames.entities.Game;
import ch.hearc.arcgames.entities.UserhasGame;
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
public class GameJpaController implements Serializable {

    public GameJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Game game) throws RollbackFailureException, Exception {
        if (game.getUserhasGameCollection() == null) {
            game.setUserhasGameCollection(new ArrayList<UserhasGame>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<UserhasGame> attachedUserhasGameCollection = new ArrayList<UserhasGame>();
            for (UserhasGame userhasGameCollectionUserhasGameToAttach : game.getUserhasGameCollection()) {
                userhasGameCollectionUserhasGameToAttach = em.getReference(userhasGameCollectionUserhasGameToAttach.getClass(), userhasGameCollectionUserhasGameToAttach.getUserhasGamePK());
                attachedUserhasGameCollection.add(userhasGameCollectionUserhasGameToAttach);
            }
            game.setUserhasGameCollection(attachedUserhasGameCollection);
            em.persist(game);
            for (UserhasGame userhasGameCollectionUserhasGame : game.getUserhasGameCollection()) {
                Game oldGameOfUserhasGameCollectionUserhasGame = userhasGameCollectionUserhasGame.getGame();
                userhasGameCollectionUserhasGame.setGame(game);
                userhasGameCollectionUserhasGame = em.merge(userhasGameCollectionUserhasGame);
                if (oldGameOfUserhasGameCollectionUserhasGame != null) {
                    oldGameOfUserhasGameCollectionUserhasGame.getUserhasGameCollection().remove(userhasGameCollectionUserhasGame);
                    oldGameOfUserhasGameCollectionUserhasGame = em.merge(oldGameOfUserhasGameCollectionUserhasGame);
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

    public void edit(Game game) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Game persistentGame = em.find(Game.class, game.getId());
            Collection<UserhasGame> userhasGameCollectionOld = persistentGame.getUserhasGameCollection();
            Collection<UserhasGame> userhasGameCollectionNew = game.getUserhasGameCollection();
            List<String> illegalOrphanMessages = null;
            for (UserhasGame userhasGameCollectionOldUserhasGame : userhasGameCollectionOld) {
                if (!userhasGameCollectionNew.contains(userhasGameCollectionOldUserhasGame)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserhasGame " + userhasGameCollectionOldUserhasGame + " since its game field is not nullable.");
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
            game.setUserhasGameCollection(userhasGameCollectionNew);
            game = em.merge(game);
            for (UserhasGame userhasGameCollectionNewUserhasGame : userhasGameCollectionNew) {
                if (!userhasGameCollectionOld.contains(userhasGameCollectionNewUserhasGame)) {
                    Game oldGameOfUserhasGameCollectionNewUserhasGame = userhasGameCollectionNewUserhasGame.getGame();
                    userhasGameCollectionNewUserhasGame.setGame(game);
                    userhasGameCollectionNewUserhasGame = em.merge(userhasGameCollectionNewUserhasGame);
                    if (oldGameOfUserhasGameCollectionNewUserhasGame != null && !oldGameOfUserhasGameCollectionNewUserhasGame.equals(game)) {
                        oldGameOfUserhasGameCollectionNewUserhasGame.getUserhasGameCollection().remove(userhasGameCollectionNewUserhasGame);
                        oldGameOfUserhasGameCollectionNewUserhasGame = em.merge(oldGameOfUserhasGameCollectionNewUserhasGame);
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
                Integer id = game.getId();
                if (findGame(id) == null) {
                    throw new NonexistentEntityException("The game with id " + id + " no longer exists.");
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
            Game game;
            try {
                game = em.getReference(Game.class, id);
                game.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The game with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<UserhasGame> userhasGameCollectionOrphanCheck = game.getUserhasGameCollection();
            for (UserhasGame userhasGameCollectionOrphanCheckUserhasGame : userhasGameCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Game (" + game + ") cannot be destroyed since the UserhasGame " + userhasGameCollectionOrphanCheckUserhasGame + " in its userhasGameCollection field has a non-nullable game field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(game);
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

    public List<Game> findGameEntities() {
        return findGameEntities(true, -1, -1);
    }

    public List<Game> findGameEntities(int maxResults, int firstResult) {
        return findGameEntities(false, maxResults, firstResult);
    }

    private List<Game> findGameEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Game.class));
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

    public Game findGame(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Game.class, id);
        } finally {
            em.close();
        }
    }

    public int getGameCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Game> rt = cq.from(Game.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
