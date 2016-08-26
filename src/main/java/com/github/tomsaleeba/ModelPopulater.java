package com.github.tomsaleeba;

import javax.annotation.PostConstruct;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.JenaRdfToCsvApplication.Theme;

/**
 * Populates the RDF model with data.
 * 
 * @author Tom Saleeba
 */
@Component
public class ModelPopulater {

	@Autowired
	private Model coreModel;
	
	@Autowired
	private Transformer transformer;
	
	@PostConstruct
	public void populate() {
		Resource rootSubject = resource("root", JenaRdfToCsvApplication.ROOT_ENTITY_LOCAL_NAME);
		addLiteral(rootSubject, "name", "Big Project");
		addLiteral(rootSubject, "landing-page", "http://example.com/big-project/index.html");
		Resource siteSubject = resource("Site", "Site-1");
		addLiteral(siteSubject, "name", "Northern Site");
		addLiteral(siteSubject, "landing-page", "http://example.com/big-project/northern-site.html");
		addLink(rootSubject, "site", siteSubject);
		Resource visitSubject = resource("Visit", "Visit-1");
		addLiteral(visitSubject, "name", "First Visit");
		markOutOfScope(visitSubject);
		addLink(siteSubject, "visit", visitSubject);
		coreModel.write(System.out, "TURTLE");
		transformer.toCsv();
	}

	private void markOutOfScope(Resource resource) {
		addLiteral(resource, JenaRdfToCsvApplication.THEME_PROPERTY_LOCAL_NAME, Theme.OUT_OF_SCOPE.toString());
	}

	private Resource resource(String typeLocalName, String instanceLocalName) {
		Resource type = coreModel.createResource(JenaRdfToCsvApplication.NAMESPACE + typeLocalName);
		return coreModel.createResource(JenaRdfToCsvApplication.NAMESPACE + instanceLocalName, type);
	}
	
	private void addLiteral(Resource subject, String propertyLocalName, String literalValue) {
		Property property = coreModel.createProperty(JenaRdfToCsvApplication.NAMESPACE + propertyLocalName);
		subject.addLiteral(property, literalValue);
	}
	
	private void addLink(Resource subject, String propertyLocalName, Resource object) {
		Property property = coreModel.createProperty(JenaRdfToCsvApplication.NAMESPACE + propertyLocalName);
		subject.addProperty(property, object);
	}
}
