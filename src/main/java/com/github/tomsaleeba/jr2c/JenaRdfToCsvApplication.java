package com.github.tomsaleeba.jr2c;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/com/github/tomsaleeba/jr2c/rdf2csv.properties")
public class JenaRdfToCsvApplication {

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
}
