package com.ten.service;

import com.ten.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.ten.dao.UserDAO;
import com.ten.model.User;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public UserServiceImpl() {
    }

    @Autowired
    private UserDAO userDAO;

    @Override
    public User selectUser(long id)  {
        try {
            return  userDAO.getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public List<User> listUser() {
        return userDAO.selectAllUsers();
    }

    @Override
    public void deleteUser(long id)  {
        try {
            userDAO.removeUser(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertUser(User user) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userDAO.addUser(user);

        //String encodedPassword = bCryptPasswordEncoder.encode(password);
    }

    @Override
    public void updateUser(User user)  {
        try {
            userDAO.editUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUserByLogin(String login) {
        return userDAO.getUserByLogin(login);
    }



}
