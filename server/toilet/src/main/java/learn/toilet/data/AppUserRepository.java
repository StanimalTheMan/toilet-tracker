package learn.toilet.data;

import learn.toilet.models.AppUser;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserRepository {
    @Transactional
    AppUser findByUsername(String username);

    @Transactional
    AppUser findById(int userId);

    @Transactional
    AppUser create(AppUser user);

    @Transactional
    void update(AppUser user);
}
