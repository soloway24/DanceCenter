package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends CrudRepository<Post,Long> {


}
