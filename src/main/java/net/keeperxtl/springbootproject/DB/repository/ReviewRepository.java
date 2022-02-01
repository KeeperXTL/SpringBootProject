package net.keeperxtl.springbootproject.DB.repository;

import net.keeperxtl.springbootproject.DB.models.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
}
