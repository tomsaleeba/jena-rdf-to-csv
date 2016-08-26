package com.github.tomsaleeba;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.JenaRdfToCsvApplication.Theme;

/**
 * Reads the RDF model and transforms it to CSV by trying to flatten it.
 * 
 * @author Tom Saleeba
 */
@Component
public class Transformer {

	@Autowired
	private Model coreModel;
	
	public void toCsv() {
		StringBuilder csvResult = new StringBuilder();
		Resource root = coreModel.createResource(JenaRdfToCsvApplication.NAMESPACE + JenaRdfToCsvApplication.ROOT_ENTITY_LOCAL_NAME);
		processEntity(root, csvResult);
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
		Property themeProperty = coreModel.createProperty(JenaRdfToCsvApplication.NAMESPACE + JenaRdfToCsvApplication.THEME_PROPERTY_LOCAL_NAME);
		Statement statement = entity.getProperty(themeProperty);
		if (statement == null) {
			return false;
		}
		if (Theme.OUT_OF_SCOPE.toString().equals(statement.getString())) {
			return true;
		}
		return false;
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
}
