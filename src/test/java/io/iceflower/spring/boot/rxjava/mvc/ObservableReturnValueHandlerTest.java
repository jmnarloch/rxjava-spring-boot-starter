package io.iceflower.spring.boot.rxjava.mvc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * A unit test code of ObservableReturnValueHandler
 *
 * @author Jakub Narloch
 * @author 김영근
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = ObservableReturnValueHandlerTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("ObservableReturnValueHandler 클래스")
public class ObservableReturnValueHandlerTest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Configuration
  @EnableAutoConfiguration
  @RestController
  protected static class Application {

    @Bean
    public ObjectMapper objectMapper() {

      return Jackson2ObjectMapperBuilder
          .json()
          .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .build();
    }


    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void prioritizeCustomReturnValueHandlers () {
      final List<HandlerMethodReturnValueHandler> returnValueHandlers =
          new ArrayList<>(adapter.getReturnValueHandlers());
      final List<HandlerMethodReturnValueHandler> customReturnValueHandlers =
          adapter.getCustomReturnValueHandlers();
      returnValueHandlers.removeAll(customReturnValueHandlers);
      returnValueHandlers.addAll(0, customReturnValueHandlers);
      adapter.setReturnValueHandlers(returnValueHandlers);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/empty")
    public Observable<Void> empty() {
      return Observable.empty();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/single")
    public Observable<String> single() {
      return Observable.just("single value");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/multiple")
    public Observable<String> multiple() {
      return Observable.just("multiple", "values");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/throw")
    public Observable<Object> error() {
      return Observable.error(new RuntimeException("Unexpected"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/timeout")
    public Observable<String> timeout() {
      return Observable.timer(1, TimeUnit.MINUTES).map(new Function<Long, String>() {
        @Override
        public String apply(Long aLong) {
          return "single value";
        }
      });
    }
  }
  @Nested
  @DisplayName("ObservableReturnValueHandler 는")
  class Describe_of_ObservableReturnValueHandler {
    @Nested
    @DisplayName("empty 응답을 받았을 때")
    class Context_with_retrieve_empty_response {
      @Test
      @DisplayName("정상 작동한다")
      void it_run_successfully() {

        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/empty", List.class);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Collections.emptyList(), response.getBody());
      }
    }
    @Nested
    @DisplayName("단일 값으로 응답을 전달해줄 때")
    class Context_with_retrieve_single_value {
      @Test
      @DisplayName("정상 작동한다")
      void it_run_successfully() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/single", List.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Collections.singletonList("single value"), response.getBody());
      }
    }
    @Nested
    @DisplayName("한번에 많은 값으로 응답을 전달해줄 때")
    class Context_with_retrieve_multiple_values {
      @Test
      @DisplayName("정상 작동한다")
      void it_run_successfully() {
        // when
        ResponseEntity<List> response = restTemplate.getForEntity("/multiple", List.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Arrays.asList("multiple", "values"), response.getBody());
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
    @Nested
    @DisplayName("connection 의 타임아웃 오류가 발생했을 때")
    class Context_with_timeout_error_on_connection {
      @Test
      @DisplayName("http 500 코드를 돌려준다")
      void it_returns_http_500_code() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity("/timeout", Object.class);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      }
    }
  }
}
