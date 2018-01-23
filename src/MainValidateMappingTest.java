import com.sun.org.apache.xpath.internal.operations.Mod;
import link_validation.LinkValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import rdf_data.RDFManager;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainValidateMappingTest {

    /**
     * runs a series of tests on a specific dataset
     * #param goldStandard an rdf file that contains the groundTruth mapping
     * #param propertiesToConsider
     * #param rdf1File
     * #param rdf2File
     * #param resultCSVFile a csv file where metrics will be put
     *
     * @return write a tsv named  "MappingFile"_validated
     * prints the the measures : recall precision and F1 score
     */

    // data/restaurants/restaurant1_restaurant2_goldstandard.rdf data/restaurants/properties.tsv data/restaurants/restaurant1.rdf data/restaurants/restaurant2.rdf

    private static final int _goldStandardRdfFile = 0;
    private static final int _propertiesToConsider = 1;
    private static final int _rdf1File = 2;
    private static final int _rdf2File = 3;

    public static void main(String[] args) throws IOException {

        int[] depthValues = new int[]{1, 2, 3};
        double[] thresholdValues = new double[]{0.1, 0.2, 0.3, 0.4,
                0.5, 0.6, 0.7, 0.8, 0.9};

        // use the average
        LinkValidator.Agregation agregation = LinkValidator.Agregation.AVG;

        // preparing the csv for results
        String cvsResultFile = args[_goldStandardRdfFile] + "_results.csv";
        FileWriter fw = new FileWriter(cvsResultFile);
        fw.write("\ndepth,threshold,agregation,precision,recall,f1score");

        int numberOfIetrations = 10;
        double proportionOfFalseMappings = 0.6;

        List<Double> precisions = new ArrayList<>();
        List<Double> recalls = new ArrayList<>();
        List<Double> f1scores = new ArrayList<>();

        double[] metrics = new double[3];

        // getting the inputs

        Map<String, String> goldStandard = Utils.getSameAsLinks(args[_goldStandardRdfFile]);
        Set<Property> properties = Utils.getConsideredPropertiesFromFile(args[_propertiesToConsider]);
        Model rdf1 = RDFManager.getRdfGraphFromFile(args[_rdf1File]);
        Model rdf2 = RDFManager.getRdfGraphFromFile(args[_rdf2File]);


        Map<String, String> mappingToValidate;
        Map<String, String> groundTruthTest;
        Map<String, String> validatedMappings;
        LinkValidator validator = new LinkValidator();


        for (int depth : depthValues) {
            for (double threshold : thresholdValues) {

                for (int i = 0; i < numberOfIetrations; i++) {

                    mappingToValidate = Utils.getTestMapping(goldStandard, proportionOfFalseMappings);
                    groundTruthTest = Utils.getIntersection(mappingToValidate, goldStandard);
                    validator = new LinkValidator(threshold,depth,properties);

                    // validated links
                    validatedMappings = validator.validateLinks(mappingToValidate,rdf1,rdf2);

                    // compute metrics
                    metrics = Utils.getPrecisionRecallF1(groundTruthTest, validatedMappings);

                    // save the metrics
                    precisions.add(metrics[Utils.precisionIdx]);
                    recalls.add(metrics[Utils.recallIdx]);
                    f1scores.add(metrics[Utils.f1Idx]);
                }

                // compute the average for all the previous iterations
                double precision = validator.average(precisions);
                double recall = validator.average(recalls);
                double f1score = validator.average(f1scores);

                // print this in the csv file
                fw.write("\n"+depth+","+threshold+","+agregation.name()+
                        ","+precision+","+recall+","+f1score);

                //empty all the structures
                precisions.clear();
                recalls.clear();
                f1scores.clear();
            }
        }
        fw.close();
    }

}
