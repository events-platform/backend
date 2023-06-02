package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.QPost;
import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>,
        QuerydslPredicateExecutor<Post> {

    boolean existsPostByBeginDateAndName(LocalDateTime beginDate, String name);
    // Delete post from join tables and from entity table
    @Modifying
    @Query(value =  "DELETE FROM users_created_posts WHERE created_posts_post_id = :postId ;"+
                    "DELETE FROM users_favorite_posts WHERE post_id = :postId ;"+
                    "DELETE FROM users_subscriptions WHERE post_id = :postId ;"+
                    "DELETE FROM posts WHERE posts.post_id = :postId ;",
                nativeQuery = true)
    void deletePostFromAllTables(@Param("postId") Long postId);
    /**
     *
     * @param fromDate Минимальная дата начала ивента
     * @param toDate Максимальная дата окончания ивента
     * @param organizers Список организаторов ивента (может состоять из одного организатора)
     * @param types Типы ивентов (митап, день открытых дверей и т.д.)
     * @param endedDateFilter Фильтр, отсеивающий все прощедшие мероприятия (на самом деле просто текущая дата)
     * @param searchQuery Поисковый запрос пользователя
     * @param pageable Параметр для пагинации
     * @return Результат запроса с пагинацией
     */
    default Page<Post> findPostsByFilters(LocalDateTime fromDate, LocalDateTime toDate,
                                  List<String> organizers,
                                  List<EType> types,
                                  List<EFormat> formats,
                                  LocalDateTime endedDateFilter,
                                  String searchQuery,
                                  Pageable pageable) {

        QPost qPost = QPost.post;
        BooleanBuilder where = new BooleanBuilder();
        if (fromDate != null) {
            where.and(qPost.beginDate.after(fromDate));
        }
        if (toDate != null) {
            where.and(qPost.endDate.before(toDate));
        }
        if (organizers != null) {
            where.and(qPost.owner.username.in(organizers));
        }
        if (endedDateFilter != null) {
            where.and(qPost.endDate.after(endedDateFilter));
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                where.and(
                        qPost.name.containsIgnoreCase(searchQuery)
                        .or(qPost.name.startsWithIgnoreCase(searchQuery))
                        .or(qPost.description.containsIgnoreCase(searchQuery))
                        .or(qPost.owner.username.containsIgnoreCase(searchQuery))
                );
        }
        if (types != null && !types.isEmpty()) {
            where.and(qPost.type.in(types));
        }
        if(formats != null && !formats.isEmpty()){
            where.and(qPost.format.in(formats));
        }

        return findAll(where, pageable);
    }
}
