package IndirektniPSF.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Aplikacija radi sa dve fizicki odvojene, ali sematski identicne baze:
 * APV - za kvartale 1, 2 i 3, i PGODINA - za kvartale 4 i 5 (godisnji obrazac).
 * <p>
 * Ranije se ovo radilo rucno (menjao se spring.datasource.url u
 * application.properties i ponovo se deploy-ovala aplikacija - vidi
 * zakomentarisane linije koje su ostale kao trag te prakse). RoutingDataSource
 * sad automatski bira pravu bazu po zahtevu na osnovu
 * {@link KvartalDataSourceContextHolder}, koji puni {@link KvartalRoutingInterceptor}
 * citajuci "kvartal" parametar iz URL-a svakog zahteva.
 * <p>
 * VAZNO: ovo pretpostavlja da obe baze imaju identicnu semu (isti entiteti/tabele) -
 * sto je do sada i bio slucaj, jer je cela aplikacija ranije radila naizmenicno
 * sa jednom ili drugom bazom u celini, nikad sa dve istovremeno.
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.apv")
    public DataSourceProperties apvDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.pgodina")
    public DataSourceProperties pgodinaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource apvDataSource() {
        return apvDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public DataSource pgodinaDataSource() {
        return pgodinaDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.APV, apvDataSource());
        targetDataSources.put(DataSourceType.PGODINA, pgodinaDataSource());

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        // APV je podrazumevana baza - za zahteve bez "kvartal" parametra
        // (npr. login, upravljanje korisnicima) - isto ponasanje kao i danas.
        routingDataSource.setDefaultTargetDataSource(apvDataSource());
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
