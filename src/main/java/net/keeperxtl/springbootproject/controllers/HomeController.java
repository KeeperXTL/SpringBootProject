package net.keeperxtl.springbootproject.controllers;

import net.keeperxtl.springbootproject.DB.models.Review;
import net.keeperxtl.springbootproject.DB.models.Role;
import net.keeperxtl.springbootproject.DB.models.User;
import net.keeperxtl.springbootproject.DB.repository.ReviewRepository;
import net.keeperxtl.springbootproject.DB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String homePage(@RequestParam(name = "username", required = false, defaultValue = "Anon") String username, Map<String, Object> model) {
        model.put("title", "Главная страница");
        model.put("name", username);
        return "index";
    }

    @GetMapping("/about")
    public String aboutPage(Map<String, Object> model) {
        model.put("title", "Страница о нас");
        return "about";
    }

    @GetMapping("/reviews")
    public String reviewsPage(Map<String, Object> model) {
        Iterable<Review> reviews = reviewRepository.findAll();
        model.put("title", "Отзывы");
        model.put("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/add-review")
    public String addReviewPage(@RequestParam String title, @RequestParam String text ,Map<String, Object> model) {
        Review review = new Review(title, text);
        reviewRepository.save(review);
        return "redirect:/reviews";
    }

    @GetMapping("/reviews/{id}")
    public String reviewPage(@PathVariable(value = "id") long id, Map<String, Object> model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);

        model.put("title", review.getTitle());
        model.put("review", review);

        return "review";
    }

    @GetMapping("/reviews/{id}/update")
    public String updateReviewPage(@PathVariable(value = "id") long id, Map<String, Object> model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);

        model.put("title", review.getTitle());
        model.put("review", review);

        return "review-update";
    }

    @PostMapping("/reviews/{id}/update")
    public String updateReview(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String text ,Map<String, Object> model) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);
        review.setTitle(title);
        review.setText(text);
        reviewRepository.save(review);

        model.put("title", review.getTitle());
        model.put("review", review);

        return "redirect:/reviews/" + id;
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable(value = "id") long id) throws ClassNotFoundException{
        Review review = reviewRepository.findById(id).orElseThrow(ClassNotFoundException::new);
        reviewRepository.delete(review);

        return "redirect:/reviews";
    }

    @GetMapping("/login")
    public String loginPage(Map<String, Object> model) {
        model.put("title", "Авторизация");

        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(Map<String, Object> model) {
        model.put("title", "Регистрация");

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam String username, @RequestParam String email,
                          @RequestParam String repeatEmail, @RequestParam String password,
                          @RequestParam String repeatPassword, @RequestParam(required = false, defaultValue = "false") boolean isCar, User user, Map<String, Object> model) {
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
    public String userPage(Principal principalUser, Map<String, Object> model) {
        User user = userRepository.findByUsername(principalUser.getName());
        Role userRole = Role.USER;
        for(Role r: user.getRoles()) {
            userRole = r;
        }
        model.put("title", "Личный кабинет");
        model.put("isCar", user.isIs_car());
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
        model.put("roles", rolesSet);
        model.put("user", user);
        return "user";
    }
    @PostMapping("/update-user")
    public String addUser(User userForm,
                          @RequestParam(required = false) String repeatEmail,
                          @RequestParam(required = false) String repeatPassword,
                          Principal principalUser,
                          Map<String, Object> model) {
        User user = userRepository.findByUsername(principalUser.getName());
        user.setUsername(userForm.getUsername());
        user.setIs_car(userForm.isIs_car());
        user.setRoles(userForm.getRoles());
        if (!userForm.getEmail().equals("") && userForm.getEmail().equals(repeatEmail))
            user.setEmail(userForm.getEmail());
        if (!userForm.getPassword().equals("") && userForm.getPassword().equals(repeatPassword))
            user.setPassword(userForm.getPassword());
        userRepository.save(user);
        return "redirect:/user";
    }
}
