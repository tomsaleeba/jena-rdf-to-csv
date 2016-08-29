package com.github.tomsaleeba.jr2c;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

public class ImprovisedCsvResultContextTest {

	private Model helper = ModelFactory.createDefaultModel();
	private static final String NS = "urn:test#";
	
	/**
	 * Can we create a new row?
	 */
	@Test
	public void testNewRow01() {
		ImprovisedCsvResultContext objectUnderTest = new ImprovisedCsvResultContext();
		Row result = objectUnderTest.newRow();
		assertNotNull(result);
	}

	/**
	 * Can we create a header column when none exist?
	 */
	@Test
	public void testGetOrCreateHeaderColumn01() {
		ImprovisedCsvResultContext objectUnderTest = new ImprovisedCsvResultContext();
		HeaderColumn result = objectUnderTest.getOrCreateHeaderColumn("Site", "name");
		assertThat(result.getName(), is("Site.name"));
	}
	
	@Test
	public void testToCsv01() {
		ImprovisedCsvResultContext objectUnderTest = new ImprovisedCsvResultContext();
		Row row1 = objectUnderTest.newRow();
		Resource subject = subject("Site1", "Site");
		row1.addOneToOneRelationship(subject, helper.createProperty("urn:test#name"), "site #1");
		row1.addOneToOneRelationship(subject, helper.createProperty("urn:test#colour"), "blue");
		row1.addOneToOneRelationship(subject, helper.createProperty("urn:test#climate"), "arid");
		String[] result = objectUnderTest.toCsv();
		assertThat(result.length, is(2));
		assertThat(result[0], is("Site.name,Site.colour,Site.climate"));
		assertThat(result[1], is("\"site #1\",\"blue\",\"arid\""));
	}

	private Resource subject(String instanceId, String type) {
		Resource typeResource = resource(type);
		return helper.createResource(NS + instanceId, typeResource);
	}

	private Resource resource(String localName) {
		return helper.createResource(NS + localName);
	}
}
