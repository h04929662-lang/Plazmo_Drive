package com.plazmodrive.api;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@DataSourceDefinition(
        name = "java:app/jdbc/plazmodrive",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "plazmodrive",
        user = "postgres",
      password = "${ENV=PLAZMODRIVE_DB_PASSWORD}")
@Singleton
@Startup
public class DataSourceConfig {
}