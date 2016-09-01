package com.github.tomsaleeba.jr2c.handler;

import org.apache.jena.rdf.model.Statement;

import com.github.tomsaleeba.jr2c.Row;

interface StatementHandler {

	boolean canHandle(Statement statement);

	void handle(Statement statement, Row row);

	void setChain(StatementHandlerChain statementHandlerChain);
}