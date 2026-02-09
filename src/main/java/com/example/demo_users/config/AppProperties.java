package com.example.demo_users.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private List<DataSourceSpec> dataSources;

  public List<DataSourceSpec> getDataSources() { return dataSources; }
  public void setDataSources(List<DataSourceSpec> dataSources) { this.dataSources = dataSources; }

}
