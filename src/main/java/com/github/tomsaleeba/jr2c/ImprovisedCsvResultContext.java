package com.github.tomsaleeba.jr2c;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovisedCsvResultContext implements CsvResultContext {

	private static final Logger logger = LoggerFactory.getLogger(ImprovisedCsvResultContext.class);
	private final List<Row> interimRows = new LinkedList<>();
	private final Map<String, HeaderColumn> headers = new HashMap<>();

	@Override
	public Row newRow() {
		Row result = new Row(this);
		interimRows.add(result);
		return result;
	}

	@Override
	public HeaderColumn getOrCreateHeaderColumn(String containerType, String propertyName) {
		// TODO clean input for spaces, etc
		String colName = containerType + "." + propertyName;
		HeaderColumn result = headers.get(colName);
		if (result == null) {
			logger.debug("Creating a new column '{}'", colName);
			result = new HeaderColumn(colName);
			headers.put(colName, result);
		}
		return result;
	}

	@Override
	public String[] toCsv() {
		List<String> interimResult = new LinkedList<>();
		Optional<String> headerOptional = headers.keySet().stream().reduce((t, u) -> t + "," + u);
		if (!headerOptional.isPresent()) {
			return interimResult.toArray(new String[] {});
		}
		String header = headerOptional.get();
		interimResult.add(header);
		for (Row curr : interimRows) {
			// FIXME we can probably parallel this to do multiple rows at once
			List<String> currFinalRows = curr.toCsv(headers.values());
			interimResult.addAll(currFinalRows);
		}
		return interimResult.toArray(new String[] {});
	}
}
