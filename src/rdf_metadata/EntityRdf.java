package rdf_metadata;

import java.util.ArrayList;

public class EntityRdf {
    // this class holds a subject predicate and resources
    public String subject;
    public String predicate;
    public ArrayList<String> resources;
    // constructor
    public EntityRdf (){
        this.resources = new ArrayList<>();
    }
    // add given string to resources
    public void addToResources (String str){this.resources.add(str);}
}

