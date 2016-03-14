/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Isolation;

import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;

/**
 * Describes transaction attributes on a method or class.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    /**
     * The transaction propagation type. Defaults to
     * {@link Propagation#REQUIRED}.
     * 
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * The transaction isolation level. Defaults to {@link Isolation#DEFAULT}.
     * 
     * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
     */
    IsolationLevel isolation() default IsolationLevel.DEFAULT;

    /**
     * The timeout for this transaction. If set to 0 defaults to the default
     * timeout of the underlying transaction system.
     */
    int timeout() default 0;

    /**
     * {@code true} if the transaction is updating. Defaults to {@code false}.
     * <p>
     * If set to false, it no commit of the transaction will be possible
     */
    boolean updating() default false;

    /**
     * Defines zero (0) or more exception {@link Class classes}, which must be a
     * subclass of {@link Throwable}, indicating which exception types must
     * cause a transaction rollback.
     * <p>
     * This is the preferred way to construct a rollback rule, matching the
     * exception class and subclasses.
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * Defines zero (0) or more exception {@link Class Classes}, which must be a
     * subclass of {@link Throwable}, indicating which exception types must
     * <b>not</b> cause a transaction rollback. This takes precedence over
     * {@link #rollbackFor()}
     * <p>
     * This is the preferred way to construct a rollback rule, matching the
     * exception class and subclasses.
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    /**
     * If set to true, a new entity manager set will be used in any case (even
     * with {@link Propagation#NEVER}.
     * <p>
     * By default, a new entity manager set will only be used if a transaction
     * is active and no entity manager set is present.
     */
    boolean forceNewEntityManagerSet() default false;
}
