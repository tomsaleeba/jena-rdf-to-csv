package com.github.tomsaleeba.jr2c;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Runner {

	private static final Logger logger = LoggerFactory.getLogger(Runner.class);
	private static final String DATA_PROPERTY_KEY = "ttl-data";

	@Autowired
	private Environment env;
	
	@Autowired
	private ModelPopulater exampleDataPopulater;
	
	@Autowired
	private Transformer transformer;
	
	@Autowired
	private Model coreModel;
	
	public void run() {
		populateModel();
		transformer.toCsv();
	}

	private void populateModel() {
		String dataParam = env.getProperty(DATA_PROPERTY_KEY);
		if (dataParam == null) {
			logger.info("No external TTL file supplied, generating some example data instead");
			exampleDataPopulater.populate();
			return;
		}
		try {
			File f = new File(dataParam);
			if (!f.isFile()) {
				throw new IllegalStateException(String.format("The supplied path '%s' isn't a file.", dataParam));
			}
			logger.info("Loading data from the TTL file '{}'", dataParam);
			InputStream in = new FileInputStream(f);
			coreModel.read(in, null, "TURTLE");
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Cannot file the supplied file", e);
		}
	}
}
