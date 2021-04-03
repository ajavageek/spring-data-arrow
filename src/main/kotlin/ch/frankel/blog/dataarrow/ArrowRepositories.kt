package ch.frankel.blog.dataarrow

import arrow.core.Either
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface ArrowRepository<T, ID> : Repository<T, ID> {
    fun findById(id: Long): Either<Unit, T>
    fun findAll(): Iterable<T>
}

class SimpleArrowRepository<T, ID>(
    private val ops: JdbcAggregateOperations,
    private val entity: PersistentEntity<T, *>
) : ArrowRepository<T, ID> {

    override fun findById(id: Long) = Either.fromNullable(
        ops.findById(id, entity.type)
    )

    override fun findAll(): Iterable<T> = ops.findAll(entity.type)
}
