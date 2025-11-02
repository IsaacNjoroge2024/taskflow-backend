package com.taskflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PreDestroy;
import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.taskflow.task.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system");
    }

    // Production database configuration
    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    private HikariDataSource dataSource;

    @Bean
    @Profile("prod")
    public HikariDataSource dataSource() {
        if (databaseUrl == null) {
            return null; // Will fallback to auto-configuration for dev environment
        }

        // Parse the URL manually instead of using URI
        // Format: postgresql://username:password@host/database

        String cleanUrl = databaseUrl.substring("postgresql://".length());
        String userInfo = cleanUrl.substring(0, cleanUrl.indexOf('@'));
        String username = userInfo.substring(0, userInfo.indexOf(':'));
        String password = userInfo.substring(userInfo.indexOf(':') + 1);

        String hostDb = cleanUrl.substring(cleanUrl.indexOf('@') + 1);
        String host = hostDb.contains("/") ? hostDb.substring(0, hostDb.indexOf('/')) : hostDb;
        String database = hostDb.contains("/") ? hostDb.substring(hostDb.indexOf('/') + 1) : "";

        // Construct a valid JDBC URL with default PostgreSQL port 5432
        String jdbcUrl = "jdbc:postgresql://" + host + ":5432/" + database;

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");

        // Connection pool settings for Render's free tier
        dataSource.setMaximumPoolSize(3);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(30000);

        return dataSource;
    }

    @PreDestroy
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}