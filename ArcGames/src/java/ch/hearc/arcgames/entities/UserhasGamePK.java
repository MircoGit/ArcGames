/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author mirco
 */
@Embeddable
public class UserhasGamePK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "User_id")
    private int userid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Game_id")
    private int gameid;

    public UserhasGamePK() {
    }

    public UserhasGamePK(int userid, int gameid) {
        this.userid = userid;
        this.gameid = gameid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userid;
        hash += (int) gameid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserhasGamePK)) {
            return false;
        }
        UserhasGamePK other = (UserhasGamePK) object;
        if (this.userid != other.userid) {
            return false;
        }
        if (this.gameid != other.gameid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.hearc.arcgames.UserhasGamePK[ userid=" + userid + ", gameid=" + gameid + " ]";
    }
    
}
