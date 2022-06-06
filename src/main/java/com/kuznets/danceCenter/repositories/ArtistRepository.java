package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArtistRepository extends JpaRepository<Artist,Long> {

    @Query("select a from Artist a where lower(a.name) = lower(:name)")
    Iterable<Artist> findByName(String name);

    @Query("select case when count(a)> 0 then true else false end from Artist a where lower(a.name) = lower(:name)")
    boolean existsByName(String name);

    @Query("select case when count(a)> 0 then true else false end from Artist a where lower(a.name) like lower(concat('%', :name,'%'))")
    boolean existsByNameText(@Param("name") String name);

    @Query("select a from Artist a where lower(a.name) like lower(concat('%', :name,'%'))")
    List<Artist> findAllByNameText(@Param("name") String name);
}
