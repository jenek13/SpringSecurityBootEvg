package com.ten.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.ten.model.Role;
import com.ten.model.User;
import com.ten.service.RoleService;
import com.ten.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller("admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public AdminController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping(value = {"/"})
    public String redirectToLoginPage() {
        return "redirect:/login";
        }

    @GetMapping(value = {"/login"})
    public String showLoginPage()  {
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<User> listUsers = userService.listUser();
        model.addAttribute("users", listUsers);
        return "admin";

    }

   @GetMapping("/admin/addUser")
    public String addUser(Model model) {
       User user = new User();
       //user.setRoles(getRoles(role.getName()));
       model.addAttribute("user", user);
       //model.addAttribute("login",user.getLogin());
       //model.addAttribute("password",user.getPassword());
       //model.addAttribute("role", role.getName());
       return "addUser";
    }

   @PostMapping(value = {"/admin/addUser"})
    public String addUser(@RequestParam("login") String login, @RequestParam("password") String password) {
        User user = new User(login, password, true);
        userService.insertUser(user);
        return "redirect:/admin";
    }





    @GetMapping(value = {"/admin/edit/{id}"})
    public ModelAndView editUser(@PathVariable("id") Long id)  {
        ModelAndView model = new ModelAndView("edit");
        model.addObject("user", userService.selectUser(id));
        return model;
    }

    @PostMapping(value = "/admin/update/{id}")
    public String editUser(@ModelAttribute("user") User user, HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("utf-8");
        userService.updateUser(new User(user.getId(), user.getLogin(),user.getPassword(),true));

        //user.setRoles(getRoles(role.getName()));
        userService.updateUser(user);
        return "redirect:/admin";
    }



    @GetMapping(value = "/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping(value = {"/user"})
    public ModelAndView userPage() {
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        String username = user.getName();
        ModelAndView model = new ModelAndView("user");
        model.addObject("user", user);
        return model;
    }

    @GetMapping(value = "/error")
    public String accessDenied() {
        return "error";
    }

    private Set<Role> getRoles(String role) {
        Set<Role> roles = new HashSet<>();

        switch (role) {
            case "admin":
                roles.add(roleService.getRoleById(1L));
                break;
            case "user":
                roles.add(roleService.getRoleById(2L));
                break;
            case "admin, user":
                roles.add(roleService.getRoleById(1L));
                roles.add(roleService.getRoleById(2L));
                break;
            default:
                roles.add(roleService.getRoleById(2L));
                break;
        }

        return roles;
    }

}
