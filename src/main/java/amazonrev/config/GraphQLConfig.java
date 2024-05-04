package amazonrev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import graphql.scalars.ExtendedScalars;

// ref: https://github.com/graphql-java/graphql-java-extended-scalars
@Configuration
public class GraphQLConfig {
  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return wiringBuilder -> wiringBuilder
        .scalar(ExtendedScalars.GraphQLLong)
        .scalar(ExtendedScalars.DateTime)
        .scalar(ExtendedScalars.Json);
  }
}
