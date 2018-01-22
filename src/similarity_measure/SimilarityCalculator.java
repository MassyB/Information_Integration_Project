package similarity_measure;

import com.wcohen.ss.SoftTFIDF;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.thrift.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SimilarityCalculator {

    Model e1, e2, global;
    Double treshold = 0.3;
    Integer depth = 2;
    List<AnonId> visited = new ArrayList<>();

    public SimilarityCalculator(Model global, Model e1, Model e2) {

        this.global = global;
        this.e1 = e1;
        this.e2 = e2;

    }


    /**
     * Return false : if it is a good sameAs
     * Return true : if it is a bad sameAs
     * @return
     */
    public boolean isFalseSameAs() {

        double score = 0.0;
        Integer counter = 0;
        Resource res1 = e1.listStatements().nextStatement().getSubject();
        Resource res2 = e2.getResource(res1.getURI());
        visited.add(res1.getId());

        score = cSimilarityRecursive(res1, res2, 0, counter);

        if(score / counter >= treshold){
            //TODO Check division
            return false;
        }
        return true;
    }
    /*
    public double cSimilarity () {
        StmtIterator iter = e1.listStatements();
        Integer counter = 0;
        double score = 0.0;
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            visited.add(statement.getResource().getId());
            Resource otherResource = e2.getResource(statement.getPredicate().getURI());
            score += cSimilarityRecursive(statement.getResource(), otherResource, 0);
            counter += 1;
        }

        return score / counter;
    }*/



    public double similarity(Literal e1, Literal e2) {

        SoftTFIDF tfIdf = new SoftTFIDF();
        return tfIdf.score(e1.getString(), e2.getString());
    }

    public double cSimilarityRecursive (RDFNode e1, RDFNode e2, Integer currentDepth, Integer counter) {
        double score = 0.0;
        StmtIterator iter = e1.getModel().listStatements();
        if (currentDepth == depth - 1) {
            while (iter.hasNext()){
                Statement st = iter.nextStatement();
                Resource otherResource = e2.getModel().getResource(st.getPredicate().getURI());
                if (st.getResource().isLiteral() && otherResource.isLiteral()){
                    score += similarity(st.getLiteral(), otherResource.asLiteral());
                    counter += 1;
                }
            }
        }
        else {
            while (iter.hasNext()){
                Statement st = iter.nextStatement();
                Resource otherResource = e2.getModel().getResource(st.getPredicate().getURI());
                if (st.getResource().isLiteral() && otherResource.isLiteral()){
                    score += similarity(st.getLiteral(), otherResource.asLiteral());
                    counter += 1;
                }
                else {
                    if (!visited.contains(st.getResource().getId())){
                        score += cSimilarityRecursive(st.getResource(), otherResource, currentDepth+1, counter);
                        visited.add(st.getResource().getId());
                    }
                }
            }

        }
        return score;
    }
}
