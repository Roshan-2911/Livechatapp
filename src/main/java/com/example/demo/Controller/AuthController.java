package com.example.demo.Controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    // Keep track of logged in users
    private Set<String> onlineUsers = new HashSet<>();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	/*
	 * @PostMapping("/login") public Map<String, Object> login(@RequestBody
	 * Map<String, String> credentials) { String username =
	 * credentials.get("username"); String password = credentials.get("password");
	 * 
	 * Optional<User> user = userRepository.findByUsernameAndPassword(username,
	 * password); if (user.isPresent()) { onlineUsers.add(username); Map<String,
	 * Object> resp = new HashMap<>(); resp.put("status", "success");
	 * resp.put("onlineUsers", onlineUsers); return resp; } return Map.of("status",
	 * "fail"); }
	 */
    
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getPassword().equals(password)) {
                // ✅ Successful login
                onlineUsers.add(username);

                Map<String, Object> resp = new HashMap<>();
                resp.put("status", "success");
                resp.put("message", "Login successful");
                resp.put("onlineUsers", onlineUsers);
                return resp;
            } else {
                // ❌ Password mismatch
                return Map.of("status", "fail", "message", "Invalid password");
            }
        }

        // 🆕 Register new user and login
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);

        onlineUsers.add(username);

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("newUser", true);
        resp.put("message", "New user registered and logged in");
        resp.put("onlineUsers", onlineUsers);
        return resp;
    }

    

    @GetMapping("/online-users")
    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }
}
