// AuthController.java
package com.sylphcorps.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sylphcorps.dto.AuthResponse;
import com.sylphcorps.dto.LoginRequest;
import com.sylphcorps.dto.SignupRequest;
import com.sylphcorps.model.Role;
import com.sylphcorps.model.User;
import com.sylphcorps.security.JwtUtils;
import com.sylphcorps.security.UserDetailsImpl;
import com.sylphcorps.serviceImpl.UserService;
import com.sylphcorps.serviceImpl.UserService;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signin")
    @Operation(
            summary = "Authenticate user",
            description = "Login with username/email and password to get JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Convert authorities to List<String>
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),        // <-- missing in your code
                userDetails.getFullName(),
                roles
        ));
    }


    @PostMapping("/signup")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account with username, email, password, and other details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Username or email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })


    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        Map<String, String> response = new HashMap<>();

        if (userService.existsByUsername(signUpRequest.getUsername())) {
            response.put("message", "Error: Username is already taken!");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            response.put("message", "Error: Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.createUser(signUpRequest);
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    /*@PostMapping("/signup/author")
    public ResponseEntity<?> registerAuthor(@Valid @RequestBody SignupRequest signUpRequest) {
        Map<String, String> response = new HashMap<>();

        if (userService.existsByUsername(signUpRequest.getUsername())) {
            response.put("message", "Error: Username is already taken!");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            response.put("message", "Error: Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.createUser(signUpRequest, Role.AUTHOR);
        response.put("message", "Author registered successfully!");
        return ResponseEntity.ok(response);
    }*/

}
