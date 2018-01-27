import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import rdf_data.RDFManager;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestFileGenerator {

    /** #param 1 is the filePath to the gold standard
     *  #param 2 is the ratio, if ratio == 0.6, 60% of the sameAs links will be false
     *
     *  generate mappings that have false same as links and save them in a tsv file.
     *  In addition to that file a gold standard is also generated, it contains only the
     *  valid links of the test file.
     * */

    public static void main(String[] args) throws IOException {

        final int  filePathArg = 0;
        final int ratioArg = 1;

        String goldStandardFilePath = args[filePathArg];
        double ratio  = Double.valueOf(args[ratioArg]);
        // getting the gold standard form the rdf file
        Map<String, String> goldStandard = Utils.getSameAsLinks(goldStandardFilePath);
        // generating test data
        Map<String, String> testMapping = Utils.getTestMapping(goldStandard, ratio);
        // computing the valid links of the test data
        Map<String, String> testMappingValidLinks = Utils.getIntersection(goldStandard, testMapping);

        // saving the testMapping
        Utils.writeMappingIntoTsv(testMapping, goldStandardFilePath+"_test.tsv");
        // saving the valid entries of testMapping
        Utils.writeMappingIntoTsv(testMappingValidLinks, goldStandardFilePath+"_test_validLinks.tsv");



    }

}
