package com.example.demo_users.repository.impl;

import com.example.demo_users.config.AppProperties;
import com.example.demo_users.config.DataSourceSpec;
import com.example.demo_users.db.JdbcTemplateFactory;
import com.example.demo_users.dto.UserDto;
import com.example.demo_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

  private final AppProperties props;
  private final JdbcTemplateFactory factory;
  private final ExecutorService pool;

  @Override
  public List<UserDto> findAllFromAllSources() {
    List<Callable<List<UserDto>>> tasks = new ArrayList<>();

    for (DataSourceSpec spec : props.getDataSources()) {
      var jdbc = factory.getAll().get(spec.getName());
      final String sql = buildSelectSql(spec);
      tasks.add(() -> jdbc.query(sql, rowMapper()));
    }

    try {
      return pool.invokeAll(tasks).stream()
        .flatMap(f -> {
          try { return f.get().stream(); }
          catch (Exception e) { throw new CompletionException(e); }
        })
        .collect(Collectors.toList());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while querying sources", e);
    }
  }


  private String buildSelectSql(DataSourceSpec spec) {
    String t = spec.getTable();
    var m = spec.getMapping();
    return "SELECT "
      + m.getId() + " AS id, "
      + m.getUsername() + " AS username, "
      + m.getName() + " AS name, "
      + m.getSurname() + " AS surname "
      + "FROM " + t;
  }

  private RowMapper<UserDto> rowMapper() {
    return (rs, rowNum) -> new UserDto(
      rs.getString("id"),
      rs.getString("username"),
      rs.getString("name"),
      rs.getString("surname")
    );
  }
}