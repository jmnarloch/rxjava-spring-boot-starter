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
package io.iceflower.spring.boot.rxjava.async;

import io.reactivex.rxjava3.core.Flowable;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * A specialized {@link SseEmitter} that handles {@link Flowable} types. The emitter subscribes to the
 * passed {@link Flowable} instance and emits every produced value through {@link #send(Object, MediaType)}.
 *
 * @author 김영근
 * @see SseEmitter
 */
public class FlowableSseEmitter<T> extends SseEmitter {

    private final ResponseBodyEmitterObserver<T> observer;

    public FlowableSseEmitter(Flowable<T> flowable) {
        this(null, flowable);
    }

    public FlowableSseEmitter(MediaType mediaType, Flowable<T> flowable) {
        this(null, mediaType, flowable);
    }

    public FlowableSseEmitter(Long timeout, MediaType mediaType, Flowable<T> flowable) {
        super(timeout);
        this.observer = new ResponseBodyEmitterObserver<T>(mediaType, flowable, this);
    }
}
