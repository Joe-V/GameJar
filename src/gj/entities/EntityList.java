package gj.entities;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Used to store an ordered collection of game entities. Entities added to this
 * list are stored in ascending order of their heights, meaning the entities
 * with the lowest height values are stored at the head of the list and those
 * with the greatest height values are stored at the tail.
 * 
 * @author Joseph
 * @version 17-May-2016
 */
public class EntityList implements Iterable<Entity> {
    /**
     * The internal list of entities, which is sorted in ascending order of
     * height. This means that the entity with the LOWEST height value is at the
     * HEAD of the list.
     */
    private final List<Entity> ents;
    
    public EntityList() {
        this.ents = new ArrayList<>();
    }
    
    public boolean addEntity(Entity ent) {
        if (ents.contains(ent)) return false; // Don't continue if this entity is already present.
        else {
            // Place the entity into the list such that it remains ordered by height (insertion sort).
            for (int i = 0; i < ents.size(); i++) {
                if ((ent.getZHeight() <= ents.get(i).getZHeight()) &&
                    (!(i-1>=0) || ent.getZHeight() >= ents.get(i-1).getZHeight())) {
                    ents.add(i, ent);
                    return true;
                }
            }
            return ents.add(ent);
        }
    }
    
    public boolean removeEntity(Entity ent) {
        return ents.remove(ent);
    }
    
    @Override
    public Iterator<Entity> iterator() {
        return ents.iterator();
    }
    
    public Collection<Entity> getAllEntities() {
        return ents;
    }
    
    public Collection<Entity> getIntersectingEntities(Rectangle2D region) {
        Collection<Entity> buf = new ArrayList<>();
        for (Entity ent : ents) {
            if (region.intersects(ent.getTransform()))
                buf.add(ent);
        }
        return buf;
    }
}