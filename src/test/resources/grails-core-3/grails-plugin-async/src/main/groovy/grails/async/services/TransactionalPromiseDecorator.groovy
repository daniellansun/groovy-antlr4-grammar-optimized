/*
 * Copyright 2013 SpringSource
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
package grails.async.services

import groovy.transform.CompileStatic
import grails.async.decorator.PromiseDecorator
import org.springframework.beans.annotation.AnnotationBeanUtils
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

/**
 * A {@link PromiseDecorator} that wraps a {@link grails.async.Promise} in a transaction
 *
 * @author Graeme Rocher
 * @since 2.3
 */
@CompileStatic
class TransactionalPromiseDecorator implements PromiseDecorator, TransactionDefinition {

    PlatformTransactionManager transactionManager
    @Delegate DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition()

    TransactionalPromiseDecorator(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    TransactionalPromiseDecorator(PlatformTransactionManager transactionManager, DefaultTransactionDefinition transactionDefinition) {
        this.transactionManager = transactionManager
        this.transactionDefinition = transactionDefinition
    }

    TransactionalPromiseDecorator(PlatformTransactionManager transactionManager, Transactional transactionDefinition) {
        this.transactionManager = transactionManager
        final definition = new DefaultTransactionDefinition()
        AnnotationBeanUtils.copyPropertiesToBean(transactionDefinition, definition)
        this.transactionDefinition = definition
    }

    @Override
    def <D> Closure<D> decorate(Closure<D> original) {
        if (transactionManager != null) {
            return (Closure<D>){ args ->
                def transactionTemplate = transactionDefinition != null ? new TransactionTemplate(transactionManager, transactionDefinition) : new TransactionTemplate(transactionManager)
                transactionTemplate.execute({
                    original.call(args)
                } as TransactionCallback)
            }
        }
        return original
    }
}
