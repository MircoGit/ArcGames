/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mirco
 */
@Entity
@Table(name = "User_has_Game")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserhasGame.findAll", query = "SELECT u FROM UserhasGame u"),
    @NamedQuery(name = "UserhasGame.findByUserid", query = "SELECT u FROM UserhasGame u WHERE u.userhasGamePK.userid = :userid"),
    @NamedQuery(name = "UserhasGame.findByGameid", query = "SELECT u FROM UserhasGame u WHERE u.userhasGamePK.gameid = :gameid"),
    @NamedQuery(name = "UserhasGame.findByScore", query = "SELECT u FROM UserhasGame u WHERE u.score = :score")})
public class UserhasGame implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserhasGamePK userhasGamePK;
    @Column(name = "score")
    private Integer score;
    @JoinColumn(name = "Game_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Game game;
    @JoinColumn(name = "User_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserhasGame() {
    }

    public UserhasGame(UserhasGamePK userhasGamePK) {
        this.userhasGamePK = userhasGamePK;
    }

    public UserhasGame(int userid, int gameid) {
        this.userhasGamePK = new UserhasGamePK(userid, gameid);
    }

    public UserhasGamePK getUserhasGamePK() {
        return userhasGamePK;
    }

    public void setUserhasGamePK(UserhasGamePK userhasGamePK) {
        this.userhasGamePK = userhasGamePK;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userhasGamePK != null ? userhasGamePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserhasGame)) {
            return false;
        }
        UserhasGame other = (UserhasGame) object;
        if ((this.userhasGamePK == null && other.userhasGamePK != null) || (this.userhasGamePK != null && !this.userhasGamePK.equals(other.userhasGamePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.hearc.arcgames.UserhasGame[ userhasGamePK=" + userhasGamePK + " ]";
    }
    
}
