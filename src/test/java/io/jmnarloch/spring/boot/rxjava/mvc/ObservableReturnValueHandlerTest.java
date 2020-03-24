/**
 * Copyright (c) 2015-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.boot.rxjava.mvc;

//import io.reactivex.Observable;
//import io.reactivex.functions.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.IntegrationTest;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link ObservableReturnValueHandler} class.
 *
 * @author Jakub Narloch
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = ObservableReturnValueHandlerTest.Application.class)
//@WebAppConfiguration
//@IntegrationTest({"server.port=0"})
//@DirtiesContext
public class ObservableReturnValueHandlerTest {
//
//    @Value("${local.server.port}")
//    private int port = 0;
//
//    private TestRestTemplate restTemplate = new TestRestTemplate();
//
//    @Configuration
//    @EnableAutoConfiguration
//    @RestController
//    protected static class Application {
//
//        @Autowired
//        private RequestMappingHandlerAdapter adapter;
//
//        @PostConstruct
//        public void prioritizeCustomReturnValueHandlers () {
//            final List<HandlerMethodReturnValueHandler> returnValueHandlers =
//                    new ArrayList<>(adapter.getReturnValueHandlers());
//            final List<HandlerMethodReturnValueHandler> customReturnValueHandlers =
//                    adapter.getCustomReturnValueHandlers();
//            returnValueHandlers.removeAll(customReturnValueHandlers);
//            returnValueHandlers.addAll(0, customReturnValueHandlers);
//            adapter.setReturnValueHandlers(returnValueHandlers);
//        }
//
//        @RequestMapping(method = RequestMethod.GET, value = "/empty")
//        public Observable<Void> empty() {
//            return Observable.empty();
//        }
//
//        @RequestMapping(method = RequestMethod.GET, value = "/single")
//        public Observable<String> single() {
//            return Observable.just("single value");
//        }
//
//        @RequestMapping(method = RequestMethod.GET, value = "/multiple")
//        public Observable<String> multiple() {
//            return Observable.just("multiple", "values");
//        }
//
//        @RequestMapping(method = RequestMethod.GET, value = "/throw")
//        public Observable<Object> error() {
//            return Observable.error(new RuntimeException("Unexpected"));
//        }
//
//        @RequestMapping(method = RequestMethod.GET, value = "/timeout")
//        public Observable<String> timeout() {
//            return Observable.timer(1, TimeUnit.MINUTES).map(new Function<Long, String>() {
//                @Override
//                public String apply(Long aLong) {
//                    return "single value";
//                }
//            });
//        }
//    }
//
//    @Test
//    public void shouldRetrieveEmptyResponse() {
//
//        // when
//        ResponseEntity<List> response = restTemplate.getForEntity(path("/empty"), List.class);
//
//        // then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(Collections.emptyList(), response.getBody());
//    }
//
//    @Test
//    public void shouldRetrieveSingleValue() {
//
//        // when
//        ResponseEntity<List> response = restTemplate.getForEntity(path("/single"), List.class);
//
//        // then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(Collections.singletonList("single value"), response.getBody());
//    }
//
//    @Test
//    public void shouldRetrieveMultipleValues() {
//
//        // when
//        ResponseEntity<List> response = restTemplate.getForEntity(path("/multiple"), List.class);
//
//        // then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(Arrays.asList("multiple", "values"), response.getBody());
//    }
//
//    @Test
//    public void shouldRetrieveErrorResponse() {
//
//        // when
//        ResponseEntity<Object> response = restTemplate.getForEntity(path("/throw"), Object.class);
//
//        // then
//        assertNotNull(response);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    public void shouldTimeoutOnConnection() {
//
//        // when
//        ResponseEntity<Object> response = restTemplate.getForEntity(path("/timeout"), Object.class);
//
//        // then
//        assertNotNull(response);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    private String path(String context) {
//        return String.format("http://localhost:%d%s", port, context);
//    }
}
