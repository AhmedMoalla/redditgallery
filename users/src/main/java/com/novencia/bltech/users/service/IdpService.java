package com.novencia.bltech.users.service;

import com.novencia.bltech.users.entity.RedditGalleryUser;
import reactor.core.publisher.Mono;

/**
 * Abstraction to propagate user data to an Identity Provider
 */
public interface IdpService {

    Mono<RedditGalleryUser> registerUser(RedditGalleryUser user, String rawPassword);
}
