package com.example.demo_users.db;

import com.example.demo_users.config.AppProperties;
import com.example.demo_users.config.DataSourceSpec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JdbcTemplateFactory {

  private final Map<String, JdbcTemplate> templates = new LinkedHashMap<>();

  public JdbcTemplateFactory(AppProperties props, DataSourceFactory dsFactory) {
    for (DataSourceSpec spec : props.getDataSources()) {
      var ds = dsFactory.create(spec);
      templates.put(spec.getName(), new JdbcTemplate(ds));
    }
  }

  public Map<String, JdbcTemplate> getAll() {
    return templates;
  }
}