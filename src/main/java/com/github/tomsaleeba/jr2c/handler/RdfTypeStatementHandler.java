package com.github.tomsaleeba.jr2c.handler;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.github.tomsaleeba.jr2c.Row;

public class RdfTypeStatementHandler implements StatementHandler {

	@Override
	public boolean canHandle(Statement statement) {
		if (statement == null || statement.getPredicate() == null) {
			toString();
		}
		if (RDF.type.equals(statement.getPredicate())) {
			return true;
		}
		return false;
	}

	@Override
	public void handle(Statement statement, Row row) {
		// do nothing, just consume the statement
	}
	
	@Override
	public void setChain(StatementHandlerChain statementHandlerChain) { }
}