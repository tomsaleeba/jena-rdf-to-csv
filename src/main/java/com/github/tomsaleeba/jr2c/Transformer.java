package com.github.tomsaleeba.jr2c;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tomsaleeba.jr2c.handler.BagStatementHandler;
import com.github.tomsaleeba.jr2c.handler.EntityStatementHandler;
import com.github.tomsaleeba.jr2c.handler.LiteralStatementHandler;
import com.github.tomsaleeba.jr2c.handler.OutOfScopeStatementHandler;
import com.github.tomsaleeba.jr2c.handler.RdfTypeStatementHandler;
import com.github.tomsaleeba.jr2c.handler.StatementHandlerChain;

/**
 * Reads the RDF model and transforms it to CSV by trying to flatten it.
 * 
 * @author Tom Saleeba
 */
@Component
public class Transformer {

	static final Logger logger = LoggerFactory.getLogger(Transformer.class);
	
	private final MagicFactory factory = newFactory();
	private StatementHandlerChain chain = factory.getChainInstance();
	
	@Autowired
	private Model coreModel;
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	@Value("${jr2c.property-name.theme}")
	private String themePropertyLocalName;
	
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

	class MagicFactory {

		private StatementHandlerChain instance;

		public StatementHandlerChain getChainInstance() {
			if (instance != null) {
				return instance;
			}
			instance = new StatementHandlerChain();
			RdfTypeStatementHandler rdfTypeStatementHandler = new RdfTypeStatementHandler();
			instance.addLink(rdfTypeStatementHandler);
			LiteralStatementHandler literalStatementHandler = new LiteralStatementHandler();
			instance.addLink(literalStatementHandler);
			instance.addLink(new BagStatementHandler(literalStatementHandler, rdfTypeStatementHandler));
			instance.addLink(new OutOfScopeStatementHandler(namespace, themePropertyLocalName));
			instance.setEntityHandler(new EntityStatementHandler());
			instance.postConstruct();
			return instance;
		}
	}

	private MagicFactory newFactory() {
		return new MagicFactory();
	}
}
