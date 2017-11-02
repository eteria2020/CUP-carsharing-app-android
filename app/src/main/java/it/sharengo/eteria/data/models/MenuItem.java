package it.sharengo.eteria.data.models;

public class MenuItem {
    
    public enum Section {
        NONE,
        HOME,
        LOGIN,
        SIGNUP,
        FAQ,
        RATES,
        HELP,
        PROFILE,
        BOOKING,
        HISTORIC,
        BUY,
        SHARE,
        SETTINGS,
        LOGOUT;
        
        public static Section toSection (String sectionString) {
            try {
                return valueOf(sectionString);
            } catch (Exception ex) {
                // For error cases
                return NONE;
            }
        }
    }
    
    public Section section;
    public boolean selected;

    public MenuItem(Section section) {
        this.section = section;
    }

    public MenuItem(MenuItem menuItem) {
        this(menuItem.section);
        this.selected = menuItem.selected;
    }
}
