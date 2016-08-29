package com.github.tomsaleeba.jr2c;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

class Row {
	private static final String NO_VALUE_FOR_THIS_COLUMN = "";
	private static final String MAGIC_PLACEHOLDER = "%OneToManyVal%";
	private final Map<HeaderColumn, String> oneToOneValues = new HashMap<>();
	private HeaderColumn oneToManyHeader; // FIXME need to support more than a single one-to-many column
	private List<String> oneToManyValues = Collections.emptyList();
	private final CsvResultContext context;

	public Row(CsvResultContext context) {
		this.context = context;
	}

	public void addOneToOneRelationship(Resource subject, Property property, String value) {
		String subjectTypeLocalName = getTypeFrom(subject);
		HeaderColumn column = context.getOrCreateHeaderColumn(subjectTypeLocalName, property.getLocalName());
		// TODO check if we already have a value. This probably means one-to-many
		oneToOneValues.put(column, value);
	}
	
	public void addOneToManyRelationship(Resource subject, Property property, List<String> values) {
		String subjectTypeLocalName = getTypeFrom(subject);
		HeaderColumn column = context.getOrCreateHeaderColumn(subjectTypeLocalName, property.getLocalName());
		oneToManyValues = values;
		oneToManyHeader = column;
	}

	private String getTypeFrom(Resource subject) {
		Statement typeProp = subject.getProperty(RDF.type);
		// TODO check if null
		return typeProp.getResource().getLocalName();
	}

	public List<String> toCsv(Collection<HeaderColumn> columnOrder) {
		// TODO check order count == values count
		// TODO handle writing an empty column when we don't have a value
		StringBuilder resultTemplate = new StringBuilder();
		for (Iterator<HeaderColumn> it = columnOrder.iterator(); it.hasNext();) {
			HeaderColumn currCol = it.next();
			String value = getValue(currCol);
			resultTemplate.append("\"");
			resultTemplate.append(value);
			resultTemplate.append("\"");
			if (it.hasNext()) {
				resultTemplate.append(",");
			}
		}
		List<String> result = new LinkedList<>();
		if (isOneToManyNotPresent(resultTemplate)) {
			result.add(resultTemplate.toString());
			return result;
		}
		for (String curr : oneToManyValues) {
			String replacedRow = resultTemplate.toString().replace(MAGIC_PLACEHOLDER, curr);
			result.add(replacedRow);
		}
		return result;
	}

	private boolean isOneToManyNotPresent(StringBuilder row) {
		return !row.toString().contains(MAGIC_PLACEHOLDER);
	}

	private String getValue(HeaderColumn currCol) {
		String result = oneToOneValues.get(currCol);
		if (result != null) {
			return result;
		}
		if (currCol.equals(oneToManyHeader)) {
			return MAGIC_PLACEHOLDER;
		}
		return NO_VALUE_FOR_THIS_COLUMN;
	}
}
