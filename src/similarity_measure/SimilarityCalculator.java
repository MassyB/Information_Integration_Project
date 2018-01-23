package similarity_measure;

import com.wcohen.ss.SoftTFIDF;
import link_validation.LinkValidator;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.FileManager;
import rdf_data.RDFManager;
import utils.Utils;

import java.io.IOException;
import java.util.*;

import static utils.Utils.printModel;


public final class SimilarityCalculator {

    // private constructor to make the class static
    private SimilarityCalculator() {}

    /**
     * Return false : if it is a good sameAs
     * Return true : if it is a bad sameAs
     * @return
     */
    /*public static boolean isFalseSameAs(Model e1, Model e2, String e1Ressource, String e2Ressource, int depth, double treshold, String aggregationFunc) {

        ArrayList<Double> score = new ArrayList<>();
        Resource res1 = e1.getResource(e1Ressource);
        Resource res2 = e2.getResource(e2Ressource);

        List<String> visited = new ArrayList<>();
        visited.add(res1.getURI());

        score.addAll(SimilarityCalculator.cSimilarityRecursive(res1, res2, depth,0, visited));

        if( / score.size() >= treshold){
            //TODO Check division"http://www.okkam.org/ontology_person2.owl#has_address"
            return false;
        }
        return true;
    }*/

    public static ArrayList<Double> cSimilarityRecursive(RDFNode e1, RDFNode e2) {
        List<String> visited = new ArrayList<>();
        visited.add(e1.asResource().getURI());
        ArrayList<Double> score = cSimilarityRecursive(e1, e2, 0, visited);
        return score;

    }

    private static double similarity(Literal e1, Literal e2) {
        SoftTFIDF tfIdf = new SoftTFIDF();
        return tfIdf.score(e1.getString(), e2.getString());
    }

    private static ArrayList<Double> cSimilarityRecursive(RDFNode e1, RDFNode e2, Integer currentDepth, List<String> visited) {
        ArrayList<Double> score = new ArrayList<>();
        StmtIterator iter = ((ResourceImpl) e1).listProperties();

        while (iter.hasNext()){
            Statement st = iter.nextStatement();
            StmtIterator iter2 = ((ResourceImpl) e2).listProperties();
            Statement st2 = null;
            while (iter2.hasNext()){
                st2 = iter2.nextStatement();
                if (st2.getPredicate().getLocalName().equals(st.getPredicate().getLocalName())){
                    break;
                }
            }

            if (st2 == null) { return new ArrayList<Double>(); }

            RDFNode resource1 = st.getObject();
            RDFNode resource2 = st2.getObject();


            if (resource1.isLiteral() && resource2.isLiteral()) {
                score.add(similarity(resource1.asLiteral(), resource2.asLiteral()));
            }
            else if (resource1.isResource() && resource2.isResource()) {
                if (!visited.contains(resource1.asResource().getURI())){

                    score.addAll(cSimilarityRecursive(resource1.asResource(), resource2,currentDepth+1, visited));
                    visited.add(resource1.asResource().getURI());
                }
            }


        }

        return score;
    }

    /*public static void magic(String goldStandardPath, String testFilePath, String rdf1Path, String rdf2Path,
                             int depth, double treshold, String propertiesPath, LinkValidator.Agregation aggregationFunc, String outputFilePath) throws IOException {

        Model model1 = ModelFactory.createDefaultModel();
        model1.read(FileManager.get().open(rdf1Path), "");

        Model model2 = ModelFactory.createDefaultModel();
        model2.read(FileManager.get().open(rdf2Path), "");

        Map<String, String> goldMap = Utils.getSameAsLinks(goldStandardPath);
        Map<String, String> testMap = Utils.getSameAsLinks(testFilePath);

        Set<Property> functionalProperties = Utils.getConsideredPropertiesFromFile(propertiesPath);


        for(Map.Entry<String, String> entry : testMap.entrySet()) {
            String e2Ressource = entry.getKey();
            String e1Ressource = entry.getValue();

            Resource rs1 = model1.getResource(e1Ressource);
            Resource rs2 = model2.getResource(e2Ressource);

            Model e1 = RDFManager.getContextualGraph(rs1, depth, new LinkedList<>(functionalProperties), model1);
            Model e2 = RDFManager.getContextualGraph(rs2, depth, new LinkedList<>(functionalProperties), model2);

            System.out.println("score " + SimilarityCalculator.cSimilarityRecursive(e1.getResource(e1Ressource), e2.getResource(e2Ressource)));
        }

    }*/

    /*public static void main(String[] args) {

        System.out.println("Arguments");
        System.out.println("----------------");
        for(String arg : args) {
            System.out.println(arg);
        }
        System.out.println("----------------");

        String goldStandardPath, testFilePath, rdf1Path, rdf2Path, propertiesPath, aggregationFunc, outputFilePath;

        goldStandardPath = "data/person2/dataset21_dataset22_goldstandard_person.xml";
        testFilePath = "data/restaurants/restaurant1_restaurant2_withFalseSameAsLinks.rdf";
        rdf1Path = "data/person2/person21.rdf";
        rdf2Path = "data/person2/person22.rdf";
        outputFilePath = "data/output.tsv";
        aggregationFunc = "AVG";

        LinkValidator.Agregation agregation = LinkValidator.Agregation.AVG;

        switch (aggregationFunc.toLowerCase()) {
            case "avg":
                agregation = LinkValidator.Agregation.AVG;
                break;
            case "max":
                agregation = LinkValidator.Agregation.MAX;
                break;
            case "min":
                agregation = LinkValidator.Agregation.MIN;
                break;
        }

        int depth = 2;
        double treshold = 0.2;

        //magic(goldStandardPath);

        //
        //
        // "data/person2/person22.rdf"

    }*/

}
