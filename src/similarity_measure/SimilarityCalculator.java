package similarity_measure;

import com.wcohen.ss.SoftTFIDF;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.FileManager;
import rdf_data.RDFManager;
import utils.Utils;

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
        //StmtIterator iter2 = ((ResourceImpl) e2).listProperties();

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

            if (st2 == null) { return null; }

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
            else {
                System.err.println("SimilarityCalculator.cSimilarityRecursive : isLiteral & isResource error");
            }

        }

        return score;
    }

    public static void main(String[] args) {

        System.out.println("Arguments");
        System.out.println("----------------");
        for(String arg : args) {
            System.out.println(arg);
        }
        System.out.println("----------------");


        Map<String, String> goldMap = Utils.getSameAsLinks("data/person2/dataset21_dataset22_goldstandard_person.xml");

        Model model1 = ModelFactory.createDefaultModel();
        model1.read(FileManager.get().open("data/person2/person21.rdf"), "");

        Model model2 = ModelFactory.createDefaultModel();
        model2.read(FileManager.get().open("data/person2/person22.rdf"), "");

        for(Map.Entry<String, String> entry : goldMap.entrySet()) {
            String e2Ressource = entry.getKey();
            String e1Ressource = entry.getValue();

            Resource rs1 = model1.getResource(e1Ressource);
            Resource rs2 = model2.getResource(e2Ressource);
            Set<Property> functionalProperties = new HashSet<>();

            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#given_name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#Address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#date_of_birth"));

            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#given_name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#Address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#date_of_birth"));


            /*functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#phone_number"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#phone_number"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#street"));*/

            Model e1 = RDFManager.getContextualGraph(rs1,1, functionalProperties, model1);
            Model e2 = RDFManager.getContextualGraph(rs2,1, functionalProperties, model2);
            System.out.println("E: \n");
            printModel(e1);
            System.out.println("E2: \n");
            printModel(e2);

            System.out.println("score " + SimilarityCalculator.cSimilarityRecursive(e1.getResource(e1Ressource), e2.getResource(e2Ressource)));

            break;
        }



    }

}
