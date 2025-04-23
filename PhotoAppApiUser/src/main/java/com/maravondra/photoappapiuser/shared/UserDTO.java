package com.maravondra.photoappapiuser.shared;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserDTO implements Serializable {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String userId;
  private String encryptedPassword;
}
