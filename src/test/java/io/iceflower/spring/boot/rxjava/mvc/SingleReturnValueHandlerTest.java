package io.iceflower.spring.boot.rxjava.mvc;

import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A unit test code of SingleReturnValueHandler
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = SingleReturnValueHandlerTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("SingleReturnValueHandler 클래스")
public class SingleReturnValueHandlerTest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/single")
    public Single<String> single() {
      return Single.just("single value");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/singleWithResponse")
    public Single<ResponseEntity<String>> singleWithResponse() {
      return Single.just(new ResponseEntity<String>("single value", HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/throw")
    public Single<Object> error() {
      return Single.error(new RuntimeException("Unexpected"));
    }
  }

  @Nested
  @DisplayName("SingleReturnValueHandler 는")
  class Describe_of_ingleReturnValueHandler {
    @Nested
    @DisplayName("단일 값을 전달해야 할 때")
    class Context_with_retrieve_single_value {
      @Test
      @DisplayName("정상적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/single", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("single value", response.getBody());
      }
    }
    @Nested
    @DisplayName("단일 값과 http 코드를 함께 전달해야 할 때")
    class Context_with_retrieve_single_value_with_status_code {
      @Test
      @DisplayName("정상적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/singleWithResponse", String.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("single value", response.getBody());
      }
    }
    @Nested
    @DisplayName("오류가 발생했을 때")
    class Context_with_retrieve_error_response {
      @Test
      @DisplayName("http 500 코드를 돌려준다")
      void it_returns_http_500_code() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/throw", Object.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      }
    }
  }







}
