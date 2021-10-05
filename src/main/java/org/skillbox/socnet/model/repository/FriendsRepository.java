package org.skillbox.socnet.model.repository;

import org.skillbox.socnet.model.entity.Friendship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRepository extends CrudRepository<Friendship, Long> {
}