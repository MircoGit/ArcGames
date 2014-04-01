/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mirco
 */
@Stateless
public class UserhasGameFacade extends AbstractFacade<UserhasGame> {
    @PersistenceContext(unitName = "ArcGamesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserhasGameFacade() {
        super(UserhasGame.class);
    }
    
}
