package org.skillbox.socnet.model.repository;

import org.skillbox.socnet.model.entity.BlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockHistoryRepository extends JpaRepository<BlockHistory, Integer> {
}
