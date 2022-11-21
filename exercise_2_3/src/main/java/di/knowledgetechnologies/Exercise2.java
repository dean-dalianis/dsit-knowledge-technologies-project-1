package di.knowledgetechnologies;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class Exercise2 {

  private static final Logger LOGGER = Logger.getLogger(Exercise2.class.getName());

  public static void main(String[] args) throws IOException {
    MemoryStore store = new MemoryStore();
    Repository repo = new SailRepository(store);
    repo.initialize();

    InputStream geonamesStream = Exercise2.class.getClassLoader().getResourceAsStream("Kallikratis-Geonames.nt");

    try (RepositoryConnection repositoryConnection = repo.getConnection()) {
      LOGGER.info("Loading repository...");
      try {
        repositoryConnection.add(geonamesStream, "", RDFFormat.NTRIPLES);
      } catch (IOException e) {
        LOGGER.severe("Error loading repository: " + e.getMessage());
        throw new RuntimeException(e);
      }
      LOGGER.info("Repository loaded...");

      Files.createDirectories(Paths.get("exercise_2_results"));

      executeQuery1(repositoryConnection);
      System.out.println("\n");
      executeQuery2(repositoryConnection);
      System.out.println("\n");
      executeQuery3(repositoryConnection);
      System.out.println("\n");
      executeQuery4(repositoryConnection);
      System.out.println("\n");
      executeQuery5(repositoryConnection);
      System.out.println("\n");
      executeQuery6(repositoryConnection);
      System.out.println("\n");
      executeQuery7(repositoryConnection);
      System.out.println("\n");
      executeQuery8(repositoryConnection);
    }
  }

  private static void executeQuery1(RepositoryConnection repositoryConnection) {
    // Give the official name and population of each municipality (δήμος) of Greece.
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?municipalityName ?population" +
            " WHERE {" +
            " ?municipality rdf:type gag:Δήμος ." +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:έχει_πληθυσμό ?population ." +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query1_result.txt", false));
      writer.write("Give the official name and population of each municipality (δήμος) of Greece.\n");
      System.out.println("Give the official name and population of each municipality (δήμος) of Greece.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write("Δήμος: " + bindingSet.getValue("municipalityName").stringValue() + " Πληθυσμός: " +
            bindingSet.getValue("population").stringValue());
        System.out.println("Δήμος: " + bindingSet.getValue("municipalityName").stringValue() + " Πληθυσμός: " +
            bindingSet.getValue("population").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query1 result: " + e.getMessage());
    }
  }


  private static void executeQuery2(RepositoryConnection repositoryConnection) {
    /*
      For each region (περιφέρεια) of Greece, give its official name, the official name of each
      regional unit (περιφερειακή ενότητα) that belongs to it, and the official name of each
      municipality (δήμος) in this regional unit. Organize your answer by region, regional unit
      and municipality.
     */
    HashMap<String, HashMap<String, List<String>>> municipalitiesOfRegionalUnitsOfRegions = new HashMap<>();

    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?regionName ?regionalUnitName ?municipalityName" +
            " WHERE {" +
            " ?region rdf:type gag:Περιφέρεια ." +
            " ?region gag:έχει_επίσημο_όνομα ?regionName ." +
            " ?regionalUnit rdf:type gag:Περιφερειακή_Ενότητα ." +
            " ?regionalUnit gag:έχει_επίσημο_όνομα ?regionalUnitName ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?municipality rdf:type gag:Δήμος ." +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:ανήκει_σε ?regionalUnit ." +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        municipalitiesOfRegionalUnitsOfRegions.putIfAbsent(bindingSet.getValue("regionName").stringValue(), new HashMap<>());
        municipalitiesOfRegionalUnitsOfRegions.get(bindingSet.getValue("regionName").stringValue())
            .putIfAbsent(bindingSet.getValue("regionalUnitName").stringValue(), new ArrayList<>());
        municipalitiesOfRegionalUnitsOfRegions.get(bindingSet.getValue("regionName").stringValue())
            .get(bindingSet.getValue("regionalUnitName").stringValue())
            .add(bindingSet.getValue("municipalityName").stringValue());
      }
    }

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query2_result.txt", false));
      writer.write("For each region (περιφέρεια) of Greece, give its official name, the official name of each\n" +
          "regional unit (περιφερειακή ενότητα) that belongs to it, and the official name of each\n" +
          "municipality (δήμος) in this regional unit. Organize your answer by region, regional unit\n" +
          "and municipality.\n");
      System.out.println("For each region (περιφέρεια) of Greece, give its official name, the official name of each\n" +
          "regional unit (περιφερειακή ενότητα) that belongs to it, and the official name of each\n" +
          "municipality (δήμος) in this regional unit. Organize your answer by region, regional unit\n" +
          "and municipality.");
      for (String region : municipalitiesOfRegionalUnitsOfRegions.keySet()) {
        writer.newLine();
        writer.write("Περιφέρεια: " + region);
        System.out.println("Περιφέρεια: " + region);
        for (String regionalUnit : municipalitiesOfRegionalUnitsOfRegions.get(region).keySet()) {
          writer.newLine();
          writer.write("  Περιφερειακή Ενότητα: " + regionalUnit);
          System.out.println("  Περιφερειακή Ενότητα: " + regionalUnit);
          for (String municipality : municipalitiesOfRegionalUnitsOfRegions.get(region).get(regionalUnit)) {
            writer.newLine();
            writer.write("    Δήμος: " + municipality);
            System.out.println("    Δήμος: " + municipality);
          }
        }
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query2 result: " + e.getMessage());
    }
  }

  private static void executeQuery3(RepositoryConnection repositoryConnection) {
    /*
      For each municipality of the region Peloponnese with population more than 5,000 people,
      give its official name, its population, and the regional unit it belongs to. Organize your
      answer by municipality and regional unit.
     */
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?municipalityName ?regionName ?population ?regionalUnitName" +
            " WHERE {" +
            " ?municipality rdf:type gag:Δήμος ." +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:ανήκει_σε ?regionalUnit ." +
            " ?regionalUnit gag:έχει_επίσημο_όνομα ?regionalUnitName ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?region gag:έχει_επίσημο_όνομα ?regionName ." +
            " ?municipality gag:έχει_πληθυσμό ?population ." +
            " FILTER(?regionName = \"ΠΕΡΙΦΕΡΕΙΑ ΠΕΛΟΠΟΝΝΗΣΟΥ\") " +
            " FILTER(?population > 5000) " +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query3_result.txt", false));
      writer.write("For each municipality of the region Peloponnese with population more than 5,000 people,\n" +
          "give its official name, its population, and the regional unit it belongs to. Organize your\n" +
          "answer by municipality and regional unit.\n");
      System.out.println("For each municipality of the region Peloponnese with population more than 5,000 people,\n" +
          "give its official name, its population, and the regional unit it belongs to. Organize your\n" +
          "answer by municipality and regional unit.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write("Δήμος: " + bindingSet.getValue("municipalityName").stringValue() + ", Πληθυσμός: " +
            bindingSet.getValue("population").stringValue() + ", Περιφερειακή Ενότητα: " + bindingSet.getValue("regionalUnitName").stringValue());
        System.out.println("Δήμος: " + bindingSet.getValue("municipalityName").stringValue() +
            ", Πληθυσμός: " + bindingSet.getValue("population").stringValue() +
            ", Περιφερειακή Ενότητα: " + bindingSet.getValue("regionalUnitName").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query3 result: " + e.getMessage());
    }
  }

  private static void executeQuery4(RepositoryConnection repositoryConnection) {
    //  For each municipality of Peloponnese for which we have no seat (έδρα) information in the dataset, give its official name.
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?municipalityName " +
            " WHERE {" +
            " ?municipality rdf:type gag:Δήμος ." +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:ανήκει_σε ?regionalUnit ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?region gag:έχει_επίσημο_όνομα ?regionName ." +
            " FILTER NOT EXISTS { ?municipality gag:έχει_έδρα ?seat }" +
            " FILTER(?regionName = \"ΠΕΡΙΦΕΡΕΙΑ ΠΕΛΟΠΟΝΝΗΣΟΥ\") " +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query4_result.txt", false));
      writer.write("For each municipality of Peloponnese for which we have no seat (έδρα) information in\n" +
          "the dataset, give its official name.\n");
      System.out.println("For each municipality of Peloponnese for which we have no seat (έδρα) information in\n" +
          "the dataset, give its official name.");
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        writer.newLine();
        writer.write("Δήμος: " + bindingSet.getValue("municipalityName").stringValue());
        System.out.println("Δήμος: " + bindingSet.getValue("municipalityName").stringValue());
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query4 result: " + e.getMessage());
    }
  }

  private static void executeQuery5(RepositoryConnection repositoryConnection) {
    /*
      For each municipality of Peloponnese, give its official name and all the administrative
      divisions of Greece that it belongs to according to Kallikratis. Your query should be the
      simplest one possible, and it should not use any explicit knowledge of how many levels
      of administration are imposed by Kallikratis.
     */
    HashMap<String, List<String[]>> municipalityToUnit = new HashMap<>();
    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?municipalityName ?unitName ?unitTypeName" +
            " WHERE {" +
            " ?municipality rdf:type gag:Δήμος ." +
            " {" +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:ανήκει_σε* ?unit ." +
            " ?unit gag:έχει_επίσημο_όνομα ?unitName ." +
            " ?unit rdf:type ?unitType ." +
            " ?unitType rdfs:label ?unitTypeName ." +
            " FILTER (lang(?unitTypeName) = 'el') " +
            " FILTER(?unitName != ?municipalityName) " +
            " }" +
            " { " +
            " ?municipality gag:ανήκει_σε ?regionalUnit ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?region gag:έχει_επίσημο_όνομα ?regionName ." +
            " FILTER(?regionName = \"ΠΕΡΙΦΕΡΕΙΑ ΠΕΛΟΠΟΝΝΗΣΟΥ\") " +
            " }" +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        if (municipalityToUnit.containsKey(bindingSet.getValue("municipalityName").stringValue())) {
          municipalityToUnit.get(bindingSet.getValue("municipalityName").stringValue()).add(new String[]{
              bindingSet.getValue("unitTypeName").stringValue(),
              bindingSet.getValue("unitName").stringValue()
          });
        } else {
          List<String[]> units = new ArrayList<>();
          units.add(new String[]{
              bindingSet.getValue("unitTypeName").stringValue(),
              bindingSet.getValue("unitName").stringValue()
          });
          municipalityToUnit.put(bindingSet.getValue("municipalityName").stringValue(), units);
        }
      }
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query5_result.txt", false));
      writer.write("For each municipality of Peloponnese, give its official name and all the administrative\n" +
          "divisions of Greece that it belongs to according to Kallikratis. Your query should be the\n" +
          "simplest one possible, and it should not use any explicit knowledge of how many levels\n" +
          "of administration are imposed by Kallikratis.\n");
      System.out.println("For each municipality of Peloponnese, give its official name and all the administrative\n" +
          "divisions of Greece that it belongs to according to Kallikratis. Your query should be the\n" +
          "simplest one possible, and it should not use any explicit knowledge of how many levels\n" +
          "of administration are imposed by Kallikratis.");
      for (String municipality : municipalityToUnit.keySet()) {
        writer.newLine();
        writer.write("Δήμος: " + municipality);
        System.out.println("Δήμος: " + municipality);
        for (String[] unit : municipalityToUnit.get(municipality)) {
          writer.newLine();
          writer.write("  " + unit[0] + ": " + unit[1]);
          System.out.println("  " + unit[0] + ": " + unit[1]);
        }
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query5 result: " + e.getMessage());
    }
  }

  private static void executeQuery6(RepositoryConnection repositoryConnection) {
    /*
      For each region of Greece, give its official name, how many municipalities belong to it,
      the official name of each regional unit (περιφερειακή ενότητα) that belongs to it, and how
      many municipalities belong to that regional unit
     */
    HashMap<String, HashMap<String, List<String>>> municipalitiesOfRegionalUnitsOfRegions = new HashMap<>();

    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?regionName ?regionalUnitName ?municipalityName" +
            " WHERE {" +
            " ?region rdf:type gag:Περιφέρεια ." +
            " ?region gag:έχει_επίσημο_όνομα ?regionName ." +
            " ?regionalUnit rdf:type gag:Περιφερειακή_Ενότητα ." +
            " ?regionalUnit gag:έχει_επίσημο_όνομα ?regionalUnitName ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?municipality rdf:type gag:Δήμος ." +
            " ?municipality gag:έχει_επίσημο_όνομα ?municipalityName ." +
            " ?municipality gag:ανήκει_σε ?regionalUnit ." +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        municipalitiesOfRegionalUnitsOfRegions.putIfAbsent(bindingSet.getValue("regionName").stringValue(), new HashMap<>());
        municipalitiesOfRegionalUnitsOfRegions.get(bindingSet.getValue("regionName").stringValue())
            .putIfAbsent(bindingSet.getValue("regionalUnitName").stringValue(), new ArrayList<>());
        municipalitiesOfRegionalUnitsOfRegions.get(bindingSet.getValue("regionName").stringValue())
            .get(bindingSet.getValue("regionalUnitName").stringValue())
            .add(bindingSet.getValue("municipalityName").stringValue());
      }
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query6_result.txt", false));
      writer.write("For each region of Greece, give its official name, how many municipalities belong to it,\n" +
          "the official name of each regional unit (περιφερειακή ενότητα) that belongs to it, and how\n" +
          "many municipalities belong to that regional unit\n");
      System.out.println("For each region of Greece, give its official name, how many municipalities belong to it,\n" +
          "the official name of each regional unit (περιφερειακή ενότητα) that belongs to it, and how\n" +
          "many municipalities belong to that regional unit");
      for (String region : municipalitiesOfRegionalUnitsOfRegions.keySet()) {
        int numberOfMunicipalitiesInRegion = municipalitiesOfRegionalUnitsOfRegions.get(region).values().stream().mapToInt(List::size).sum();
        writer.newLine();
        writer.write("Περιφέρεια: " + region + " (" + numberOfMunicipalitiesInRegion + " Δήμοι)");
        System.out.println("Περιφέρεια: " + region + " (" + numberOfMunicipalitiesInRegion + " Δήμοι)");
        for (String regionalUnit : municipalitiesOfRegionalUnitsOfRegions.get(region).keySet()) {
          int numberOfMunicipalitiesInRegionalUnit = municipalitiesOfRegionalUnitsOfRegions.get(region).get(regionalUnit).size();
          writer.newLine();
          writer.write("  Περιφερειακή Ενότητα: " + regionalUnit + " (" + numberOfMunicipalitiesInRegionalUnit + " Δήμοι)");
          System.out.println("  Περιφερειακή Ενότητα: " + regionalUnit + " (" + numberOfMunicipalitiesInRegionalUnit + " Δήμοι)");
        }
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query6 result: " + e.getMessage());
    }
  }

  private static void executeQuery7(RepositoryConnection repositoryConnection) {
    /*
      Check the consistency of the dataset regarding stated populations: the sum of the
      populations of all administrative units A of level L must be equal to the population of the
      administrative unit B of level L+1 to which all administrative units A belong to. (You
      have to write one query only.)
     */
    HashMap<String, Integer> unitBPopulation = new HashMap<>();
    HashMap<String, List<Object[]>> unitBs = new HashMap<>();

    String unitAType = "Δημοτική_Ενότητα";

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query7_result.txt", false));
      writer.write("Check the consistency of the dataset regarding stated populations: the sum of the\n" +
          "populations of all administrative units A of level L must be equal to the population of the\n" +
          "administrative unit B of level L+1 to which all administrative units A belong to. (You\n" +
          "have to write one query only.)\n");
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query7 result: " + e.getMessage());
    }
    System.out.println("Check the consistency of the dataset regarding stated populations: the sum of the\n" +
        "populations of all administrative units A of level L must be equal to the population of the\n" +
        "administrative unit B of level L+1 to which all administrative units A belong to. (You\n" +
        "have to write one query only.)");

    while (true) {
      String unitBType = "";
      String query =
          "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
              "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
              " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
              " SELECT ?unitA ?unitB ?unitAName ?unitBName ?populationA ?populationB ?unitBType" +
              " WHERE {" +
              " ?unitA rdf:type gag:" + unitAType + " ." +
              " ?unitA gag:έχει_πληθυσμό ?populationA ." +
              " ?unitA gag:έχει_επίσημο_όνομα ?unitAName ." +
              " ?unitA gag:ανήκει_σε ?unitB ." +
              " ?unitB gag:έχει_πληθυσμό ?populationB ." +
              " ?unitB gag:έχει_επίσημο_όνομα ?unitBName ." +
              " ?unitB rdf:type ?unitBType ." +
              " } ";
      TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
      try (TupleQueryResult result = tupleQuery.evaluate()) {
        while (result.hasNext()) {
          BindingSet bindingSet = result.next();

          unitBs.putIfAbsent(bindingSet.getValue("unitBName").stringValue(), new ArrayList<>());
          unitBs.get(bindingSet.getValue("unitBName").stringValue())
              .add(new Object[]{bindingSet.getValue("unitAName").stringValue(), bindingSet.getValue("populationA").stringValue()});
          unitBPopulation.putIfAbsent(bindingSet.getValue("unitBName").stringValue(),
              Integer.parseInt(bindingSet.getValue("populationB").stringValue()));
          unitBType = bindingSet.getValue("unitBType").stringValue();
          unitBType = unitBType.substring(unitBType.lastIndexOf("/") + 1);
        }
        if (unitBs.isEmpty()) {
          break;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query7_result.txt", true));
        for (String uB : unitBs.keySet()) {
          int sumOfUnitAPopulations = unitBs.get(uB).stream().mapToInt(o -> Integer.parseInt(o[1].toString())).sum();
          if (sumOfUnitAPopulations != unitBPopulation.get(uB)) {
            writer.newLine();
            writer.write("Εντοπίστηκε αναντιστοιχία πληθυσμού\n");
            writer.write(unitBType + ": " + uB + " (" + unitBPopulation.get(uB) + ")\n");
            writer.write("Πληθυσμός για " + unitAType + " που ανήκουν στον " + unitBType + uB + ": " + sumOfUnitAPopulations + "\n");
            writer.write("Διαφορά: " + (unitBPopulation.get(uB) - sumOfUnitAPopulations) + "\n");
            writer.write(unitAType + " που ανήκουν στον " + unitBType + ": " + uB + "\n");
            for (Object[] unitA : unitBs.get(uB)) {
              writer.write("  " + unitA[0] + " (" + unitA[1] + ")\n");
            }

            System.out.println("Εντοπίστηκε αναντιστοιχία πληθυσμού");
            System.out.println(unitBType + ": " + uB + " (" + unitBPopulation.get(uB) + ")");
            System.out.println("Πληθυσμός για " + unitAType + " που ανήκουν στον " + unitBType + ": " + uB + ": " + sumOfUnitAPopulations);
            System.out.println("Διαφορά: " + (unitBPopulation.get(uB) - sumOfUnitAPopulations));
            System.out.println(unitAType + " που ανήκουν στον " + unitBType + ": " + uB);
            for (Object[] uA : unitBs.get(uB)) {
              System.out.println("  " + uA[0] + " (" + uA[1] + ")");
            }
          }
        }
        writer.close();
      } catch (IOException e) {
        LOGGER.severe("Error writing query7 result: " + e.getMessage());
      }
      unitBPopulation.clear();
      unitBs.clear();
      unitAType = unitBType;
    }
  }

  private static void executeQuery8(RepositoryConnection repositoryConnection) {
    /*
      Give the decentralized administrations (αποκεντρωμένες διοικήσεις) of Greece that
      consist of more than two regional units. (You cannot use SPARQL 1.1 aggregate
      operators to express this query.)
     */
    HashMap<String, List<String>> regionalUnitsOfDecentralizedAdministrations = new HashMap<>();

    String query =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            " PREFIX gag: <http://geo.linkedopendata.gr/gag/ontology/>" +
            " SELECT ?decentralizedAdministrationName ?regionalUnitName" +
            " WHERE {" +
            " ?regionalUnit rdf:type gag:Περιφερειακή_Ενότητα ." +
            " ?regionalUnit gag:έχει_επίσημο_όνομα ?regionalUnitName ." +
            " ?regionalUnit gag:ανήκει_σε ?region ." +
            " ?region gag:ανήκει_σε ?decentralizedAdministration ." +
            " ?decentralizedAdministration gag:έχει_επίσημο_όνομα ?decentralizedAdministrationName ." +
            " } ";
    TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
    try (TupleQueryResult result = tupleQuery.evaluate()) {
      while (result.hasNext()) {
        BindingSet bindingSet = result.next();
        regionalUnitsOfDecentralizedAdministrations.putIfAbsent(bindingSet.getValue("decentralizedAdministrationName").stringValue(),
            new ArrayList<>());
        regionalUnitsOfDecentralizedAdministrations.get(bindingSet.getValue("decentralizedAdministrationName").stringValue())
            .add(bindingSet.getValue("regionalUnitName").stringValue());
      }
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("exercise_2_results/query8_result.txt", false));
      writer.write("Give the decentralized administrations (αποκεντρωμένες διοικήσεις) of Greece that\n" +
          "consist of more than two regional units.\n");
      System.out.println("Give the decentralized administrations (αποκεντρωμένες διοικήσεις) of Greece that\n" +
          "consist of more than two regional units.");
      for (String decentralizedAdministration : regionalUnitsOfDecentralizedAdministrations.keySet()) {
        if (regionalUnitsOfDecentralizedAdministrations.get(decentralizedAdministration).size() > 2) {
          writer.newLine();
          writer.write("Αποκεντρωμένη Διοίκηση: " + decentralizedAdministration + " (" +
              regionalUnitsOfDecentralizedAdministrations.get(decentralizedAdministration).size() + " Περιφερειακές Ενότητες)");
          System.out.println("Αποκεντρωμένη Διοίκηση: " + decentralizedAdministration + " ("
              + regionalUnitsOfDecentralizedAdministrations.get(decentralizedAdministration).size() + " Περιφερειακές Ενότητες)");
        }
      }
      writer.close();
    } catch (IOException e) {
      LOGGER.severe("Error writing query8 result: " + e.getMessage());
    }
  }
}
