package com.github.tomsaleeba.jr2c.handler;

import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.JenaRdfToCsvApplication.Theme;
import com.github.tomsaleeba.jr2c.Row;

/**
 * Expects the statement is a resource statement so should be configured after
 * the literal handler(s) in the chain. It's not possible to have an out of scope
 * literal so this makes sense.
 */
@Component
public class OutOfScopeStatementHandler implements StatementHandler {

	@Autowired
	private Set<Theme> inScopeThemes;
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	@Value("${jr2c.property-name.theme}")
	private String themePropertyLocalName;
	
	@Override
	public boolean canHandle(Statement statement) {
		Property themeProperty = statement.getModel().createProperty(namespace + themePropertyLocalName);
		Resource otherEntity = statement.getResource();
		Statement themeStatement = otherEntity.getProperty(themeProperty);
		boolean isNoTheme = themeStatement == null;
		if (isNoTheme) {
			return false;
		}
		boolean isInScope = inScopeThemes.stream().anyMatch(e -> e.toString().equals(themeStatement.getString()));
		if (isInScope) {
			return false;
		}
		return true;
	}

	@Override
	public void handle(Statement statement, Row row) {
		// do nothing, just consume the statement
	}
	
	@Override
	public void setChain(StatementHandlerChain statementHandlerChain) { }
}