package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Song;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SongRepository extends JpaRepository<Song,Long> {

    Iterable<Song> findByTitle(String title);

    List<Song> findByIdIn(List<Long> ids, Sort sort);

    @Query("select case when count(s)> 0 then true else false end from Song s where lower(s.title) like lower(concat('%', :title,'%'))")
    boolean existsByTitleText(@Param("title") String title);

    @Query("select s from Song s where lower(s.title) like lower(concat('%', :title,'%'))")
    List<Song> findByTitleText(@Param("title") String title);
}
