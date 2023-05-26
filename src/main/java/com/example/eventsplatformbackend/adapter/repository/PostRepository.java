package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    boolean existsPostByBeginDateAndName(LocalDateTime beginDate, String name);
    @Query(value = "SELECT * FROM POSTS "+
            "WHERE (cast(:fromDate as timestamp) is null or POSTS.begin_date > cast(:fromDate as timestamp)) "+
            "AND (cast(:toDate as timestamp) is null or POSTS.end_date < cast(:toDate as timestamp))",
            countQuery = "SELECT COUNT(*) FROM POSTS "+
                    "WHERE (cast(:fromDate as timestamp) is null or POSTS.begin_date > cast(:fromDate as timestamp)) "+
                    "AND (cast(:toDate as timestamp) is null or POSTS.end_date < cast(:toDate as timestamp))",
            nativeQuery = true)
    Page<Post> findPostsByFiltersWithPagination(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
