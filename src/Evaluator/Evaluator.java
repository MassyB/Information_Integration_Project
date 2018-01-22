package Evaluator;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import rdf_metadata.*;

import java.io.InputStream;



public class Evaluator {

    public static void main(String[] args){
        String goldstandardFileName = "data/restaurants/restaurant1_restaurant2_goldstandard.rdf";
        String inputFile = "data/restaurants/restaurant1_restaurant2_withFalseSameAsLinks.rdf";
        String resultsFileName = "data/restaurants/restaurant1_restaurant2_Results.rdf";


        RDFMetadata rm = new RDFMetadata(); EntityMap goldMap = rm.getEntityMap(goldstandardFileName);
        evaluate(goldstandardFileName, inputFile, resultsFileName);
    }

    public static Integer countCorrectLinks(Model input, Model gold){

        return 0;
    }

    public static void evaluate(String goldstandardName, String inputName, String resultsName){

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
