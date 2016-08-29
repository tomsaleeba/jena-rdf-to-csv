package com.github.tomsaleeba.jr2c;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.JenaRdfToCsvApplication.Theme;

/**
 * Reads the RDF model and transforms it to CSV by trying to flatten it.
 * 
 * @author Tom Saleeba
 */
@Component
public class Transformer {

	private static final Logger logger = LoggerFactory.getLogger(Transformer.class);
	
	@Autowired
	private Model coreModel;
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	@Value("${jr2c.property-name.theme}")
	private String themePropertyName;
	
	@Value("${jr2c.entity-name.root}")
	private String rootEntityName;
	
	private Set<Theme> inScopeThemes = new HashSet<>();
	
	public void toCsv() {
		CsvResultContext csvResultContext = new ImprovisedCsvResultContext();
		Resource root = coreModel.createResource(namespace + rootEntityName);
		Row row = csvResultContext.newRow();
		processEntity(root, csvResultContext, row);
		logger.info("== The CSV result ==");
		String[] csvLines = csvResultContext.toCsv();
		for (String curr : csvLines) {
			System.out.println(curr);
		}
	}

	private void processEntity(Resource entity, CsvResultContext csvResultContext, Row row) {
		for (Statement curr : entity.listProperties().toList()) {
			if (isLiteral(curr)) {
				row.addOneToOneRelationship(curr.getSubject(), curr.getPredicate(), curr.getString()); // FIXME handle more than String
				continue;
			}
			if (isRdfTypeStatement(curr)) {
				continue;
			}
			Resource otherEntity = curr.getResource();
			if (isOutOfScope(otherEntity)) {
				continue;
			}
			if (isBag(otherEntity)) {
				// We're going to get repeated rows now
				// TODO auto split into another table on one-to-many
				processBag(curr, csvResultContext, row);
				continue;
			}
			// Assuming one-to-one, fields go on this table
			processEntity(otherEntity, csvResultContext, row);
		}
	}

	private void processBag(Statement bagStatement, CsvResultContext csvResultContext, Row row) {
		Resource bag = bagStatement.getResource();
		List<String> values = new LinkedList<>();
		for (StmtIterator it = bag.listProperties(); it.hasNext();) {
			Statement curr = it.next();
			if (isRdfTypeStatement(curr)) {
				continue;
			}
			values.add(curr.getString()); // FIXME handle more than just String
		}
		row.addOneToManyRelationship(bagStatement.getSubject(), bagStatement.getPredicate(), values);
	}

	private boolean isBag(Resource resource) {
		Statement typeStatement = resource.getProperty(RDF.type);
		if (typeStatement == null) {
			return false;
		}
		if (RDF.Bag.equals(typeStatement.getResource())) {
			return true;
		}
		return false;
	}

	private boolean isOutOfScope(Resource entity) {
		Property themeProperty = coreModel.createProperty(namespace + themePropertyName);
		Statement statement = entity.getProperty(themeProperty);
		boolean isNoTheme = statement == null;
		if (isNoTheme) {
			return false;
		}
		boolean isInScope = inScopeThemes.stream().anyMatch(e -> e.toString().equals(statement.getString()));
		if (isInScope) {
			return false;
		}
		return true;
	}

	private boolean isLiteral(Statement s) {
		try {
			s.getResource();
			return false;
		} catch (ResourceRequiredException e) {
			return true;
		}
	}
	
	private boolean isRdfTypeStatement(Statement curr) {
		if (RDF.type.equals(curr.getPredicate())) {
			return true;
		}
		return false;
	}

	public void setInScopeThemes(Set<Theme> inScopeThemes) {
		this.inScopeThemes.clear();
		this.inScopeThemes.addAll(inScopeThemes);
	}
}
