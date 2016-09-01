package com.github.tomsaleeba.jr2c;

import java.util.EnumSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.github.tomsaleeba.jr2c.handler.BagStatementHandler;
import com.github.tomsaleeba.jr2c.handler.EntityStatementHandler;
import com.github.tomsaleeba.jr2c.handler.LiteralStatementHandler;
import com.github.tomsaleeba.jr2c.handler.OutOfScopeStatementHandler;
import com.github.tomsaleeba.jr2c.handler.RdfTypeStatementHandler;
import com.github.tomsaleeba.jr2c.handler.StatementHandlerChain;

@SpringBootApplication
@PropertySource("classpath:/com/github/tomsaleeba/jr2c/rdf2csv.properties")
public class JenaRdfToCsvApplication {

	private static final Logger logger = LoggerFactory.getLogger(JenaRdfToCsvApplication.class);
	
	@Value("${jr2c.namespace}")
	private String namespace;
	
	public enum Theme {
		THEME1,
		THEME2;
	}
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(JenaRdfToCsvApplication.class, args);
		Runner runner = context.getBean(Runner.class);
		runner.run();
	}

	@Bean
	public Model coreModel() {
		Model result = ModelFactory.createDefaultModel();
		result.setNsPrefix("", namespace);
		return result;
	}
	
	@Bean
	public StatementHandlerChain statementHandlerChain(OutOfScopeStatementHandler outOfScopeStatementHandler,
			EntityStatementHandler entityStatementHandler, RdfTypeStatementHandler rdfTypeStatementHandler,
			LiteralStatementHandler literalStatementHandler, BagStatementHandler bagStatementHandler) {
		StatementHandlerChain result = new StatementHandlerChain();
		result.addLink(rdfTypeStatementHandler);
		result.addLink(literalStatementHandler);
		result.addLink(bagStatementHandler);
		result.addLink(outOfScopeStatementHandler);
		result.setEntityHandler(entityStatementHandler);
		result.postConstruct();
		return result;
	}
	
	@Bean
	public Set<Theme> themes() {
		Set<Theme> result = EnumSet.of(
				Theme.THEME1
//				,Theme.THEME2 // Uncomment me to include this theme
		);
		logger.info("Including the following themes: {}", result);
		return result;
	}
}
