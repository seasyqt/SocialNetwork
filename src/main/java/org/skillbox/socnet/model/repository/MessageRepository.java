package org.skillbox.socnet.model.repository;

import org.skillbox.socnet.model.entity.Dialog;
import org.skillbox.socnet.model.entity.Message;
import org.skillbox.socnet.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    Page<Message> findAllByDialogAndMessageTextContaining(Dialog dialog, String query, Pageable pageable);

    @Query(value = "SELECT COUNT(DISTINCT m.id) " +
            "FROM Message m INNER JOIN Dialog d " +
            "ON m.dialog = d " +
            "WHERE (d.owner = :user or d.recipient = :user) " +
            "and m.readStatus = 'SENT' and m.author <> :user")
    Long getCountOfUnreadMessage(@Param("user") User user);

}