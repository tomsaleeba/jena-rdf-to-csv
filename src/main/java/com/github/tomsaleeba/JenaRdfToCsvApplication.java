package com.github.tomsaleeba;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JenaRdfToCsvApplication {

	public static final String NAMESPACE = "urn:github.com/tomsaleeba#";
	public static final String ROOT_ENTITY_LOCAL_NAME = "the_root";
	public static final String THEME_PROPERTY_LOCAL_NAME = "theme";
	
	public enum Theme {
//		IN_SCOPE, // Can probably imply this
		OUT_OF_SCOPE;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(JenaRdfToCsvApplication.class, args);
	}
	
	@Bean
	public Model coreModel() {
		Model result = ModelFactory.createDefaultModel();
		result.setNsPrefix("", NAMESPACE);
		return result;
	}
}
