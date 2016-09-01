package com.github.tomsaleeba.jr2c.handler;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomsaleeba.jr2c.JenaRdfToCsvApplication.Theme;
import com.github.tomsaleeba.jr2c.Row;

/**
 * Expects the statement is a resource statement so should be configured after
 * the literal handler(s) in the chain. It's not possible to have an out of scope
 * literal so this makes sense.
 */
public class OutOfScopeStatementHandler implements StatementHandler {

	private static final Logger logger = LoggerFactory.getLogger(OutOfScopeStatementHandler.class);
	private final String namespace;
	private final String themePropertyName;
	private Set<Theme> inScopeThemes = new HashSet<>();
	
	public OutOfScopeStatementHandler(String namespace, String themePropertyName) {
		this.namespace = namespace;
		this.themePropertyName = themePropertyName;
		// FIXME turn these Themes into config
		Set<Theme> inScopeThemes = EnumSet.of(
				Theme.THEME1
//				,Theme.THEME2 // Uncomment me to include this theme
		);
		logger.info("Including the following themes: {}", inScopeThemes);
	}

	@Override
	public boolean canHandle(Statement statement) {
		Property themeProperty = statement.getModel().createProperty(namespace + themePropertyName);
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