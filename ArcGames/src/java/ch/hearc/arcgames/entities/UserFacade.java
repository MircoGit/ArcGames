/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mirco
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {
    @PersistenceContext(unitName = "ArcGamesPU")
    private EntityManager em;

    @Inject
    UserController uc;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    public List<User> findRangeSearch(String username, String firstName, String lastName, String location,int[] range) {
        javax.persistence.Query q;
        
        if(!username.equals("") && !firstName.equals("") && !lastName.equals("") && !location.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%' AND u.firstName LIKE '%"+firstName+"%' AND u.lastName LIKE '%"+lastName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!firstName.equals("") && !lastName.equals("") && !location.equals(""))
        {      
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.firstName LIKE '%"+firstName+"%' AND u.lastName LIKE '%"+lastName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!username.equals("") && !lastName.equals("") && !location.equals(""))
        {      
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%'  AND u.lastName LIKE '%"+lastName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!username.equals("") && !firstName.equals("") && !location.equals(""))
        {      
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%' AND u.firstName LIKE '%"+firstName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!username.equals("") && !firstName.equals("") && !lastName.equals(""))
        {      
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%' AND u.firstName LIKE '%"+firstName+"%' AND u.lastName LIKE '%"+lastName+"%'");
        }
        else if(!username.equals("") && !firstName.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%' AND u.firstName LIKE '%"+firstName+"%'");
        }
        else if(!username.equals("") && !lastName.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%' AND u.lastName LIKE '%"+lastName+"%'");
        }
        else if(!username.equals("") && !location.equals(""))
        {         
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%'  AND u.location LIKE '%"+location+"%'");
        }
        else if(!firstName.equals("") && !lastName.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.firstName LIKE '%"+firstName+"%' AND u.lastName LIKE '%"+lastName+"%'");
        }
        else if(!firstName.equals("") && !location.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.firstName LIKE '%"+firstName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!lastName.equals("") && !location.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.lastName LIKE '%"+lastName+"%' AND u.location LIKE '%"+location+"%'");
        }
        else if(!username.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username LIKE '%"+username+"%'");
        }
        else if(!firstName.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.firstName LIKE '%"+firstName+"%'");
        }
        else if(!lastName.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.lastName LIKE '%"+lastName+"%'");
        }
        else if(!location.equals(""))
        {
            q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.location LIKE '%"+location+"%'");
        }
        else
        {
            q = getEntityManager().createQuery("SELECT u FROM User u");
        }
        
        uc.setSize(q.getResultList().size());
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
}
