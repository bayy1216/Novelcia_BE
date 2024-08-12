package com.reditus.novelcia.infrastructure

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


@NoRepositoryBean
interface BatchRepository<T, ID>: JpaRepository<T, ID> {
    fun saveInBatch(entities: List<T>)
}

@Transactional(propagation = Propagation.NEVER)
class BatchRepositoryImpl<T, ID>(
    jpaMetamodelEntityInformation: JpaMetamodelEntityInformation<T, ID>,
    entityManager: EntityManager,
) : SimpleJpaRepository<T, ID>(
    jpaMetamodelEntityInformation,
    entityManager
), BatchRepository<T, ID> {
    override fun saveInBatch(entities: List<T>) {
        val batchExecutor = SpringContext.getBean(BatchExecutor::class.java)
        batchExecutor.saveInBatch(entities)
    }
}

@Component
class BatchExecutor(
    private val entityManagerFactory: EntityManagerFactory,
    @Value("\${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private val batchSize: Int,
) {


    fun <T> saveInBatch(entities: List<T>) {
        val entityManager = entityManagerFactory.createEntityManager()
        val entityTransaction = entityManager.transaction

        try {
            entityTransaction.begin()

            for ((i, entity) in entities.withIndex()) {
                if (i % batchSize == 0 && i > 0) {
                    logger.info("Flushing the entity manager ...")
                    entityTransaction.commit()
                    entityTransaction.begin()

                    entityManager.clear()
                }

                entityManager.persist(entity)
            }

            logger.info("Flushing the remaining entities ...")

            entityTransaction.commit()
        } catch (e: RuntimeException) {
            if (entityTransaction.isActive) {
                entityTransaction.rollback()
            }
            throw e
        } finally {
            entityManager.close()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BatchExecutor::class.java)
    }
}

@Component
class SpringContext : ApplicationContextAware {

    companion object {
        lateinit var context: ApplicationContext

        fun <T : Any?> getBean(beanClass: Class<T>): T {
            return context.getBean(beanClass)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}