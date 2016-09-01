package com.github.tomsaleeba.jr2c;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.handler.StatementHandlerChain;

/**
 * Reads the RDF model and transforms it to CSV by trying to flatten it.
 * 
 * @author Tom Saleeba
 */
@Component
public class Transformer {

	private static final Logger logger = LoggerFactory.getLogger(Transformer.class);
	
	@Autowired
	private StatementHandlerChain chain;
	
	@Autowired
	private Model coreModel;
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	@Value("${root-entity}")
	private String rootEntityUri;
	
	public void toCsv() {
		CsvResultContext csvResultContext = new ImprovisedCsvResultContext();
		logger.info("Using '{}' as the root entity URI", rootEntityUri);
		Resource root = coreModel.createResource(rootEntityUri);
		Row row = csvResultContext.newRow();
		chain.handleEntity(root, row);
		logger.info("== Start CSV result ==");
		String[] csvLines = csvResultContext.toCsv();
		for (String curr : csvLines) {
			System.out.println(curr);
		}
		logger.info("== End CSV result ==");
	}
}
