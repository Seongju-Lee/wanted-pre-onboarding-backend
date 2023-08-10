package preonboarding.board.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 유저 조회")
    public void existsByEmail() {
        // Given
        User user = userRepository.save(new User("test@test.com", "validPassword"));
        // When
        boolean existsByEmail = userRepository.existsByEmail(user.getEmail());
        // Then
        assertThat(existsByEmail).isTrue();
    }

    @Test
    @DisplayName("이메일로 유저 탐색")
    public void testFindByEmail() {
        // Given
        User user = userRepository.save(new User("test@test.com", "validPassword"));
        // When
        User foundUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("유저 저장")
    public void testSaveUser() {
        // Given
        User user = new User("test@example.com", "validPassword");
        // When
        User savedUser = userRepository.save(user);
        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
    }
}
