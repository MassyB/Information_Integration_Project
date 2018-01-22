package utils;

import rdf_data.RDFManager;
import rdf_metadata.EntityMap;
import rdf_metadata.EntityRdf;
import rdf_metadata.RDFMetadata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
            fw.write(URIentity1+"\t"+URIentity1+"\n");
        }
        fw.close();
    }



}