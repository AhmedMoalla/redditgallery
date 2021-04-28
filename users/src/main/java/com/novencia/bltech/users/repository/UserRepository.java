package com.novencia.bltech.users.repository;

import com.novencia.bltech.users.entity.RedditGalleryUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Repository
public interface UserRepository extends ReactiveCrudRepository<RedditGalleryUser, String> {
    Mono<Boolean> existsByEmail(@Email @NotEmpty String email);
}
