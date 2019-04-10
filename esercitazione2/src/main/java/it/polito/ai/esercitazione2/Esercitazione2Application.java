package it.polito.ai.esercitazione2;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication
public class Esercitazione2Application {

    public static void main(String[] args) {
        SpringApplication.run(Esercitazione2Application.class, args);
    }

    @Bean
    public DataSource postgreSQLDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://server-name:5432/database-name");
        ds.setUsername("admin");
        ds.setPassword("***secret***");

        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean factoryBean() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(postgreSQLDataSource());
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.schema-generation.database.action", "create");
        factory.setJpaProperties(properties);

        return factory;
    }

}
