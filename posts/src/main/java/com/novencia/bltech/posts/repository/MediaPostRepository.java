package com.novencia.bltech.posts.repository;

import com.novencia.bltech.posts.entity.MediaPost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaPostRepository extends ReactiveCrudRepository<MediaPost, String> {
}
