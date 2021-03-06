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
public class NewsFacade extends AbstractFacade<News> {
    @PersistenceContext(unitName = "ArcGamesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NewsFacade() {
        super(News.class);
    }
    
    @Override
    public List<News> findRange(int[] range) {
        javax.persistence.Query q = getEntityManager().createNamedQuery("News.orderByDate");
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
