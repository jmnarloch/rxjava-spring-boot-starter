package io.iceflower.spring.boot.rxjava.async;

import io.iceflower.spring.boot.rxjava.dto.EventDto;
import io.reactivex.rxjava3.core.Single;
import java.util.Date;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * A unit test code of SingleDeferredResult
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = SingleDeferredResultTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("SingleDeferredResult 클래스")
public class SingleDeferredResultTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/single")
    public SingleDeferredResult<String> single() {
      return new SingleDeferredResult<String>(Single.just("single value"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/singleWithResponse")
    public SingleDeferredResult<ResponseEntity<String>> singleWithResponse() {
      return new SingleDeferredResult<ResponseEntity<String>>(
          Single.just(new ResponseEntity<String>("single value", HttpStatus.NOT_FOUND)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event", produces = APPLICATION_JSON_VALUE)
    public SingleDeferredResult<EventDto> event() {
      return new SingleDeferredResult<EventDto>(Single.just(new EventDto("Spring.io", new Date())));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/throw")
    public SingleDeferredResult<Object> error() {
      return new SingleDeferredResult<Object>(Single.error(new RuntimeException("Unexpected")));
    }
  }

  @Nested
  @DisplayName("SingleDeferredResult 는")
  class Describe_of_SingleDeferredResult {
    @Nested
    @DisplayName("단일 값을 전달해야 할때")
    class Context_with_retrive_single_value {
      @Test
      @DisplayName("성공적으로 값을 돌려준다")
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
    @DisplayName("단일 값과 HTTP 상태코드를 함께 전달해야 할 때")
    class Context_with_retrieve_single_value_with_status_code {
      @Test
      @DisplayName("성공적으로 값을 돌려준다")
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
    @DisplayName("json으로 직렬화한 값을 전달해 줄 때")
    class Context_with_json_serialized_pojo_value {
      @Test
      @DisplayName("성공적으로 값을 돌려준다")
      void it_returns_successfully() {
        // when
        ResponseEntity<EventDto> response = restTemplate.getForEntity("/event", EventDto.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Spring.io", response.getBody().getName());
      }
    }
    @Nested
    @DisplayName("오류가 발생했을 때")
    class Context_with_retrieve_error_response {
      @Test
      @DisplayName("500 에러를 받는다")
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
