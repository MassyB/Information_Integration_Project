package rdf_metadata;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.*;


public class RDFMetadata {

    public EntityMap getEntityMap(String inputFileName) {

        EntityMap map = new EntityMap();
        // or maybe for String[] inputs

        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }

        model.read(in, "");
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            if (object instanceof Resource) {
                if (map.inMap(subject.toString()))
                    if(predicate.toString().contains("entity")) {
                        EntityRdf entity = map.getRdf(subject.toString());
                        entity.addToResources(object.toString());
                    }
            }
            if (map.inMap(subject.toString())){
                EntityRdf entity = map.getRdf(subject.toString());

                if (object.toString().equals("="))
                    entity.predicate = "=";
            }
            else {
                EntityRdf entity = new EntityRdf();
                entity.subject = subject.toString();
                map.addToMap(subject.toString(), entity);
            }
        }
        map.removeEmptyCells();
        return map;
    }
    
}
