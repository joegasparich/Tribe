package items;

import java.util.ArrayList;

/**
 *
 * @author Rory
 * 
 * Stores items in it, will be migrated to a building class once buildings are implemented
 */
public class Storage {
    
    private ArrayList<Item> items = new ArrayList<Item>();
    
    public void addItem(Item i) {
        items.add(i);
    }
    
    public void removeItem(Item i) {
        items.remove(i);
    }
    
    public ArrayList<Item> listItems() {
        return items;
    }
}
