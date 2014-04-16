/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.entities;

import java.io.Serializable;
import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 *
 * @author mirco
 */
@Entity
@Table(name = "News")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "News.orderByDate", query = "SELECT n FROM News n ORDER BY n.date DESC"),
    @NamedQuery(name = "News.findAll", query = "SELECT n FROM News n"),
    @NamedQuery(name = "News.findById", query = "SELECT n FROM News n WHERE n.newsPK.id = :id"),
    @NamedQuery(name = "News.findByTitle", query = "SELECT n FROM News n WHERE n.title = :title"),
    @NamedQuery(name = "News.findByDate", query = "SELECT n FROM News n WHERE n.date = :date"),
    @NamedQuery(name = "News.findByUserid", query = "SELECT n FROM News n WHERE n.newsPK.userid = :userid")})
public class News implements Serializable, Comparable<News> {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected NewsPK newsPK;
    @Size(max = 45)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "content")
    private String content;
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "User_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public News() {
        updateDate();
    }

    public News(NewsPK newsPK) {
        this.newsPK = newsPK;
        updateDate();
    }

    public News(int id, int userid) {
        this.newsPK = new NewsPK(id, userid);
        updateDate();
    }

    public NewsPK getNewsPK() {
        return newsPK;
    }

    public void setNewsPK(NewsPK newsPK) {
        this.newsPK = newsPK;
        updateDate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateDate();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        updateDate();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        updateDate();
    }

    public User getUser() {
        return user;
    }

    public String getUserName() {
        return user.getUsername();
    }

    public void setUser(User user) {
        this.user = user;
        updateDate();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (newsPK != null ? newsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof News)) {
            return false;
        }
        News other = (News) object;
        if ((this.newsPK == null && other.newsPK != null) || (this.newsPK != null && !this.newsPK.equals(other.newsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.hearc.arcgames.News[ newsPK=" + newsPK + " ]";
    }

    private void updateDate() {
        this.date = new Date();
        date.setTime(date.getTime() + 3600 * 1000);
    }

    @Override
    public int compareTo(News n) {
        return n.getDate().compareTo(this.date);
    }

}
