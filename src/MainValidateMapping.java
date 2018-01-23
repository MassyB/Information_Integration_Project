import link_validation.LinkValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import rdf_data.RDFManager;
import utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validate a single mapping
 *
 * */

public class MainValidateMapping {

    /**
     * #param MappingFile a tsv (tab separated value) file containing the mapping to validate
     * #param goldStandard a tsv file containing the valid mappings (the ground truth)
     * #param rdf1File
     * #param rdf2File
     * #param propertiesToConsiderFile
     * #param depth
     * #param threshold
     *
     * @return write a tsv named  "MappingFile"_validated
     *         prints the the measures : recall precision and F1 score
     *
     *
     *
     *
     * Example of inputs:
     * data/restaurants/test0.5.tsv data/restaurants/gold_mapping.tsv data/restaurants/restaurant1.rdf data/restaurants/restaurant2.rdf data/restaurants/properties.tsv 3 0.4
     */

    private static final int _MappingFile = 0;
    private static final int _goldStandardFile = 1;
    private static final int _rdf1File = 2;
    private static final int _rdf2File = 3;
    private static final int _propertiesToConsiderFile = 4;
    private static final int _depth = 5;
    private static final int _threshold = 6;

    // data/restaurants/test0.5.tsv data/restaurants/restaurant1_restaurant2_goldstandard.rdf data/restaurants/restaurant1.rdf data/restaurants/restaurant2.rdf data/properties.txt 3 0.4

    public static void main(String[] args) throws IOException {

        if(args.length != 7){
            // the programs needs the right inputs
            throw new IllegalArgumentException();
        }
        Map<String, String> mappingToValidate = Utils.readTsvFile(args[_MappingFile]);
        Map<String, String> groundTruthMapping = Utils.readTsvFile(args[_goldStandardFile]);
        Model rdf1 = RDFManager.getRdfGraphFromFile(args[_rdf1File]);
        Model rdf2 = RDFManager.getRdfGraphFromFile(args[_rdf2File]);
        Set<Property> propertiesToConsider = Utils.getConsideredPropertiesFromFile(args[_propertiesToConsiderFile]);
        int depth = Integer.valueOf(args[_depth]);
        double threshold = Double.valueOf(args[_threshold]);

        // instanciate the validator
        LinkValidator validator = new LinkValidator(threshold, depth, propertiesToConsider);

        // validate the links
        Map<String, String> validatedMappings = validator.validateLinks(mappingToValidate, rdf1, rdf2);

        // write the results
        Utils.writeMappingIntoTsv(validatedMappings, args[_MappingFile]+"_validated.tsv");

        groundTruthMapping = Utils.getIntersection(groundTruthMapping, mappingToValidate);
        // compute the metrics : precision, recall, and F1 score
        double[] metrics = Utils.getPrecisionRecallF1(groundTruthMapping, validatedMappings);

        // print them
        System.out.println("Precision: " + metrics[Utils.precisionIdx]+
                           "\nRecall: " + metrics[Utils.recallIdx]+
                           "\nF1 Score: "+ metrics[Utils.f1Idx]);


    }
}
