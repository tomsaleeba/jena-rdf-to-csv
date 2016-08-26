# JENA RDF to CSV prototype

This is a quick demo about how we can traverse an RDF graph and try to flatten it into a CSV file. All the 1:1 relationships are easy but it get's more interesting when we deal with 1:many relationships. We also introduce the concept of themes so we can exclude some parts of the tree and groups others under that theme.

## Running it

    # clone this repo
    cd jena-rdf-to-csv
    ./mvnw spring-boot:run

It'll print the RDF graph in Turtle format and then print the CSV file.
