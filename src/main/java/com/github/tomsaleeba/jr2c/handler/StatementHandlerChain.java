package com.github.tomsaleeba.jr2c.handler;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import com.github.tomsaleeba.jr2c.Row;

public class StatementHandlerChain {

	private List<StatementHandler> chainLinks = new LinkedList<>();
	private EntityStatementHandler entityHandler;
	
	public void handle(Statement statement, Row row) {
		for (StatementHandler curr : chainLinks) {
			if (curr.canHandle(statement)) {
				curr.handle(statement, row);
				return;
			}
		}
	}

	public void handleEntity(Resource root, Row row) {
		if (entityHandler == null) {
			throw new IllegalStateException("Programmer error: no entity handler configured!");
		}
		entityHandler.handleEntity(root, row);
	}

	public void addLink(StatementHandler statementHandler) {
		chainLinks.add(statementHandler);
	}

	public void postConstruct() {
		chainLinks.add(entityHandler);
		for (StatementHandler curr : chainLinks) {
			curr.setChain(this);
		}
	}

	public void setEntityHandler(EntityStatementHandler entityHandler) {
		this.entityHandler = entityHandler;
	}
}