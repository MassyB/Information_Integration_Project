package rdf_metadata;

import java.util.HashMap;
import java.util.Iterator;

public class EntityMap {

    public HashMap<String,EntityRdf> map ;
    // constructor
    public EntityMap(){
        map = new HashMap<String,EntityRdf>();
    }
    //checking if the subject is in map
    public boolean inMap(String subject){
        if (this.map.containsKey(subject))
            return true;
        return false;
    }
    // get rdf by a given subject
    public EntityRdf getRdf(String subject){
        return this.map.get(subject.toString());
    }
    // add to the map
    public void addToMap(String subject, EntityRdf entity){
        this.map.put(subject, entity);
    }
    // remove empty cells
    public void removeEmptyCells(){
        Iterator it = this.map.keySet().iterator();
        while (it.hasNext()){
            String str = (String) it.next();
            if(this.map.get(str).resources.size() != 2){
                it.remove();
            }
        }

    }

}
