package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Song;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SongRepository extends CrudRepository<Song,Long> {

    Iterable<Song> findByTitle(String title);


    @Query("select case when count(t)> 0 then true else false end from Song t where lower(t.title) like lower(concat('%', :title,'%'))")
    boolean existsByTitle(@Param("title") String title);

}
