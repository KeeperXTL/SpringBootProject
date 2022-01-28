package net.keeperxtl.springbootproject.DB.repository;

import net.keeperxtl.springbootproject.DB.models.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {

}
