package com.github.tomsaleeba.jr2c.handler;

import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;

import com.github.tomsaleeba.jr2c.Row;

public class LiteralStatementHandler implements StatementHandler {

	@Override
	public boolean canHandle(Statement statement) {
		try {
			statement.getResource();
			return false;
		} catch (ResourceRequiredException e) {
			return true;
		}
	}

	@Override
	public void handle(Statement statement, Row row) {
		row.addOneToOneRelationship(statement.getSubject(), statement.getPredicate(), getValue(statement));
	}

	String getValue(Statement statement) {
		return statement.getString(); // FIXME handle more than just String
	}
	
	@Override
	public void setChain(StatementHandlerChain statementHandlerChain) { }
}