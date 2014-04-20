package ch.hearc.arcgames.entities;

import ch.hearc.arcgames.entities.util.JsfUtil;
import ch.hearc.arcgames.entities.util.PaginationHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    private User current;
    private DataModel items = null;
    @EJB
    private ch.hearc.arcgames.entities.UserFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    String login;
    String loginPasswd;
    int sessionId = 0;
    String usernameSearch = "";
    String firstNameSearch = "";
    String lastNameSearch = "";
    String locationSearch = "";
    int size;
    boolean mode = false;

    public String getLastNameSearch() {
        return lastNameSearch;
    }

    public void setLastNameSearch(String lastNameSearch) {
        this.lastNameSearch = lastNameSearch;
    }

    public String getLocationSearch() {
        return locationSearch;
    }

    public void setLocationSearch(String locationSearch) {
        this.locationSearch = locationSearch;
    }

    public String getFirstNameSearch() {
        return firstNameSearch;
    }

    public void setFirstNameSearch(String firstNameSearch) {
        this.firstNameSearch = firstNameSearch;
    }

    public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public String getUsernameSearch() {
        return usernameSearch;
    }

    public void setUsernameSearch(String usernameSearch) {
        this.usernameSearch = usernameSearch;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLoginPasswd() {
        return loginPasswd;
    }

    public void setLoginPasswd(String loginPasswd) {
        this.loginPasswd = loginPasswd;
    }

    public UserController() {
    }

    public User getSelected() {
        if (current == null) {
            current = new User();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {

        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return size;
                }

                @Override
                public DataModel createPageDataModel() {
                    DataModel dm = new ListDataModel(getFacade().findRangeSearch(usernameSearch,firstNameSearch,lastNameSearch,locationSearch, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    size = dm.getRowCount();
                    return dm;
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
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new User();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (User) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
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

    public User getUser(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + User.class.getName());
            }
        }
    }

    public void savePasswd(FacesContext context, UIComponent toValidate, Object value) {
        current.setPasswd((String) value);
    }

    public void validatePasswd(FacesContext context, UIComponent toValidate, Object value) {
        String confirmPasswd = (String) value;

        if (!confirmPasswd.equals(current.getPasswd())) {
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "invalid password", "invalid password");
            throw new ValidatorException(m);
        }
    }

    public String checkLogin() {
        List<User> users = getFacade().findAll();
        for (User u : users) {
            if (u.getUsername().equals(login) && u.getPasswd().equals(loginPasswd)) {
                sessionId = u.getId();
                return "news/List";
            }
        }
        return null;
    }

    public String logout() {
        sessionId = 0;
        return "/news/List";
    }

    public void search() {
        recreateModel();
    }

    public void searchMode() {
        mode = !mode;
    }


    public boolean isAdmin() {
        if (sessionId == 0) {
            return false;
        }
        return getFacade().find(sessionId).getIsAdmin();
    }
    
    public String getCurrentUserName()
    {
        return getFacade().find(sessionId).getUsername();
    }
    
    public String editCurrentProfile()
    {
        current = getFacade().find(sessionId);
        return "/user/Edit";
    }
}
