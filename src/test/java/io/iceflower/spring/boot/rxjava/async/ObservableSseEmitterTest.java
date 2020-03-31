package io.iceflower.spring.boot.rxjava.async;

import io.iceflower.spring.boot.rxjava.dto.EventDto;
import io.reactivex.rxjava3.core.Observable;
import java.util.Date;
import java.util.GregorianCalendar;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * A unit test code of ObservableDeferredResult
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = ObservableSseEmitterTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("ObservableDeferredResult 클래스")
public class ObservableSseEmitterTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/sse")
    public ObservableSseEmitter<String> single() {
      return new ObservableSseEmitter<String>(Observable.just("single value"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    public ObservableSseEmitter<String> messages() {
      return new ObservableSseEmitter<String>(Observable.just("message 1", "message 2", "message 3"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events")
    public ObservableSseEmitter<EventDto> event() {
      return new ObservableSseEmitter<EventDto>(APPLICATION_JSON, Observable.just(
          new EventDto("Spring.io", getDate(2016, 5, 11)),
          new EventDto("JavaOne", getDate(2016, 9, 22))
      ));
    }
  }
  private static Date getDate(int year, int month, int day) {
    return new GregorianCalendar(year, month, day).getTime();
  }

  @Nested
  @DisplayName("ObservableDeferredResult 는")
  class Describe_of_ObservableSseEmitter {
    @Nested
    @DisplayName("SSE 데이터를 전송받아야 할 때")
    class Context_with_retrieve_sse_data {
      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/sse", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("data:single value\n\n", response.getBody());
      }
    }
    @Nested
    @DisplayName("SSE 메시지를 한번에 여러 개 받아야 할 때")
    class Context_with_retrieve_multiple_messages {
      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/messages", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("data:message 1\n\ndata:message 2\n\ndata:message 3\n\n", response.getBody());
      }
    }
    @Nested
    @DisplayName("JSON으로 직렬화된 SSE 메시지를 여러개 받아야 할 때")
    class Context_with_retrieve_json_over_sse_multiple_messages {
      @Test
      @DisplayName("데이터를 성공적으로 전달받는다")
      void it_returns_successfully() {

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/events", String.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
      }
    }
  }
}
