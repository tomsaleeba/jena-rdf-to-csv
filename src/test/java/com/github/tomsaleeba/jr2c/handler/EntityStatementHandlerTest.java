package com.github.tomsaleeba.jr2c.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.junit.Test;

import com.github.tomsaleeba.jr2c.CsvResultContext;
import com.github.tomsaleeba.jr2c.ImprovisedCsvResultContext;
import com.github.tomsaleeba.jr2c.Row;

public class EntityStatementHandlerTest {

	/**
	 * Can we tell that we can't handle a literal statement?
	 */
	@Test
	public void testCanHandle01() {
		EntityStatementHandler objectUnderTest = new EntityStatementHandler();
		Statement statement = getLiteralStatement();
		boolean result = objectUnderTest.canHandle(statement);
		assertFalse("Literal statement shouldn't be handleable", result);
	}
	
	/**
	 * Can we tell that we can handle a resource statement?
	 */
	@Test
	public void testCanHandle02() {
		EntityStatementHandler objectUnderTest = new EntityStatementHandler();
		Statement statement = getResourceStatement();
		boolean result = objectUnderTest.canHandle(statement);
		assertTrue("Resource statement should be handleable", result);
	}
	
	/**
	 * Can we handle a resource statement?
	 */
	@Test
	public void testHandle01() {
		EntityStatementHandler objectUnderTest = new EntityStatementHandler();
		Statement statement = getResourceStatement();
		Property prop = statement.getModel().createProperty("urn:favourite_color");
		statement.getResource().addLiteral(prop, "orange");
		Statement expectedStatement = statement.getResource().getProperty(prop);
		CsvResultContext context = new ImprovisedCsvResultContext();
		Row row = new Row(context);
		StatementHandlerChain statementHandlerChain = mock(StatementHandlerChain.class);
		objectUnderTest.setChain(statementHandlerChain);
		objectUnderTest.handle(statement, row);
		verify(statementHandlerChain).handle(expectedStatement, row);
		verifyNoMoreInteractions(statementHandlerChain);
	}

	private Statement getResourceStatement() {
		Model model = ModelFactory.createDefaultModel();
		Resource subject = model.createResource("urn:sub1");
		Property property = model.createProperty("urn:someProp");
		Resource object = model.createResource("urn:sub2");
		subject.addProperty(property, object);
		return subject.getProperty(property);
	}

	private Statement getLiteralStatement() {
		Model model = ModelFactory.createDefaultModel();
		Resource subject = model.createResource("urn:sub1");
		Property property = model.createProperty("urn:someProp");
		subject.addLiteral(property, "blah");
		return subject.getProperty(property);
	}
}
