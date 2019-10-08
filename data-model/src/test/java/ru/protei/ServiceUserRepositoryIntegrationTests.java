package ru.protei;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.protei.autoconfigure.ModelAutoConfiguration;
import ru.protei.model.ServiceUser;
import ru.protei.model.ServiceUser.OnlineStatus;
import ru.protei.model.UserDetails;
import ru.protei.repository.jpa.ServiceUserJpaRepository;
import ru.protei.repository.jpa.UserDetailsJpaRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {ModelAutoConfiguration.class})
public class ServiceUserRepositoryIntegrationTests {

  private static final ServiceUser localUser = new ServiceUser()
      .setId(UUID.randomUUID())
      .setOnlineStatus(OnlineStatus.ONLINE)
      .setUsername("pihanya")
      .setEmail("gostevvm@yandex.ru");

  private static final UserDetails localUsersDetails = new UserDetails()
      .setPhoneNumber("+79883143061")
      .setScreenName("Гостев Михаил Владимирович")
      .setBio("Студент ИТМО");

  @Autowired private ServiceUserJpaRepository userRepository;
  @Autowired private UserDetailsJpaRepository detailsRepository;

  @BeforeEach
  public void init() {
    userRepository.deleteAll();
  }

  @Test
  public void addUserWithoutDetails() {
    var id = userRepository.save(localUser).getId();
    localUser.setId(id);

    localUsersDetails.setUserId(id);

    var databaseUser = userRepository.getOne(id);
    assertEquals(localUser, databaseUser);
  }

  @Test
  public void addUserWithDetails() {
    var databaseUser = userRepository.save(localUser);

    localUsersDetails.setUser(databaseUser);
    detailsRepository.save(localUsersDetails);

    databaseUser = userRepository.getOne(localUser.getId());
    assertEquals(localUser, databaseUser);
  }

  @Test(expected = Throwable.class)
  public void addUserWithNotExistingDetails() {
    localUser.setDetails(localUsersDetails);
    userRepository.save(localUser);
  }
}
