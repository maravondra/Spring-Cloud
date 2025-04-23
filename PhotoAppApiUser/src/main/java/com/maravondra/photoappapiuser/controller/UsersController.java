package com.maravondra.photoappapiuser.controller;

import com.maravondra.photoappapiuser.model.CreateUserRequestModel;
import com.maravondra.photoappapiuser.model.CreateUserResponseModel;
import com.maravondra.photoappapiuser.service.UserServiceImpl;
import com.maravondra.photoappapiuser.shared.UserDTO;
import jakarta.validation.Valid;
import java.util.UUID;
import org.apache.catalina.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

  @Autowired
  private Environment env;

  @Autowired
  private UserServiceImpl userService;

  @GetMapping("/status/check")
  public String status(){
    return "Working on port " + env.getProperty("local.server.port") ;
  }

  @PostMapping
  public ResponseEntity<CreateUserResponseModel> createUser(
     @Valid @RequestBody CreateUserRequestModel userDetails
  ){

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    UserDTO userDTO = mapper.map(userDetails, UserDTO.class);

    UserDTO createdUser = userService.createUser(userDTO);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.map(createdUser, CreateUserResponseModel.class));
  }
}
