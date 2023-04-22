package com.authorization.server.controller;

import com.authorization.server.dao.UserDao;
import com.authorization.server.entity.User;
import com.authorization.server.mapper.DtoMapperService;
import com.authorization.server.model.AuthRequest;
import com.authorization.server.util.JwtTokenUtil;
import com.authorization.server.view.UserView;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//@Api(tags = "Authentication")
@RestController
@RequestMapping(path = "/api/public")
public class AuthApiController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("sign-in")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), request.getPassword()
                            )
                    );

            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenUtil.generateToken(user)
                    )
                    .body("login successfully");

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid User user, BindingResult bindingResult) {

        if (userDao.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userDao.save(user);

        return ResponseEntity.accepted().body(user);
    }

}
