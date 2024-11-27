package com.anastasiat.repository;

import com.anastasiat.entity.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class CustomTaskRepositoryImpl implements CustomTaskRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public CustomTaskRepositoryImpl(
            EntityManager entityManager
    ) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<Task> findByStatuses(List<String> statuses, int limit) {
        String nativeSql = "select * from Tasks t where t.status in (:statuses) for update skip locked";

        Query nativeQuery = entityManager.createNativeQuery(nativeSql, Task.class);
        nativeQuery.setParameter("statuses", statuses);

        return nativeQuery.setMaxResults(limit).getResultList();
    }

    @Override
    @Transactional
    public List<Task> findStuckTasks(List<String> statuses, int limit, ZoneId zoneId, int shift) {
        String nativeSql = "select * from Tasks t where t.status in (:statuses) " +
                "and (t.expected_execution_date < :currentDate) for update skip locked";

        Query nativeQuery = entityManager.createNativeQuery(nativeSql, Task.class);
        nativeQuery.setParameter("statuses", statuses);
        nativeQuery.setParameter("currentDate", LocalDateTime.now(zoneId).minusMinutes(shift));

        return nativeQuery.setMaxResults(limit).getResultList();
    }
}
