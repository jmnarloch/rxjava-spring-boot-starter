package io.iceflower.spring.boot.rxjava.async;

import io.iceflower.spring.boot.rxjava.dto.EventDto;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * A unit test code of ObservableDeferredResult
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = ObservableDeferredResultTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("ObservableDeferredResult 클래스")
public class ObservableDeferredResultTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/empty")
    public ObservableDeferredResult<String> empty() {
      return new ObservableDeferredResult<String>(Observable.<String>empty());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/single")
    public ObservableDeferredResult<String> single() {
      return new ObservableDeferredResult<String>(Observable.just("single value"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/multiple")
    public ObservableDeferredResult<String> multiple() {
      return new ObservableDeferredResult<String>(Observable.just("multiple", "values"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event", produces = APPLICATION_JSON_VALUE)
    public ObservableDeferredResult<EventDto> event() {
      return new ObservableDeferredResult<EventDto>(
          Observable.just(
              new EventDto("Spring.io", new Date()),
              new EventDto("JavaOne", new Date())
          )
      );
    }

    @RequestMapping(method = RequestMethod.GET, value = "/throw")
    public ObservableDeferredResult<Object> error() {
      return new ObservableDeferredResult<Object>(Observable.error(new RuntimeException("Unexpected")));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/timeout")
    public ObservableDeferredResult<String> timeout() {
      return new ObservableDeferredResult<String>(Observable.timer(1, TimeUnit.MINUTES).map(new Function<Long, String>() {
        @Override
        public String apply(Long aLong) {
          return "single value";
        }
      }));
    }
  }
  @Nested
  @DisplayName("ObservableDeferredResult 는")
  class Describe_of_ObservableDeferredResult {
    @Nested
    @DisplayName("공백 응답을 전달해야 할 때")
    class Context_of_retrive_empty_response {
      @Test
      @DisplayName("정상적으로 공백 응답을 돌려준다")
      void it_returns_empty_response() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/empty", List.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Collections.emptyList(), response.getBody());
      }
    }
    @Nested
    @DisplayName("Single value를 반환해야 할 때")
    class Context_with_retrive_single_value {
      @Test
      @DisplayName("정상적으로 Single value를 돌려준다")
      void it_returns_single_value() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/single", List.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Collections.singletonList("single value"), response.getBody());
      }
    }
    @Nested
    @DisplayName("multiple value를 반환해야 할 때")
    class Context_with_retrive_multiple_values {
      @Test
      @DisplayName("정상적으로 multiple value를 돌려준다")
      void it_returns_multiple_values() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/multiple", List.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Arrays.asList("multiple", "values"), response.getBody());
      }
    }
    @Nested
    @DisplayName("Json으로 직렬화한 List 값을 반환해야 할 때")
    class Context_with_retrive_json_serialized_list_values {
      @Test
      @DisplayName("정상적으로 Json으로 직렬화한 List 값을 돌려준다")
      void it_returns_json_serialized_list_values() {
        // when
        ResponseEntity<List<EventDto>> response = restTemplate.exchange("/event", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<EventDto>>() {});

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("JavaOne", response.getBody().get(1).getName());
      }
    }
    @Nested
    @DisplayName("에러값을 반환해야 할 때")
    class Context_with_retrive_error_response {
      @Test
      @DisplayName("정상적으로 에러값을 반환한다")
      void it_returns_error_response() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/throw", Object.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      }
    }
    @Nested
    @DisplayName("타임아웃이 발생했을 때")
    class Context_with_timeout_connection {
      @Test
      @DisplayName("정상적으로 에러값을 돌려준다")
      void it_returns_http_500_error() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/timeout", Object.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      }
    }
  }
}