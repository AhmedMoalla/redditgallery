package com.novencia.bltech.posts.controller;

import com.novencia.bltech.posts.entity.MediaPost;
import com.novencia.bltech.posts.repository.MediaPostRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/posts")
public class PostRestController {

    private final MediaPostRepository repository;

    public PostRestController(MediaPostRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<MediaPost> getAll() {
        return repository.findAll();
    }
}
