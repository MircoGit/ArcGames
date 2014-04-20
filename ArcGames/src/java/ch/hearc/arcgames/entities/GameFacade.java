/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mirco
 */
@Stateless
public class GameFacade extends AbstractFacade<Game> {
    @PersistenceContext(unitName = "ArcGamesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GameFacade() {
        super(Game.class);
    }
    
    public List<Game> findRangeSearch(String name, String description,int[] range) {
        javax.persistence.Query q;
        if(name.equals("") && description.equals(""))
        {
            return findRange(range);
        }
        else if(name.equals(""))
        {
            q = getEntityManager().createQuery("SELECT g FROM Game g WHERE g.description LIKE '%"+description+"%'");
        }
        else if(description.equals(""))
        {
            q = getEntityManager().createQuery("SELECT g FROM Game g WHERE g.name LIKE '%"+name+"%'");
        }
        else
        {
            q = getEntityManager().createQuery("SELECT g FROM Game g WHERE g.name LIKE '%"+name+"%' AND g.description LIKE '%"+description+"%'");
        }
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
