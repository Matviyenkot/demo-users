package com.example.demo_users.config;

import lombok.Data;

@Data
public class DataSourceSpec {
  private String name;
  private String strategy;
  private String url;
  private String table;
  private String user;
  private String password;
  private ColumnMapping mapping;

}

