package link_validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.util.List;
import java.util.Map;

public class LinkValidator {

    public enum Agregation { AVG, MAX, MIN }
    private double threshold;
    private Agregation agregationFunction;
    int depth;

    public LinkValidator(double threshold, int depth, Agregation agregationFunction){
        this.threshold = threshold;
        this.depth = depth;
        this.agregationFunction  = agregationFunction;
    }

    public Map<String, String> validateLinks(Map<String, String> mappingToValidate,
                                             Model rdf1, Model rdf2, List<Property> propertiesToConsider){

        //TODO implement
        return null;
    }

    public Map<String, String> validateLinks(Map<String, String> mappingToValidate,
                                             Model rdf1, Model rdf2, List<Property> propertiesToConsider,
                                             Map<String, String> propertiesMapping){
        //TODO implment
        return null;
    }

    public boolean isValidLink(String entity1URI, String entityURI2, Model rdf1, Model rdf2){
        //TODO implement
        return false;
    }



}
