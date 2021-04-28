package com.novencia.bltech.users.controller;

import com.novencia.bltech.users.dto.RegisteredUserDto;
import com.novencia.bltech.users.dto.RegistrationResponseDto;
import com.novencia.bltech.users.dto.UserDto;
import com.novencia.bltech.users.entity.RedditGalleryUser;
import com.novencia.bltech.users.exception.EmailAlreadyExistsException;
import com.novencia.bltech.users.exception.UserRegistrationException;
import com.novencia.bltech.users.exception.UsernameAlreadyExistsException;
import com.novencia.bltech.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserRestController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PermitAll
    @PostMapping
    public Mono<RegistrationResponseDto> registerUser(@Valid @RequestBody Mono<UserDto> userDto) {

        return userDto
                .doOnNext(dto -> log.info("Received POST /users with data: {}", dto))
                .map(dto -> Tuples.of(modelMapper.map(dto, RedditGalleryUser.class), dto.getPassword()))
                .flatMap(tuple -> userService.registerUser(tuple.getT1(), tuple.getT2()))
                .map(newUser -> modelMapper.map(newUser, RegisteredUserDto.class))
                .map(RegistrationResponseDto::new)
                .onErrorResume(WebExchangeBindException.class, e -> Mono.just(new RegistrationResponseDto(e.getAllErrors())))
                .onErrorResume(UsernameAlreadyExistsException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())))
                .onErrorResume(EmailAlreadyExistsException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())))
                .onErrorResume(UserRegistrationException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())));
    }
}
