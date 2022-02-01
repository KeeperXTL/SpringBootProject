package net.keeperxtl.springbootproject.controllers;

import net.keeperxtl.springbootproject.DB.models.Review;
import net.keeperxtl.springbootproject.DB.models.Role;
import net.keeperxtl.springbootproject.DB.models.User;
import net.keeperxtl.springbootproject.DB.repository.ReviewRepository;
import net.keeperxtl.springbootproject.DB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String homePage(@RequestParam(name = "username", required = false, defaultValue = "Anon") String username, Model model) {
        model.addAttribute("title", "Главная страница");
        model.addAttribute("name", username);
        return "index";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("title", "Страница о нас");
        return "about";
    }

    @GetMapping("/reviews")
    public String reviewsPage(Model model) {
        Iterable<Review> reviews = reviewRepository.findAll();
        model.addAttribute("title", "Отзывы");
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/add-review")
    public String addReviewPage(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String text) {
        Review review = new Review(title, text, user);
        reviewRepository.save(review);
        return "redirect:/reviews";
    }

    @GetMapping("/reviews/{id}")
    public String reviewPage(@PathVariable(value = "id") long id, Model model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);

        model.addAttribute("title", review.getTitle());
        model.addAttribute("review", review);

        return "review";
    }

    @GetMapping("/reviews/{id}/update")
    public String updateReviewPage(@PathVariable(value = "id") long id, Model model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);

        model.addAttribute("title", review.getTitle());
        model.addAttribute("review", review);

        return "review-update";
    }

    @PostMapping("/reviews/{id}/update")
    public String updateReview(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String text , Model model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);
        review.setTitle(title);
        review.setText(text);
        reviewRepository.save(review);

        model.addAttribute("title", review.getTitle());
        model.addAttribute("review", review);

        return "redirect:/reviews/" + id;
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable(value = "id") long id) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);
        reviewRepository.delete(review);

        return "redirect:/reviews";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Авторизация");

        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("title", "Регистрация");

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam String username, @RequestParam String email,
                          @RequestParam String repeatEmail, @RequestParam String password,
                          @RequestParam String repeatPassword, @RequestParam(required = false, defaultValue = "false") boolean isCar, User user) {
        if (email.equals(repeatEmail) && password.equals(repeatPassword)) {
            user.setRoles(Collections.singleton(Role.USER));
            user.setEnabled(true);
            user.setIs_car(isCar);
            userRepository.save(user);
            return "redirect:/login";
        } else {
            return "redirect:/registration?error";
        }
    }
    @GetMapping("/user")
    public String userPage(@AuthenticationPrincipal User principalUser, Model model) {
        Optional<User> foundedUser = userRepository.findById(principalUser.getId());
        User user = foundedUser.get();
        Role userRole = Role.USER;
        for(Role r: user.getRoles()) {
            userRole = r;
        }
        model.addAttribute("title", "Личный кабинет");
        model.addAttribute("isCar", user.isIs_car());
        Map<Role, Boolean> roles = new HashMap<>();
        for (Role r : Role.values()) {
            if (r.equals(userRole)) {
                roles.put(r, true);
            }
            else {
                roles.put(r, false);
            }
        }
        Set<Map.Entry<Role, Boolean>> rolesSet = roles.entrySet();
        model.addAttribute("roles", rolesSet);
        model.addAttribute("user", user);
        return "user-account";
    }
    @PostMapping("/update-user")
    public String addUser(User userForm,
                          @RequestParam(required = false) String repeatEmail,
                          @RequestParam(required = false) String repeatPassword,
                          @AuthenticationPrincipal User principalUser) {
        Optional<User> foundedUser = userRepository.findById(principalUser.getId());
        User user = foundedUser.get();
        user.setUsername(userForm.getUsername());
        user.setIs_car(userForm.isIs_car());
        user.setRoles(userForm.getRoles());
        if (!userForm.getEmail().equals("") && userForm.getEmail().equals(repeatEmail))
            user.setEmail(userForm.getEmail());
        if (!userForm.getPassword().equals("") && userForm.getPassword().equals(repeatPassword))
            user.setPassword(userForm.getPassword());
        userRepository.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/user";
    }

    @GetMapping("/admin-panel")
    public String adminPanelPage(Model model) {
        List<User> users = userRepository.findAll();

        model.addAttribute("title", "Панель администратора");
        model.addAttribute("users", users);

        return "admin-panel";
    }

    @GetMapping("/admin-panel/user/{username}")
    public String userInfo(@PathVariable(value = "username") String username, Model model) {
        User user = userRepository.findByUsername(username);

        model.addAttribute("title", "Панель администратора");
        model.addAttribute("user", user);

        return "user";
    }

    @GetMapping("/admin-panel/user/{username}/ban")
    public String banUser(@PathVariable(value = "username") String username, Model model) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);

        model.addAttribute("title", "Панель администратора");

        return "redirect:/admin-panel";
    }

    @GetMapping("/admin-panel/user/{username}/unban")
    public String unbanUser(@PathVariable(value = "username") String username, Model model) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);

        model.addAttribute("title", "Панель администратора");

        return "redirect:/admin-panel";
    }
}
