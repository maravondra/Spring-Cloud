package com.maravondra.photoappapiuser.service;

import com.maravondra.photoappapiuser.data.UserEntity;
import com.maravondra.photoappapiuser.repository.UserRepository;
import com.maravondra.photoappapiuser.shared.UserDTO;
import java.util.ArrayList;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDTO createUser(UserDTO userDetails) {
    userDetails.setUserId(UUID.randomUUID().toString());
    userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    UserEntity userEntity = mapper.map(userDetails, UserEntity.class);

    userRepository.save(userEntity);

    UserDTO returnedValue = mapper.map(userEntity, UserDTO.class);

    return returnedValue;
  }

  @Override
  public UserDTO getUserDetailsByEmail(String email) {
    UserEntity byEmail = userRepository.findByEmail(email);
    if(byEmail == null) {
      throw new UsernameNotFoundException(email);
    }

    return new ModelMapper().map(byEmail, UserDTO.class);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity byEmail = userRepository.findByEmail(username);
    if(byEmail == null) {
      throw new UsernameNotFoundException(username);
    }
    return new User(
        byEmail.getEmail(),
        byEmail.getEncryptedPassword(),
        true, true, true, true,
        new ArrayList<>()
    );
    }

}
