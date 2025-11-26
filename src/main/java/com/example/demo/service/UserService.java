package com.example.demo.service;

import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkLogin(String username, String password) {

        UserModel user = userRepository.findByUsername(username);


        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
}
