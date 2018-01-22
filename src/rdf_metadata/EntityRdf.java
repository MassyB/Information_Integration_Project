package rdf_metadata;

import java.util.ArrayList;

public class EntityRdf {
    public String subject;
    public String predicate;
    public ArrayList<String> resources;

    public EntityRdf (){
        this.resources = new ArrayList<>();
    }
    public void addToResources (String str){this.resources.add(str);}
}

