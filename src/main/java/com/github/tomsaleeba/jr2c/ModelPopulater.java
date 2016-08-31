package com.github.tomsaleeba.jr2c;

import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.JenaRdfToCsvApplication.Theme;

/**
 * Populates the RDF model with example data.
 * 
 * @author Tom Saleeba
 */
@Component
public class ModelPopulater {

	private static final Logger logger = LoggerFactory.getLogger(ModelPopulater.class);
	
	@Autowired
	private Model coreModel;
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	@Value("${jr2c.property-name.theme}")
	private String themePropertyName;
	
	@Value("${jr2c.entity-name.root}")
	private String rootEntityName;
	
	public void populate() {
		Resource species = bag(
			"species one", "species two");
		
		Resource visitSubject = resource("Visit", "Visit-1",
			literalStatement("name", "First Visit"),
			themeStatement(Theme.THEME2),
			resourceStatement("observedSpecies", species));
		
		Resource siteSubject = resource("Site", "Site-1",
			literalStatement("name", "Northern Site"),
			literalStatement("landing-page", "http://example.com/big-project/northern-site.html"),
			resourceStatement("visit", visitSubject),
			themeStatement(Theme.THEME1));
		
		resource("root", rootEntityName,
			literalStatement("name", "Big Project"),
			literalStatement("landing-page", "http://example.com/big-project/index.html"),
			resourceStatement("site", siteSubject));
		logger.info("== Start example model data ==");
		coreModel.write(System.out, "TURTLE");
		logger.info("== End example model data ==");
	}

	private Resource bag(String...values) {
		Bag result = coreModel.createBag();
		for (String curr : values) {
			result.add(curr);
		}
		return result;
	}

	private ResourceStatementPlaceholder resourceStatement(String propertyLocalName, Resource otherResource) {
		return new ResourceStatementPlaceholder(propertyLocalName, otherResource);
	}

	private StatementPlaceholder literalStatement(String propertyLocalName, String literalValue) {
		return new LiteralStatementPlaceholder(propertyLocalName, literalValue);
	}
	
	private StatementPlaceholder themeStatement(Theme theme) {
		return literalStatement(themePropertyName, theme.toString());
	}
	
	private abstract class StatementPlaceholder {
		private final String propertyLocalName;

		public StatementPlaceholder(String propertyLocalName) {
			this.propertyLocalName = propertyLocalName;
		}

		public String getPropertyLocalName() {
			return propertyLocalName;
		}

		public abstract void appendTo(Resource result);
	}
	
	private class LiteralStatementPlaceholder extends StatementPlaceholder {
		private final String literalValue;
		
		public LiteralStatementPlaceholder(String propertyLocalName, String literalValue) {
			super(propertyLocalName);
			this.literalValue = literalValue;
		}

		@Override
		public void appendTo(Resource result) {
			Property property = coreModel.createProperty(namespace + getPropertyLocalName());
			result.addLiteral(property, literalValue);
		}
	}
	
	private class ResourceStatementPlaceholder extends StatementPlaceholder {
		private final Resource otherResource;
		
		public ResourceStatementPlaceholder(String propertyLocalName, Resource otherResource) {
			super(propertyLocalName);
			this.otherResource = otherResource;
		}

		@Override
		public void appendTo(Resource result) {
			Property property = coreModel.createProperty(namespace + getPropertyLocalName());
			result.addProperty(property, otherResource);
		}
	}

	private Resource resource(String typeLocalName, String instanceLocalName, StatementPlaceholder...statements) {
		Resource type = coreModel.createResource(namespace + typeLocalName);
		Resource result = coreModel.createResource(namespace + instanceLocalName, type);
		for (StatementPlaceholder curr: statements) {
			curr.appendTo(result);
		}
		return result;
	}
}
