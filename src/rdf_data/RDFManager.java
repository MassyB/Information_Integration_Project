package rdf_data;

import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RDFManager {


    public static Model getContextualGraph(Resource resource, int depth,
                                           Set<Property> propertiesToConsider, Model originalGraph) {


        Set<Statement> statements = getContextualGraph(originalGraph, resource, new HashSet<RDFNode>(),
                propertiesToConsider, 0, depth, new boolean[1], new boolean[1]);

        Model contextualGraph = ModelFactory.createDefaultModel();
        contextualGraph.add(new ArrayList<>(statements));
        return contextualGraph;

    }

    public static Set<Statement> getContextualGraph(Model model, Resource resource, Set<RDFNode> visited,
                                                    Set<Property> propertiesToConsider, int reachedDepth, int depth,
                                                    boolean[] removeLastEntityFalg, boolean[] supressStmtWithObject) {

        Set<Statement> contextualGraph = new HashSet<>();
        if (reachedDepth > depth) return contextualGraph;
        if (visited.contains(resource)) return contextualGraph;
        reachedDepth++;

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
            Property p = stmt.getPredicate();
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
                            propertiesToConsider, reachedDepth,
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

}
