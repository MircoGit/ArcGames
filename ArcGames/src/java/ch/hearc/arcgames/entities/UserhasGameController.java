package ch.hearc.arcgames.entities;

import ch.hearc.arcgames.entities.util.JsfUtil;
import ch.hearc.arcgames.entities.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

@Named("userhasGameController")
@SessionScoped
public class UserhasGameController implements Serializable {

    private UserhasGame current;
    private DataModel items = null;
    @EJB
    private ch.hearc.arcgames.entities.UserhasGameFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    @Inject
    private UserController uc;
    
    @Inject
    private GameController gc;

    public UserhasGameController() {
    }

    public UserhasGame getSelected() {
        if (current == null) {
            current = new UserhasGame();
            current.setUserhasGamePK(new ch.hearc.arcgames.entities.UserhasGamePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private UserhasGameFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (UserhasGame) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new UserhasGame();
        current.setUserhasGamePK(new ch.hearc.arcgames.entities.UserhasGamePK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.getUserhasGamePK().setGameid(current.getGame().getId());
            current.getUserhasGamePK().setUserid(current.getUser().getId());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserhasGameCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (UserhasGame) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getUserhasGamePK().setGameid(current.getGame().getId());
            current.getUserhasGamePK().setUserid(current.getUser().getId());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserhasGameUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (UserhasGame) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserhasGameDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public UserhasGame getUserhasGame(ch.hearc.arcgames.entities.UserhasGamePK id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = UserhasGame.class)
    public static class UserhasGameControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserhasGameController controller = (UserhasGameController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userhasGameController");
            return controller.getUserhasGame(getKey(value));
        }

        ch.hearc.arcgames.entities.UserhasGamePK getKey(String value) {
            ch.hearc.arcgames.entities.UserhasGamePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new ch.hearc.arcgames.entities.UserhasGamePK();
            key.setUserid(Integer.parseInt(values[0]));
            key.setGameid(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(ch.hearc.arcgames.entities.UserhasGamePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getUserid());
            sb.append(SEPARATOR);
            sb.append(value.getGameid());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserhasGame) {
                UserhasGame o = (UserhasGame) object;
                return getStringKey(o.getUserhasGamePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserhasGame.class.getName());
            }
        }
    }
    
    public void updateScore(String s)
    {
        if(s.equals("")) s = "0";
        int score = Integer.valueOf(s);
        UserhasGame newUhasG = new UserhasGame();
        newUhasG.setUser(uc.getUser(uc.getSessionId()));
        newUhasG.setGame(gc.getSelected());
        newUhasG.setScore(score);
        boolean find = false;
        for(UserhasGame uhg : getFacade().findAll())
        {
            if(uhg.getGame().equals(newUhasG.getGame()) && uhg.getUser().equals(newUhasG.getUser()))
            {
                find = true;
                if(newUhasG.getScore() > uhg.getScore())
                {
                    uhg.setScore(score);
                }
            }
        }
        if(!find){
            newUhasG.setUserhasGamePK(new ch.hearc.arcgames.entities.UserhasGamePK());
            newUhasG.getUserhasGamePK().setGameid(gc.getSelected().getId());
            newUhasG.getUserhasGamePK().setUserid(uc.getSessionId());
            getFacade().create(newUhasG);
        }
    }
}
