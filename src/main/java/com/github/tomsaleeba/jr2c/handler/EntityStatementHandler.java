package com.github.tomsaleeba.jr2c.handler;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.Row;

@Component
public class EntityStatementHandler implements StatementHandler {
	
	private StatementHandlerChain chain;

	@Override
	public void handle(Statement statement, Row row) {
		Resource entity = statement.getResource();
		handleEntity(entity, row);
	}
	
	public void handleEntity(Resource entity, Row row) {
		for (Statement curr : entity.listProperties().toList()) {
			chain.handle(curr, row);
		}
	}

	@Override
	public boolean canHandle(Statement statement) {
		try {
			statement.getResource();
			return true;
		} catch (ResourceRequiredException e) {
			return false;
		}
	}

	@Override
	public void setChain(StatementHandlerChain statementHandlerChain) {
		this.chain = statementHandlerChain;
	}
}