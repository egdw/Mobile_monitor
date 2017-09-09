package im.hdy.reposity;

import im.hdy.entity.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hdy on 2017/9/8.
 */
@Repository
public interface ContactMessageReposity extends MongoRepository<ContactMessage, String> {
}
