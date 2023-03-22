package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;

@Controller
@RequestMapping
public class UserController {

    UserServiceImpl userService;
    RoleService roleService;
    public UserController() {}
    @Autowired
    public UserController(UserServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/user")
    public String showUser(Principal principal, Model model){
        model.addAttribute("user", userService.findUserByName(principal.getName()));
        return "user";
    }

    @GetMapping ("/admin/users")
    public String getUsers(Model model){
        model.addAttribute("users", userService.listUsers());
        return "users";
    }

    @GetMapping ("/admin/users/{id}")
    public String getUser(@PathVariable("id") int id, Model model){
        model.addAttribute("user", userService.getUser(id));
        return "userEdit";
    }

    @GetMapping ("/admin/users/new")
    public String getNew(@RequestParam(defaultValue = "") String error, Model model){
        if (!error.isEmpty()) {
            model.addAttribute("message", "This name is already taken");
        }
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("user", new User());
        return "new";
    }

    @PostMapping("/admin/users")
    public String create(@ModelAttribute User user){
        userService.add(user);
        return "redirect:/admin/users";
    }

    @GetMapping ("/admin/users/{id}/edit")
    public String editUser(@PathVariable("id") int id, Model model){
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("roles", roleService.findAll());
        return "edit";
    }

    @PatchMapping("admin/users/{id}")
    public String edit(@ModelAttribute("user") User user) {
        userService.update(user);
        return "redirect:/admin/users";
    }

    @GetMapping ("admin/users/{id}/delete")
    public String deleteUser(@PathVariable("id") int id){
        userService.delete(id);
        return "redirect:/admin/users";
    }

    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public String handleException(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("error", "double");
        return "redirect:/admin/users/new";
    }
}

