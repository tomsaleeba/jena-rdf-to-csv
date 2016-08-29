package com.github.tomsaleeba.jr2c;

public interface CsvResultContext {

	Row newRow();

	HeaderColumn getOrCreateHeaderColumn(String containerType, String propertyName);

	String[] toCsv();
}
