package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByIdIn(List<Long> postIds, Sort sort);
}
