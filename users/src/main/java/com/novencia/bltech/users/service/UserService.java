package com.novencia.bltech.users.service;

import com.novencia.bltech.users.entity.RedditGalleryUser;
import com.novencia.bltech.users.exception.EmailAlreadyExistsException;
import com.novencia.bltech.users.exception.UsernameAlreadyExistsException;
import com.novencia.bltech.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final IdpService idpService;

    public UserService(UserRepository userRepository, IdpService idpService) {
        this.userRepository = userRepository;
        this.idpService = idpService;
    }

    public Mono<RedditGalleryUser> registerUser(RedditGalleryUser user, String rawPassword) {
        return userRepository.existsById(user.getUsername())
                .doOnNext(exists -> throwExIfUsernameExists(exists, user.getUsername()))
                .flatMap(b -> userRepository.existsByEmail(user.getEmail()))
                .doOnNext(exists -> throwExIfEmailExists(exists, user.getEmail()))
                .flatMap(exists -> idpService.registerUser(user, rawPassword))
                .flatMap(userRepository::save);
    }

    private void throwExIfUsernameExists(boolean exists, String username) {
        if (exists) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void throwExIfEmailExists(boolean exists, String email) {
        if (exists) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
