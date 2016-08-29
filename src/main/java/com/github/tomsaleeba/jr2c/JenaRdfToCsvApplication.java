package com.github.tomsaleeba.jr2c;

import java.util.EnumSet;
import java.util.Set;

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
		ModelPopulater populater = context.getBean(ModelPopulater.class);
		populater.populate();
		Transformer transformer = context.getBean(Transformer.class);
		Set<Theme> inScopeThemes = EnumSet.of(
				Theme.THEME1
				,Theme.THEME2 // Uncomment me to include this theme
		);
		transformer.setInScopeThemes(inScopeThemes);
		transformer.toCsv();
	}
	
	@Bean
	public Model coreModel() {
		Model result = ModelFactory.createDefaultModel();
		result.setNsPrefix("", namespace);
		return result;
	}
}
