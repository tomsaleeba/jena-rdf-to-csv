# JENA RDF to CSV prototype

This is a quick demo about how we can traverse an RDF graph and try to flatten it into a CSV file. All the 1:1 relationships are easy but it get's more interesting when we deal with 1:many relationships. We also introduce the concept of themes so we can exclude some parts of the tree and groups others under that theme.

## Running it

    # clone this repo
    cd jena-rdf-to-csv
    ./mvnw spring-boot:run

It'll print the RDF graph in Turtle format and then print the CSV file.

## Command line parameters

    --ttl-data       path to the TTL/TURTLE RDF data to load into the model. If not supplied, some data will be generated
    --root-entity    URI of the root entity in the data. This is where we'll start the crawl. Not required if you don't provide --ttl-data