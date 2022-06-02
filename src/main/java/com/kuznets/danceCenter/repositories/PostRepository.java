package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post,Long> {


}
