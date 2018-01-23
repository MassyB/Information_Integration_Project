package similarity_measure;

import com.wcohen.ss.SoftTFIDF;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileManager;
import rdf_data.RDFManager;
import utils.Utils;

import java.util.*;


public class SimilarityCalculator {

    Model e1, e2;
    String e1Ressource, e2Ressource;
    Double treshold = 0.3;
    Integer depth = 2;
    List<String> visited = new ArrayList<>();

    public SimilarityCalculator(Model e1, Model e2, String e1Ressource, String e2Ressource) {

        this.e1 = e1;
        this.e2 = e2;
        this.e1Ressource = e1Ressource;
        this.e2Ressource = e2Ressource;

    }


    /**
     * Return false : if it is a good sameAs
     * Return true : if it is a bad sameAs
     * @return
     */
    public boolean isFalseSameAs() {

        double score = 0.0;
        ArrayList<Integer> counter = new ArrayList<>();
        Resource res1 = e1.getResource(e1Ressource);
        Resource res2 = e2.getResource(e2Ressource);
        visited.add(res1.getURI());

        score = cSimilarityRecursive(res1, res2, 0, counter);

        if(score / counter.size() >= treshold){
            //TODO Check division"http://www.okkam.org/ontology_person2.owl#has_address"
            return false;
        }
        return true;
    }
    /*
    public double cSimilarity () {
        StmtIterator iter = e1.listStatements();
        Integer counter = 0;
        double score = 0.0;
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            visited.add(statement.getResource().getId());
            Resource otherResource = e2.getResource(statement.getPredicate().getURI());
            score += cSimilarityRecursive(statement.getResource(), otherResource, 0);
            counter += 1;
        }

        return score / counter;
    }*/



    public double similarity(Literal e1, Literal e2) {

        SoftTFIDF tfIdf = new SoftTFIDF();
        return tfIdf.score(e1.getString(), e2.getString());
    }

    public double cSimilarityRecursive (RDFNode e1, RDFNode e2, Integer currentDepth, ArrayList<Integer> counter) {
        double score = 0.0;
        StmtIterator iter = e1.getModel().listStatements();
        StmtIterator iter2 = e2.getModel().listStatements();
        if (currentDepth == depth - 1) {
            while (iter.hasNext()){
                Statement st = iter.nextStatement();
                iter2 = e2.getModel().listStatements();
                Statement st2 = null;
                while (iter2.hasNext()){
                    st2 = iter2.nextStatement();
                    if (st2.getPredicate().getLocalName().equals(st.getPredicate().getLocalName())){
                        break;
                    }
                }

                if (st2 == null){
                    return 0.0;
                }


                RDFNode resource1 = st.getObject();
                // RDFNode resource22 = st2.getProperty(e2Property).getObject();

                RDFNode resource2 = st2.getObject();


                if (resource1.isLiteral() && resource2.isLiteral()) {
                    score += similarity(resource1.asLiteral(), resource2.asLiteral());
                    counter.add(1);
                }

                // st.getLiteral().getString()
                // e2.asResource().getProperty(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#phone_number"))
                //String nameSpace = e2.asResource().getNameSpace();
                //Property e2Property = e2.getModel().getProperty(e2.getModel().listStatements().nextStatement().getPredicate().getNameSpace(),st.getPredicate().getLocalName());
                //Property e1Property = e1.getModel().getProperty(e1.getModel().listStatements().nextStatement().getPredicate().getNameSpace(),st.getPredicate().getLocalName());

                // e2.getModel().getResource("http://www.okkam.org/ontology_restaurant2.owl#name").
            }
        }
        else {
            while (iter.hasNext()){
                Statement st = iter.nextStatement();
                iter2 = e2.getModel().listStatements();
                Statement st2 = null;
                while (iter2.hasNext()){
                    st2 = iter2.nextStatement();
                    if (st2.getPredicate().getLocalName().equals(st.getPredicate().getLocalName())){
                        break;
                    }
                }

                if (st2 == null){
                    return 0.0;
                }

                // st.getLiteral().getString()
                // e2.asResource().getProperty(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#phone_number"))
               // String nameSpace = e2.asResource().getNameSpace();
//                Property e2Property = e2.getModel().getProperty(e2.getModel().listStatements().nextStatement().getPredicate().getNameSpace(),st.getPredicate().getLocalName());
                // Property e1Property = e1.getModel().getProperty(e1.getModel().listStatements().nextStatement().getPredicate().getNameSpace(),st.getPredicate().getLocalName());



                //((ResourceImpl) e2).getProperty(e2Property).getObject();

                // e2.getModel().getResource("http://www.okkam.org/ontology_restaurant2.owl#name").

                RDFNode resource1 = st.getObject();
                // RDFNode resource22 = st2.getProperty(e2Property).getObject();

                RDFNode resource2 = st2.getObject();


                if (resource1.isLiteral() && resource2.isLiteral()) {
                    score += similarity(resource1.asLiteral(), resource2.asLiteral());
                    counter.add(1);
                }
                else if (resource1.isResource() && resource2.isResource()) {
                    if (!visited.contains(resource1.asResource().getURI())){
                        score += cSimilarityRecursive(resource1.asResource(), resource2, currentDepth+1, counter);
                        visited.add(resource1.asResource().getURI());
                        counter.add(1);
                    }
                }
                else {
                    System.err.println("DK exception");
                }

            }

        }
        return score;
    }

    public static void main(String[] args) {

        Map<String, String> goldMap = Utils.getSameAsLinks("data/restaurants/restaurant1_restaurant2_goldstandard.rdf");



        Model model1 = ModelFactory.createDefaultModel();
        model1.read(FileManager.get().open("data/restaurants/restaurant1.rdf"), "");

        Model model2 = ModelFactory.createDefaultModel();
        model2.read(FileManager.get().open("data/restaurants/restaurant2.rdf"), "");

        for(Map.Entry<String, String> entry : goldMap.entrySet()) {
            String e2Ressource = entry.getKey();
            String e1Ressource = entry.getValue();

            Resource rs1 = model1.getResource(e1Ressource);
            Resource rs2 = model2.getResource(e2Ressource);
            Set<Property> functionalProperties = new HashSet<>();

            /*functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#given_name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#Address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person1.owl#date_of_birth"));

            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#given_name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#Address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_person2.owl#date_of_birth"));*/


            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#phone_number"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant1.owl#street"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#phone_number"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#name"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#has_address"));
            functionalProperties.add(new PropertyImpl("http://www.okkam.org/ontology_restaurant2.owl#street"));

            Model e1 = RDFManager.getContextualGraph(rs1,1, functionalProperties, model1);
            Model e2 = RDFManager.getContextualGraph(rs2,2, functionalProperties, model2);

            Utils.printModel(e1);
            System.out.println("________");
            Utils.printModel(e2);



            SimilarityCalculator sc = new SimilarityCalculator(e1, e2, e1Ressource, e2Ressource);
            if(sc.isFalseSameAs()) {
                System.out.println("Not good");
            }
            else {
                System.out.println("Good => output");
            }

            // TODO remove
            //break;
        }



    }

}
