package com.maravondra.photoappapiuser.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestModel {

  @NotNull(message = "First name cannot be null")
  @Size(min = 2, message = "First name must be at least 2 characters")
  private String firstName;

  @NotNull(message = "Last name cannot be null")
  @Size(min = 2, message = "Last name must be at least 2 characters")
  private String lastName;

  @NotNull(message = "Email cannot be null")
  @Email
  private String email;

  @NotNull(message = "Passwor cannot be null")
  @Size(min = 8, max=16, message = "Password must be at between 8 and 16 characters")
  private String password;

}
