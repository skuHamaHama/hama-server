package likelion.hamahama.user.service;
import likelion.hamahama.user.entity.User;
import likelion.hamahama.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import likelion.hamahama.user.dto.SignRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public List<User> findAll(){

        return userRepository.findAll();
    }
    @Transactional
    public User findUserOne(String email){
        Optional<User> user = userRepository.findByEmail(email);

        return user.get();
    }
    @Transactional
    public void updateUser(String email, SignRequest request){

        Optional<User> user = userRepository.findByEmail(email);

        user.get().setNickname(request.getNickname());
        user.get().setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user.get());

    }
    @Transactional
    public void deleteUser(String email){

        userRepository.deleteByEmail(email);

    }
}