package com.snaplist_backend.repository;

import com.snaplist_backend.domain.Task.Status;
import com.snaplist_backend.domain.Task.Priority;
import com.snaplist_backend.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TaskRepository extends JpaRepository<Task, Long> {

   Page<Task> findByUserId(Long userId, Pageable pageable);
   Page<Task> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);
   Page<Task> findByUserIdAndPriority(Long userId, Priority ppriority, Pageable pageable);

    @Query("""
        SELECT t FROM Task t
        WHERE t.user.id = :userId
        AND t.dueDate = :today
        """)
    Page<Task> findTasksDueToday(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Task t
        WHERE t.user.id = :userId
        AND t.status = com.snaplist_backend.domain.Task$Status.PENDING
        AND t.dueDate < :today
        """)
    Page<Task> findOverdueTasks(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            Pageable pageable
    );

   @Query("""
        SELECT t FROM Task t
        WHERE t.user.id = :userId
        AND LOWER(t.text) LIKE LOWER(CONCAT('%', :q, '%')) 
        """)
   Page<Task> searchByText(
           @Param("userId") Long userId,
           @Param("q") String q,
           Pageable pageable
    );
}
