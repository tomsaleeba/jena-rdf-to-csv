package com.github.tomsaleeba.jr2c;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;
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
		StringBuilder csvResult = new StringBuilder();
		Resource root = coreModel.createResource(namespace + rootEntityName);
		processEntity(root, csvResult);
		logger.info("== The CSV result ==");
		System.out.println(csvResult.toString());
	}

	private void processEntity(Resource entity, StringBuilder csvResult) {
		for (Statement curr : entity.listProperties().toList()) {
			if (isLiteral(curr)) {
				addRow(csvResult, curr.getSubject(), curr.getPredicate(), curr.getString()); // FIXME handle more than String
				continue;
			}
			if (isType(curr)) {
				continue;
			}
			Resource otherEntity = curr.getResource();
			if (isOutOfScope(otherEntity)) {
				continue;
			}
			addRow(csvResult, curr.getSubject(), curr.getPredicate(), otherEntity);
			processEntity(otherEntity, csvResult);
		}
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

	private void addRow(StringBuilder csvResult, Resource subject, Property predicate, Resource object) {
		addRow(csvResult, subject, predicate, object.getURI());
	}

	private void addRow(StringBuilder csvResult, Resource subject, Property predicate, String literal) {
		String str = String.format("\"%s\",\"%s\",\"%s\"\n", subject.getURI(), predicate.getURI(), literal);
		csvResult.append(str);
	}

	private boolean isLiteral(Statement s) {
		try {
			s.getResource();
			return false;
		} catch (ResourceRequiredException e) {
			return true;
		}
	}
	
	private boolean isType(Statement curr) {
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
