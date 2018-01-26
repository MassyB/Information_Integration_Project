package utils;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import rdf_metadata.EntityMap;
import rdf_metadata.EntityRdf;
import rdf_metadata.RDFMetadata;

import java.io.*;
import java.util.*;

public class Utils {

   public final static int precisionIdx= 0;
   public final static int recallIdx = 1;
   public final static int f1Idx = 2;

    /***
     *  @param filePath file containing the .xml Or .rdf file representing the sameAs links
     *
     *   @return mapping of same links
     *
     *   reads the file using Jena library and returns SameAs links as a map of string to string
     *   which is much convenient to handle than a regular Jena Model object
     *
     */
    public static Map<String, String> getSameAsLinks(String filePath){

        RDFMetadata rdfMetadata = new RDFMetadata();
        Map<String, String> sameAsMapping = new HashMap<>();

        EntityMap entityMap = rdfMetadata.getEntityMap(filePath);
        for(EntityRdf entityRdf: entityMap.map.values()){
            ArrayList<String> mapping = entityRdf.resources;
            sameAsMapping.put(mapping.get(1), mapping.get(0));
        }

        return sameAsMapping;
    }

    /**
     *  @param mapping sameAs links represented as a map of string to string
     *  @param filePath the file path of the TSV to put the mappings in
     *
     *  write the mapping into a TSV file (for test purposes for example)
     *
     * */
    public static void writeMappingIntoTsv(Map<String, String> mapping, String filePath) throws IOException {

        FileWriter fw = new FileWriter(filePath);
        for(String URIentity1 : mapping.keySet()){
            String URIentity2 = mapping.get(URIentity1);
            fw.write(URIentity1+"\t"+URIentity2+"\n");
        }
        fw.close();
    }

    /**
     *  for debugging purposes, just print the graph on the screen
     * */
    public static void printModel(Model model){
        model.write(System.out);
    }

    /**
     * @param filePath change the extension fo the file
     *
     */
    public static String changeExtenstionToTsv(String filePath){
        return filePath.replace(".rdf",".tsv");
    }
    /**
     *  @param filepath file containing SameAs mappings
     *
     *  @return mapping (string to string) modeling sameAs links
     *
     *  this method read a tsv file containing the sameAs links and convert it to a map
     *  which is easier to handle.
     *
     * */
    public static Map<String, String> readTsvFile(String filepath) throws IOException {
        Map<String, String> mappings= new HashMap<>();
        // Open the file
        FileInputStream fstream = new FileInputStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            String[] splits = strLine.split("\t");
            if(splits.length == 2){
                mappings.put(splits[0], splits[1]);
            }
        }
        //Close the input stream
        br.close();

        return mappings;
    }

    /**
     *  @param filepath file provieded by the domain expert. It contains properties to consider
     *                  to compute the contextual graph hence the similarity.
     *  @return a set of Jena Property objects
     * */
    public static Set<Property> getConsideredPropertiesFromFile(String filepath) throws IOException {

        Set<Property> properties = new HashSet<>();
        String strLine;

        FileInputStream fstream = new FileInputStream(filepath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null){
            Property p = ResourceFactory.createProperty(strLine);
            //maybe add if p.isProperty() then add
            properties.add(p);
        }
        br.close();
        return properties;
    }

    /**
     * @param goldStandart mapping containing the valid sameAs links
     * @param ratio number between 0 and 1 to control how much of the data is going to be
     *              modified.
     *
     * this method is meant to produce test data, by faking some entries of the goldStandard
     * ratio parameter is used to control how much of the data is going to be faked. if ratio is
     * 0.6 this means that 60% of the links are going to be not valid links
     * */
    public static Map<String, String> getTestMapping(Map<String, String> goldStandart, double ratio){
        Map<String, String> testMapping = new HashMap<>();
        List<String> entitiesFromKb2 = new ArrayList<>(goldStandart.values());
        Random random = new Random();
        int nbEntities = entitiesFromKb2.size();
        for(String entityURI1 : goldStandart.keySet()){
            String goodMapping = goldStandart.get(entityURI1);
            // flip a coin and see if the entry has to change
            if(random.nextFloat() < ratio){
                // change the entry
                String falseMapping;

                while(! (falseMapping = entitiesFromKb2.get(random.nextInt(nbEntities))).equals(goodMapping)){
                    testMapping.put(entityURI1, falseMapping);
                }
            }
            else {
                testMapping.put(entityURI1, goodMapping);
            }
        }
        return testMapping;
    }

    /**
     *  @param map1
     *  @param map2
     *
     *  @return intersection of map1 and map2
     *
     *  method computing the intersection of two mapps. returning only common entries of both
     * */

    public static Map<String, String> getIntersection(Map<String, String> map1, Map<String, String> map2){

        Map<String, String> intersection = new HashMap<>();

        for(String k1: map1.keySet()){
            String v1 = map1.get(k1);
            if(map2.containsKey(k1) && map2.get(k1).equals(v1)){
                intersection.put(k1,v1);
            }
        }

        return intersection;
    }

    /**
     *  @param goldStandard ground truth
     *  @param validationOutput result of the validation
     *
     *  @return an array of doubles containing precision, recall and F1 score
     *
     *  computes metrics : precision, recall and F1 score
     *
     * */

    public static double[] getPrecisionRecallF1(Map<String, String> goldStandard, Map<String,String> validationOutput ){

        int goldStandardSize = goldStandard.size();
        int validationOutputSize = validationOutput.size();
        int nbCommonEntries = 0;

        for(String entity :validationOutput.keySet()){

            if(goldStandard.containsKey(entity) &&
                    goldStandard.get(entity).equals(validationOutput.get(entity))){
                nbCommonEntries ++;
            }
        }

        double[] results = new double[3];

        results[precisionIdx] = nbCommonEntries / (1.0 * validationOutputSize);
        results[recallIdx] = nbCommonEntries / (1.0 * goldStandardSize);
        results[f1Idx] = 2 * (results[precisionIdx] * results[recallIdx]) / (results[precisionIdx] + results[recallIdx]);
        return results;
    }

}