package rdf_metadata;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.*;


public class RDFMetadata {

    public EntityMap getEntityMap(String inputFileName) {

        EntityMap map = new EntityMap();
        // this is a data structure created to read rdf and xml files and put it into memory
        // so that later on we can use it

        // create the model
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }

        model.read(in, "");
        StmtIterator iter = model.listStatements();
        // for every element
        while (iter.hasNext()) {
            //get current elements
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            //if the object is a instance of resource
            if (object instanceof Resource) {
                if (map.inMap(subject.toString()))
                    if(predicate.toString().contains("entity")) {
                        // get the entity by subject
                        EntityRdf entity = map.getRdf(subject.toString());
                        entity.addToResources(object.toString());
                    }
            }
            // if the subject is in map
            if (map.inMap(subject.toString())){
                EntityRdf entity = map.getRdf(subject.toString());

                if (object.toString().equals("="))
                    entity.predicate = "=";
            }
            else {
                // add to the map
                EntityRdf entity = new EntityRdf();
                entity.subject = subject.toString();
                map.addToMap(subject.toString(), entity);
            }
        }
        //checking if the dataset contains empty cells.
        map.removeEmptyCells();
        return map;
    }
    
}
