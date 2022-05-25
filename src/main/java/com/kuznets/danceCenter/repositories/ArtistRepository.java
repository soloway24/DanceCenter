package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Artist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ArtistRepository extends CrudRepository<Artist,Long> {

    Iterable<Artist> findByName(String s);
    boolean existsByName(String name);

    @Query("select case when count(t)> 0 then true else false end from Artist t where lower(t.name) like lower(concat('%', :name,'%'))")
    boolean existsByNameText(@Param("name") String name);

}
