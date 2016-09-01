package com.github.tomsaleeba.jr2c.handler;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.Row;

@Component
public class BagStatementHandler implements StatementHandler {

	@Autowired
	private LiteralStatementHandler literalHandler;
	
	@Autowired
	private RdfTypeStatementHandler rdfTypeHandler;

	@Override
	public boolean canHandle(Statement statement) {
		Resource resource = statement.getResource();
		Statement typeStatement = resource.getProperty(RDF.type);
		if (typeStatement == null) {
			return false;
		}
		if (RDF.Bag.equals(typeStatement.getResource())) {
			return true;
		}
		return false;
	}

	@Override
	public void handle(Statement bagStatement, Row row) {
		Resource bag = bagStatement.getResource();
		List<String> values = new LinkedList<>();
		for (StmtIterator it = bag.listProperties(); it.hasNext();) {
			Statement curr = it.next();
			if (rdfTypeHandler.canHandle(curr)) {
				continue;
			}
			if (!literalHandler.canHandle(curr)) {
				continue;
			}
			// FIXME need to handle bags of more that just string literals, will need to add the chain
			values.add(literalHandler.getValue(curr));
		}
		row.addOneToManyRelationship(bagStatement.getSubject(), bagStatement.getPredicate(), values);
	}
	
	@Override
	public void setChain(StatementHandlerChain statementHandlerChain) { }
}