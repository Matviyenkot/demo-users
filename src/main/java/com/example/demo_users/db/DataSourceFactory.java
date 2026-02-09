package com.example.demo_users.db;

import com.example.demo_users.config.DataSourceSpec;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactory {

  public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
  public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  public static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

  public HikariDataSource create(DataSourceSpec spec) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(spec.getUrl());
    ds.setUsername(spec.getUser());
    ds.setPassword(spec.getPassword());

    // Map 'strategy' to driver if needed
    String strategy = spec.getStrategy().toLowerCase();
    switch (strategy) {
      case "postgres" -> ds.setDriverClassName(POSTGRESQL_DRIVER);
      case "mysql"    -> ds.setDriverClassName(MYSQL_DRIVER);
      case "oracle"   -> ds.setDriverClassName(ORACLE_DRIVER);
      default -> throw new IllegalArgumentException("Unsupported strategy: " + strategy);
    }

    ds.setMaximumPoolSize(5);
    ds.setPoolName("ds-" + spec.getName());
    return ds;
  }
}
