package rdf_data;

import org.apache.jena.rdf.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RDFManager {

    /**
     *  @param resource the rdf resource used to compute the Contextual graph
     *  @param depth the maximal depth to reach during construction of the contextual graph
     *  @param propertiesToConsider set containing URI of properties to explore during construction
     *                              of contextual graph
     *  @param originalGraph the graph containing the contextual graph
     *
     *  @return a Jena Model object containing the contextual graph of resource
     *
     *  this method computes the contextual graph of a node given a set of properties to consider
     *  and a maximal depth to reach.
     *
     * */

    public static Model getContextualGraph(Resource resource, int depth,
                                           Set<Property> propertiesToConsider, Model originalGraph) {


        Set<Statement> statements = getContextualGraph(originalGraph, resource, new HashSet<RDFNode>(),
                propertiesToConsider, 0, depth, new boolean[1], new boolean[1]);

        Model contextualGraph = ModelFactory.createDefaultModel();
        contextualGraph.add(new ArrayList<>(statements));
        return contextualGraph;

    }

    /**
     *  @param model the graph containing the contextual graph
     *  @param resource the rdf resource used to compute the Contextual graph
     *  @param visited
     *  @param propertiesToConsider set containing URI of properties to explore during construction
     *                              of contextual graph
     *  @param reachedDepth the current depth
     *  @param depth the maximal depth to reach during construction of the contextual graph
     *  @param removeLastEntityFalg this flag is set when an entity needs to be removed for
     *                              the contextual graph
     *  @param supressStmtWithObject used to triger a flag and rid of the paths that don't end with a literal
     *
     *  @return a set of statements reachable from the resource
     *
     *  This function is a recursive one, each call returns a set of statements reachable from
     *  the current resource given that the maximal depth is not reached and only properties of
     *  propertiesTocConsider are used.
     *
     * */

    private static Set<Statement> getContextualGraph(Model model, Resource resource, Set<RDFNode> visited,
                                                    Set<Property> propertiesToConsider, int reachedDepth, int depth,
                                                    boolean[] removeLastEntityFalg, boolean[] supressStmtWithObject) {

        Set<Statement> contextualGraph = new HashSet<>();
        if (reachedDepth >= depth){
            supressStmtWithObject[0] = true ;
            return contextualGraph;
        }
        if (visited.contains(resource)) return contextualGraph;


        removeLastEntityFalg[0] = false;
        supressStmtWithObject[0] = false;
        visited.add(resource);

        // reading the resource
        StmtIterator iterator = resource.listProperties();

        // for dealing with non literal objects in the contextual graph
        int nbObjects = 0;
        int nbFlagedResourceObject = 0;

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            Property    p = stmt.getPredicate();
            // if the contextual graph considers this property go deeper
            if (propertiesToConsider.contains(p)) {
                RDFNode object = stmt.getObject();
                nbObjects++;

                if (object.isLiteral()) {
                    contextualGraph.add(stmt);
                } else if (object.isResource()) {

                    // do the recursion
                    contextualGraph.add(stmt);
                    Set<Statement> subContextualGraph = getContextualGraph(model, (Resource) object, visited,
                            propertiesToConsider, reachedDepth+1,
                            depth, removeLastEntityFalg,
                            supressStmtWithObject);
                    if (removeLastEntityFalg[0]) {

                        // inc the number of flaged entities
                        nbFlagedResourceObject++;
                        removeLastEntityFalg[0] = false;
                    }

                    if (supressStmtWithObject[0]) {

                        contextualGraph.remove(stmt);
                        supressStmtWithObject[0] = false;
                    }


                    contextualGraph.addAll(subContextualGraph);

                }
            }
        }

        // triger a flag get rid of the paths that don't end with a literal
        if(nbFlagedResourceObject == nbObjects){
            supressStmtWithObject[0] = true;
        }

        return contextualGraph;
    }

    /**
     *  @param filePath path to the rdf file containing the RDF graph
     *
     *  @return reads an rdf graph from a file and returns model object
     *          as defined in Jena
     *
     * */

    public static Model getRdfGraphFromFile(String filePath) throws FileNotFoundException {

        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = new FileInputStream(filePath);
        model.read(in,null);

        return model;
    }
}
