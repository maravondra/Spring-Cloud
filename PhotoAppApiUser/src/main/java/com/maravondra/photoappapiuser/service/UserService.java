package com.maravondra.photoappapiuser.service;

import com.maravondra.photoappapiuser.shared.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  UserDTO createUser(UserDTO userDetails);

  UserDTO getUserDetailsByEmail(String email);
}
