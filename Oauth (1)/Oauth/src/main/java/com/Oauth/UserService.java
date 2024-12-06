package com.Oauth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
     private UserDao userDao;

     public User createUser(User user){
        
        return userDao.save(user);
     }

     public User findByEmail(String email){
        return userDao.findByEmail(email).orElse(null);
     }
     public User updateToken(User user, String token){
       user.setAccessToken(token);
       return userDao.save(user);
     }

     public User registerNewUser(User user){
      return userDao.save(user);
     }
}
