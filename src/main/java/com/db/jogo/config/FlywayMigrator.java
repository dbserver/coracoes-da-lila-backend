package com.db.jogo.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class FlywayMigrator {

    @Bean(initMethod = "migrate")
    public Flyway migrateLila(){
       Flyway flyway = new Flyway();
       flyway.setDataSource(getDataSource());
       flyway.setLocations("classpath:db/lila/migrations");
       flyway.setTable("historico_migracoes");
       flyway.setBaselineOnMigrate(true);
       flyway.setBaselineVersionAsString("0");

        return flyway;
    }

    @DependsOn("migrateLila")
    @Bean(initMethod = "migrate")
    public Flyway seedLila(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(getDataSource());
        flyway.setLocations("classpath:db/lila/data");
        flyway.setTable("historico_insercoes");
        flyway.setBaselineOnMigrate(true);
        flyway.setBaselineVersionAsString("0");

        return flyway;
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }
}
