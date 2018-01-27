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

public class Test {

    public static void main(String[] args) throws IOException {

        Model model = RDFManager.getRdfGraphFromFile("data/person1/person11.rdf");

        Map<String, String> gold_standard = Utils.readTsvFile("data/restaurants/restaurant1_restaurant2_goldstandard.rdf");
        Map<String, String> test_standard = Utils.getTestMapping(gold_standard,06);
        Utils.writeMappingIntoTsv(test_standard, "rastaurant_test_data.tsv");


    }

}
