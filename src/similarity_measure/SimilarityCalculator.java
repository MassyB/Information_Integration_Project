package similarity_measure;

import com.wcohen.ss.SoftTFIDF;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import java.util.ArrayList;
import java.util.List;

/**
 * SimilarityCalculator is a class that computes the similarity between 2
 * contextual graphs
 */
public class SimilarityCalculator {

    // private constructor to make the class static
    private SimilarityCalculator() {}

    /**
     * First call function, takes 2 parameters, the 2 nodes to compare and return a list of score
     * @param e1
     * @param e2
     * @return
     */
    public static ArrayList<Double> cSimilarityRecursive(RDFNode e1, RDFNode e2) {
        List<String> visited = new ArrayList<>();
        visited.add(e1.asResource().getURI());
        ArrayList<Double> score = cSimilarityRecursive(e1, e2, visited);
        return score;

    }

    /**
     * Compute similarity, using SoftTFIDF, of 2 literals
     * @param e1
     * @param e2
     * @return
     */
    private static double similarity(Literal e1, Literal e2) {
        SoftTFIDF tfIdf = new SoftTFIDF();
        return tfIdf.score(e1.getString(), e2.getString());
    }

    /**
     * Recursive function, checking all the node of the contextual graphs e1,e2 and compare them
     * Returns a list of score
     * @param e1 contextual graph
     * @param e2 contextual graph
     * @param visited visited nodes are added in this list
     * @return
     */
    private static ArrayList<Double> cSimilarityRecursive(RDFNode e1, RDFNode e2, List<String> visited) {
        ArrayList<Double> score = new ArrayList<>();
        StmtIterator iter = ((ResourceImpl) e1).listProperties();

        // Looping over the first contextual graph
        while (iter.hasNext()){
            Statement st = iter.nextStatement();
            StmtIterator iter2 = ((ResourceImpl) e2).listProperties();
            Statement st2 = null;

            // looping over 2nd contextual graph, and search the corresponding property
            // by comparing localName "given_name" for instance
            while (iter2.hasNext()){
                st2 = iter2.nextStatement();
                if (st2.getPredicate().getLocalName().equals(st.getPredicate().getLocalName())){
                    break;
                }
            }

            // if no corresponding property, returns nothing (incompletness of data)
            if (st2 == null) { return new ArrayList<Double>(); }

            // if there was a corresponding property
            RDFNode resource1 = st.getObject();
            RDFNode resource2 = st2.getObject();

            // if they are both literal, compute similarity
            if (resource1.isLiteral() && resource2.isLiteral()) {
                score.add(similarity(resource1.asLiteral(), resource2.asLiteral()));
            }
            // recursively call again this function
            else if (resource1.isResource() && resource2.isResource()) {
                // if the node is not already visited
                if (!visited.contains(resource1.asResource().getURI())){
                    score.addAll(cSimilarityRecursive(resource1.asResource(), resource2, visited));
                    visited.add(resource1.asResource().getURI());
                }
            }
        }

        return score;
    }
}
