package com.example.demo_users.controller;


import com.example.demo_users.generated.api.UsersApi;
import com.example.demo_users.generated.model.User;
import com.example.demo_users.service.UsersAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

  private final UsersAggregationService service;

  @Override
  public ResponseEntity<List<User>> listUsers() {
    return ResponseEntity.ok(service.getAll());
  }
}
