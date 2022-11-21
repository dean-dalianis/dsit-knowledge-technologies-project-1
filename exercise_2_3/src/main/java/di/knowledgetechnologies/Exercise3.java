package di.knowledgetechnologies;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

public class Exercise3 {
  private static final Logger LOGGER = Logger.getLogger(Exercise3.class.getName());

  public static void main(String[] args) throws IOException {
    MemoryStore store = new MemoryStore();
    ForwardChainingRDFSInferencer inferencer = new ForwardChainingRDFSInferencer(store);
    Repository repo = new SailRepository(inferencer);
    repo.initialize();

    InputStream schemaOrgStream = Exercise3.class.getClassLoader().getResourceAsStream("schemaorg-current-http.nt");

    try (RepositoryConnection repositoryConnection = repo.getConnection()) {
      LOGGER.info("Loading repository...");
      try {
        repositoryConnection.add(schemaOrgStream, "http://schema.org/", RDFFormat.NTRIPLES);
      } catch (IOException e) {
        LOGGER.severe("Error loading repository: " + e.getMessage());
        throw new RuntimeException(e);
      }
      LOGGER.info("Repository loaded...");

      Files.createDirectories(Paths.get("exercise_3_results"));

      executeQuery1(repositoryConnection);
      System.out.println("\n");
      executeQuery2(repositoryConnection);
      System.out.println("\n");
      executeQuery3(repositoryConnection);
      System.out.println("\n");
      executeQuery4(repositoryConnection);
      System.out.println("\n");
    }
    executeQuery5();
  }

  private static void executeQuery1(RepositoryConnection repositoryConnection) {
    // Find all subclasses of class CollegeOrUniversity (note that http://schema.org/ prefers to use the equivalent term “type” for “class”).
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            " PREFIX schema: <http://schema.org/>" +
            " SELECT ?classLabel" +
            " WHERE {" +
            " ?class rdf:type rdfs:Class ." +
            " ?class rdfs:subClassOf/rdfs:subClassOf? schema:CollegeOrUniversity . " +
            " ?class rdfs:label ?classLabel ." +
            " FILTER (?classLabel != \"CollegeOrUniversity\")" +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_3_results/query1_result.txt", false));
      writer.write(
          "Find all subclasses of class CollegeOrUniversity (note that http://schema.org/ prefers to use the equivalent term “type” for “class”).\n");
      System.out.println(
          "Find all subclasses of class CollegeOrUniversity (note that http://schema.org/ prefers to use the equivalent term “type” for “class”).");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write(bindingSet.getValue("classLabel").stringValue());
        System.out.println(bindingSet.getValue("classLabel").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query1 result: " + e.getMessage());
    }
  }

  private static void executeQuery2(RepositoryConnection repositoryConnection) {
    // Find all the superclasses of class CollegeOrUniversity.
    String query =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            " PREFIX schema: <http://schema.org/>" +
            " SELECT ?superclassLabel" +
            " WHERE {" +
            " schema:CollegeOrUniversity rdfs:subClassOf* ?superClass ." +
            " ?superClass rdfs:label ?superclassLabel ." +
            " FILTER (?superclassLabel != \"CollegeOrUniversity\")" +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_3_results/query2_result.txt", false));
      writer.write("Find all the superclasses of class CollegeOrUniversity.\n");
      System.out.println("Find all the superclasses of class CollegeOrUniversity.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write(bindingSet.getValue("superclassLabel").stringValue());
        System.out.println(bindingSet.getValue("superclassLabel").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query2 result: " + e.getMessage());
    }
  }

  private static void executeQuery3(RepositoryConnection repositoryConnection) {
    // Find all properties defined for the class CollegeOrUniversity together with all the properties inherited by its superclasses.
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            " PREFIX schema: <http://schema.org/>" +
            " SELECT DISTINCT ?property" +
            " WHERE {" +
            " schema:CollegeOrUniversity rdfs:subClassOf* ?superClass ." +
            " ?superClass ?property ?class ." +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_3_results/query3_result.txt", false));
      writer.write(
          "Find all properties defined for the class CollegeOrUniversity together with all the properties inherited by its superclasses.\n");
      System.out.println(
          "Find all properties defined for the class CollegeOrUniversity together with all the properties inherited by its superclasses.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write(bindingSet.getValue("property").stringValue());
        System.out.println(bindingSet.getValue("property").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query3 result: " + e.getMessage());
    }
  }

  private static void executeQuery4(RepositoryConnection repositoryConnection) {
    // Find all classes that are subclasses of class Thing and are found in at most 2 levels of subclass relationships away from Thing.
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            " PREFIX schema: <http://schema.org/>" +
            " SELECT ?sub (COUNT(?mid)  as ?distance) " +
            " WHERE {" +
            " ?sub rdfs:subClassOf+ ?mid ." +
            " ?mid rdfs:subClassOf+ schema:Thing ." +
            " FILTER (?sub != schema:Thing)" +
            " } " +
            " GROUP BY ?sub" +
            " HAVING (?distance <= 3) " +
            " ORDER BY ?distance ?sub";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_3_results/query4_result.txt", false));
      writer.write(
          "Find all classes that are subclasses of class Thing and are found in at most 2 levels of subclass relationships away from Thing.\n");
      System.out.println(
          "Find all classes that are subclasses of class Thing and are found in at most 2 levels of subclass relationships away from Thing.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write(bindingSet.getValue("sub").stringValue());
        System.out.println(bindingSet.getValue("sub").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query4 result: " + e.getMessage());
    }
  }

  private static void executeQuery5() {
    // Finally, express the above queries on the ontology and dataset but without the use of inferencing.
    MemoryStore store = new MemoryStore();
    Repository repo = new SailRepository(store);
    repo.initialize();
    InputStream schemaOrgStream = Exercise3.class.getClassLoader().getResourceAsStream("schemaorg-current-http.nt");

    try (RepositoryConnection repositoryConnection = repo.getConnection()) {
      LOGGER.info("Loading repository with no inferencing...");
      try {
        repositoryConnection.add(schemaOrgStream, "http://schema.org/", RDFFormat.NTRIPLES);
      } catch (IOException e) {
        LOGGER.severe("Error loading repository with no inferencing: " + e.getMessage());
        throw new RuntimeException(e);
      }
      LOGGER.info("Repository with no inferencing loaded...");
      String query =
          "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
              "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
              " PREFIX schema: <http://schema.org/>" +
              " SELECT ?sub (COUNT(?mid)  as ?distance) " +
              " WHERE {" +
              " ?sub rdfs:subClassOf ?mid ." +
              " ?mid rdfs:subClassOf schema:Thing ." +
              " FILTER (?sub != schema:Thing)" +
              " } " +
              " GROUP BY ?sub" +
              " HAVING (?distance <= 3) " +
              " ORDER BY ?distance ?sub";
      TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);

      try (TupleQueryResult result = tupleQuery.evaluate()) {
        BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_3_results/query5_result.txt", false));
        writer.write(
            "Finally, express the above queries on the ontology and dataset but without the use of inferencing.\n");
        System.out.println(
            "Finally, express the above queries on the ontology and dataset but without the use of inferencing.");
        while (result.hasNext()) {
          BindingSet bindingSet = result.next();
          writer.newLine();
          writer.write(bindingSet.getValue("sub").stringValue());
          System.out.println(bindingSet.getValue("sub").stringValue());
        }
        writer.close();
      } catch (IOException e) {
        LOGGER.severe("Error writing query5 result: " + e.getMessage());
      }
    }
  }
}
