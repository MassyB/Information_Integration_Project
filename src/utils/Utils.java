package utils;

import org.apache.jena.rdf.model.Property;
import rdf_data.RDFManager;
import rdf_metadata.EntityMap;
import rdf_metadata.EntityRdf;
import rdf_metadata.RDFMetadata;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Map<String, String> getSameAsLinks(String filePath){

        RDFMetadata rdfMetadata = new RDFMetadata();
        Map<String, String> sameAsMapping = new HashMap<>();

        EntityMap entityMap = rdfMetadata.getEntityMap(filePath);
        for(EntityRdf entityRdf: entityMap.map.values()){
            ArrayList<String> mapping = entityRdf.resources;
            sameAsMapping.put(mapping.get(0), mapping.get(1));
        }

        return sameAsMapping;
    }

    public static void wirteMappingIntoTsv(Map<String, String> mapping, String filePath) throws IOException {

        FileWriter fw = new FileWriter(filePath);
        for(String URIentity1 : mapping.keySet()){
            String URIentity2 = mapping.get(URIentity1);
            fw.write(URIentity1+"\t"+URIentity2+"\n");
        }
        fw.close();
    }

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

    public static List<Property> getConsideredPropertiesFromFile(String filepath){

        List<Property> properties = new ArrayList<>();

        //TODO magic here

        return properties;
    }


}