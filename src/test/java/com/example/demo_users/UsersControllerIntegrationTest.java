package com.example.demo_users;

import com.example.demo_users.generated.model.User;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersControllerIntegrationTest {

  @Container
  static PostgreSQLContainer<?> db1 =
    new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("ds1")
      .withUsername("testuser")
      .withPassword("testpass");

  @Container
  static PostgreSQLContainer<?> db2 =
    new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("ds2")
      .withUsername("testuser")
      .withPassword("testpass");


  static {
    db1.start();
    db2.start();
  }


  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;


  @BeforeAll
  void startContainers() {
    Startables.deepStart(Stream.of(db1, db2)).join();
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry reg) {
    reg.add("app.data-sources[0].name", () -> "data-base-1");
    reg.add("app.data-sources[0].strategy", () -> "postgres");
    reg.add("app.data-sources[0].url", db1::getJdbcUrl);
    reg.add("app.data-sources[0].table", () -> "users");
    reg.add("app.data-sources[0].user", db1::getUsername);
    reg.add("app.data-sources[0].password", db1::getPassword);
    reg.add("app.data-sources[0].mapping.id", () -> "user_id");
    reg.add("app.data-sources[0].mapping.username", () -> "login");
    reg.add("app.data-sources[0].mapping.name", () -> "first_name");
    reg.add("app.data-sources[0].mapping.surname", () -> "last_name");

    reg.add("app.data-sources[1].name", () -> "data-base-2");
    reg.add("app.data-sources[1].strategy", () -> "postgres");
    reg.add("app.data-sources[1].url", db2::getJdbcUrl);
    reg.add("app.data-sources[1].table", () -> "user_table");
    reg.add("app.data-sources[1].user", db2::getUsername);
    reg.add("app.data-sources[1].password", db2::getPassword);
    reg.add("app.data-sources[1].mapping.id", () -> "ldap_login");
    reg.add("app.data-sources[1].mapping.username", () -> "ldap_login");
    reg.add("app.data-sources[1].mapping.name", () -> "name");
    reg.add("app.data-sources[1].mapping.surname", () -> "surname");
  }

  @BeforeAll
  static void init() throws Exception {
    try (var c = DriverManager.getConnection(db1.getJdbcUrl(), db1.getUsername(),
      db1.getPassword())) {
      c.createStatement().execute("""
          CREATE TABLE users (
            user_id    VARCHAR PRIMARY KEY,
            login      VARCHAR,
            first_name VARCHAR,
            last_name  VARCHAR
          );
          INSERT INTO users (user_id, login, first_name, last_name)
          VALUES ('1','user1','Name1','Surname1');
        """);
    }
    try (var c = DriverManager.getConnection(db2.getJdbcUrl(), db2.getUsername(),
      db2.getPassword())) {
      c.createStatement().execute("""
          CREATE TABLE user_table (
            ldap_login VARCHAR PRIMARY KEY,
            name       VARCHAR,
            surname    VARCHAR
          );
          INSERT INTO user_table (ldap_login, name, surname)
          VALUES ('2','Name2','Surname2'),
                 ('3','Name3','Surname3');
        """);
    }
  }

  @Test
  void shouldReturnMergedUsersFromAllSources() throws Exception {
    var json = mvc.perform(get("/users"))
      .andExpect(status().isOk())
      .andReturn().getResponse().getContentAsString();

    List<User> users = Arrays.asList(mapper.readValue(json, User[].class));
    assertThat(users).hasSize(3)
      .extracting(User::getId)
      .containsExactlyInAnyOrder("1", "2", "3");
  }

}