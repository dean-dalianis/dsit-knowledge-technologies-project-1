# DSIT Knowledge Technologies Project 1

This repository contains the solutions for the homework 1 of the course M164 Knowledge Technologies for the academic
year 2022-2023. The homework focuses on various topics such as DBpedia, querying the Greek administrative geography
dataset using SPARQL, working with the schema.org ontology, and system diagnostics using ontologies and KGs.

## Project Description

The project description can be found [here](Project.pdf).

The datasets and ontology used in the project can be found [here](https://drive.google.com/drive/folders/1KqVMz6FqSQLwUfhMNVq0oSD275BYAmu6?usp=sharing). 

## Solutions

### Exercise 1 (DBpedia)

- [Query 1](exercise_1/ex_1_1.rq): Find all Greek wines known by DBpedia and the region of Greece where they are
  produced.
- [Query 2](exercise_1/ex_1_2.rq): Find all the Greek universities known to DBpedia. Output their name, the city that
  they are located, and the number of prime ministers of Greece that have graduated from them.

### Exercise 2 (Querying the Greek administrative geography dataset using SPARQL)

- Query 1: Give the official name and population of each municipality of Greece.
- Query 2: For each region of Greece, give its official name, the official name of each regional unit that belongs to
  it, and the official name of each municipality in this regional unit.
- Query 3: For each municipality of the region Peloponnese with a population more than 5,000 people, give its official
  name, its population, and the regional unit it belongs to.
- Query 4: For each municipality of Peloponnese for which we have no seat information in the dataset, give its official
  name.
- Query 5: For each municipality of Peloponnese, give its official name and all the administrative divisions of Greece
  that it belongs to according to Kallikratis.
- Query 6: For each region of Greece, give its official name, how many municipalities belong to it, the official name of
  each regional unit that belongs to it, and how many municipalities belong to that regional unit.
- Query 7: Check the consistency of the dataset regarding stated populations.
- Query 8: Give the decentralized administrations of Greece that consist of more than two regional units.

The solutions for the queries can be found [here](exercise_2_3/src/main/java/di/knowledgetechnologies/Exercise2.java).

*Note: Some print statements are written in Greek.*

### Exercise 3 (http://schema.org)

- Query 1: Find all subclasses of class CollegeOrUniversity.
- Query 2: Find all the superclasses of class CollegeOrUniversity.
- Query 3: Find all properties defined for the class CollegeOrUniversity together with all the properties inherited by
  its superclasses.
- Query 4: Find all classes that are subclasses of class Thing and are found in at most 2 levels of subclass
  relationships away from Thing.
- Query 5: Express the above queries on the ontology and dataset but without the use of inferencing.

The solutions for the queries can be found [here](exercise_2_3/src/main/java/di/knowledgetechnologies/Exercise3.java)

### Exercise 4 (System diagnostics using ontologies and KGs)

The solutions for exercise 4 are not available in this repository.