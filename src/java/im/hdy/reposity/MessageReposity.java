package im.hdy.reposity;

import im.hdy.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hdy on 2017/9/8.
 */
@Repository
public interface MessageReposity extends MongoRepository<Message, Integer> {
}
