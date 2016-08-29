package com.github.tomsaleeba.jr2c;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

public class RowTest {

	private Model helper = ModelFactory.createDefaultModel();
	private static final String NS = "urn:test#";
	
	/**
	 * Can we add a one-to-one column?
	 */
	@Test
	public void testAddOneToOneRelationship01() {
		CsvResultContext context = mock(CsvResultContext.class);
		HeaderColumn col1 = new HeaderColumn("Site.name");
		when(context.getOrCreateHeaderColumn("Site", "name")).thenReturn(col1);
		Row objectUnderTest = new Row(context);
		objectUnderTest.addOneToOneRelationship(subject("Site1", "Site"), helper.createProperty("urn:test#name"), "site #1");
		List<String> result = objectUnderTest.toCsv(new HashSet<HeaderColumn>(Arrays.asList(col1)));
		assertThat(result.size(), is(1));
		assertThat(result.get(0), is("\"site #1\""));
		verify(context).getOrCreateHeaderColumn("Site", "name");
	}
	
	/**
	 * Can we add a one-to-many column?
	 */
	@Test
	public void testAddOneToManyRelationship01() {
		CsvResultContext context = mock(CsvResultContext.class);
		HeaderColumn col1 = new HeaderColumn("Site.name");
		when(context.getOrCreateHeaderColumn("Site", "name")).thenReturn(col1);
		HeaderColumn col2 = new HeaderColumn("Site.species");
		when(context.getOrCreateHeaderColumn("Site", "species")).thenReturn(col2);
		Row objectUnderTest = new Row(context);
		objectUnderTest.addOneToOneRelationship(subject("Site1", "Site"), helper.createProperty("urn:test#name"), "site #1");
		objectUnderTest.addOneToManyRelationship(subject("Site1", "Site"), helper.createProperty("urn:test#species"), Arrays.asList("species one", "species two"));
		List<String> result = objectUnderTest.toCsv(new HashSet<HeaderColumn>(Arrays.asList(col1, col2)));
		assertThat(result.size(), is(2));
		assertThat(result.get(0), is("\"site #1\",\"species one\""));
		assertThat(result.get(1), is("\"site #1\",\"species two\""));
		verify(context).getOrCreateHeaderColumn("Site", "name");
		verify(context).getOrCreateHeaderColumn("Site", "species");
	}

	private Resource subject(String instanceId, String type) {
		Resource typeResource = resource(type);
		return helper.createResource(NS + instanceId, typeResource);
	}

	private Resource resource(String localName) {
		return helper.createResource(NS + localName);
	}
}
