package im.hdy.reposity;

import im.hdy.entity.Mobile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hdy on 2017/9/8.
 */
@Repository
public interface MobileReposity extends MongoRepository<Mobile, Integer> {
}
