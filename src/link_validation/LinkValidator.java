package link_validation;

import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import org.apache.jena.ext.com.google.common.primitives.Doubles;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import rdf_data.RDFManager;
import similarity_measure.SimilarityCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkValidator {

    public enum Agregation { AVG, MAX, MIN }
    private double threshold;
    private Agregation agregationFunction;
    int depth;
    Set<Property> propertiesToConsider;

    public LinkValidator(double threshold, int depth, Agregation agregationFunction,
                         Set<Property> properties){

        this.threshold = threshold;
        this.depth = depth;
        this.agregationFunction  = agregationFunction;
        this.propertiesToConsider = properties;
    }

    public LinkValidator(){

    }

    // a default constructor that uses the average for agregating
    public LinkValidator(double threshold, int depth, Set<Property> properties){

        this.threshold = threshold;
        this.depth = depth;
        this.agregationFunction  = Agregation.AVG;
        this.propertiesToConsider = properties;
    }

    public Map<String, String> validateLinks(Map<String, String> mappingToValidate,
                                             Model rdf1, Model rdf2){

        Map<String, String> validMappings = new HashMap<>();

        for(String entity1URI: mappingToValidate.keySet()){
            String entity2URI = mappingToValidate.get(entity1URI);

            // getting the resources form the rdf files
            Resource entity1 = rdf1.getResource(entity1URI);
            Resource entity2 = rdf2.getResource(entity2URI);

            // computing the contextual graphs for both
            Model contextualGraph1 = RDFManager.getContextualGraph(entity1, depth,
                                                propertiesToConsider, rdf1);

            Model contextualGraph2 = RDFManager.getContextualGraph(entity2, depth,
                                                propertiesToConsider, rdf2);

            //getting the resources to the contextual graph
            entity1 = contextualGraph1.getResource(entity1URI);
            entity2 = contextualGraph2.getResource(entity2URI);

            // see if it's valid
            if(isValidLink(entity1, entity2)){
                validMappings.put(entity1URI, entity2URI);
            }
        }
        return validMappings;
    }

    /**
     * Check if a link is valid or not
     * @param entity1
     * @param entity2
     * @return true if it is, false otherwise
     */
    public boolean isValidLink(RDFNode entity1, RDFNode entity2){
        ArrayList<Double> similarities = SimilarityCalculator.cSimilarityRecursive(entity1, entity2);

        // we did not find any match
        if(similarities.isEmpty())
            return false;

        switch (this.agregationFunction) {
            case AVG:
                return average(similarities) > threshold;
            case MAX:
                return max(similarities) > threshold;
            case MIN:
                return min(similarities) > threshold;
        }

        return false;
    }

    /**
     * Returns average of a list
     * @param similarities
     * @return
     */
    private double average(List<Double> similarities){
        return similarities.stream().mapToDouble(value -> value).average().getAsDouble();
    }

    /**
     * Returns min of a list
     * @param similarities
     * @return
     */
    private double min(List<Double> similarities){
        return similarities.stream().mapToDouble(value -> value).min().getAsDouble();
    }

    /**
     * Returns max of a list
     * @param similarities
     * @return
     */
    private double max(List<Double> similarities){
        return similarities.stream().mapToDouble(value -> value).max().getAsDouble();
    }

}
