package Evaluator;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;


public class Evaluator {

    public void Main(){
        String goldstandardFileName = "data/restaurants/restaurant1_restaurant2_goldstandard.rdf";
        String inputFile = "data/restaurants/restaurant1_restaurant2_withFalseSameAsLinks.rdf";
        String resultsFileName = "data/restaurants/restaurant1_restaurant2_Results.rdf";
        evaluate(goldstandardFileName, inputFile, resultsFileName);

    }

    public Integer countCorrectLinks(Model input, Model gold){
        StmtIterator iter = input.listStatements();

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            if (object instanceof Resource) {
                    if(predicate.toString().contains("entity")) {
                        gold.

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
    }

    public void evaluate(String goldstandardName, String inputName, String resultsName){

        Integer correctSameAs = 0;
        Integer incorrectSameAs = 0;

        Model modelGold = ModelFactory.createDefaultModel();
        InputStream in1 = FileManager.get().open( goldstandardName );
        if (in1 == null) {
            throw new IllegalArgumentException( "File: " + goldstandardName + " not found");
        }
        modelGold.read(in1, "");
        Model modelInput = ModelFactory.createDefaultModel();
        InputStream in2 = FileManager.get().open( inputName );
        if (in2 == null) {
            throw new IllegalArgumentException( "File: " + inputName + " not found");
        }
        modelGold.read(in2, "");
        Model modelResults = ModelFactory.createDefaultModel();
        InputStream in3 = FileManager.get().open( resultsName );
        if (in3 == null) {
            throw new IllegalArgumentException( "File: " + resultsName + " not found");
        }
        modelResults.read(in3, "");

        Integer totalNumberOfCorrect = countCorrectLinks(modelInput, modelGold);

        StmtIterator iter = modelResults.listStatements();

        while (iter.hasNext()){
            Statement statement = iter.nextStatement();

        }






    }
}
