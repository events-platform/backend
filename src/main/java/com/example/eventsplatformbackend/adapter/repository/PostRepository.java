package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    boolean existsPostByBeginDateAndName(LocalDateTime beginDate, String name);
    // Wrap main query as 't' in select for correct sorting work
    @Query(value = "SELECT * "+
            "FROM (SELECT POSTS.* "+
            "   FROM POSTS INNER JOIN USERS ON POSTS.user_id = USERS.user_id "+
            "   WHERE "+
            "       (cast(:fromDate as timestamp) is null or POSTS.begin_date > cast(:fromDate as timestamp)) "+
            "       AND (cast(:toDate as timestamp) is null or POSTS.end_date < cast(:toDate as timestamp)) "+
            "       AND (COALESCE(:organizers) IS NULL or USERS.username IN (:organizers)) "+
            "       AND (COALESCE(:types) IS NULL or POSTS.type IN (:types))) as t",
            countQuery = "SELECT COUNT(*) "+
                    "FROM (SELECT POSTS.* "+
                    "   FROM POSTS INNER JOIN USERS ON POSTS.user_id = USERS.user_id "+
                    "   WHERE "+
                    "       (cast(:fromDate as timestamp) is null or POSTS.begin_date > cast(:fromDate as timestamp)) "+
                    "       AND (cast(:toDate as timestamp) is null or POSTS.end_date < cast(:toDate as timestamp)) "+
                    "       AND (COALESCE(:organizers) IS NULL or USERS.username IN (:organizers)) "+
                    "       AND (COALESCE(:types) IS NULL or POSTS.type IN (:types))) as t",
            nativeQuery = true)
    Page<Post> findPostsByFiltersWithPagination(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("organizers") List<String> organizers,
            @Param("types") List<String> types,
            Pageable pageable);
    // Delete post from join tables and from entity table
    @Modifying
    @Query(value =  "DELETE FROM users_created_posts WHERE created_posts_post_id = :postId ;"+
                    "DELETE FROM users_favorite_posts WHERE post_id = :postId ;"+
                    "DELETE FROM users_subscriptions WHERE post_id = :postId ;"+
                    "DELETE FROM posts WHERE posts.post_id = :postId ;",
                nativeQuery = true)
    void deletePostFromAllTables(@Param("postId") Long postId);
}
