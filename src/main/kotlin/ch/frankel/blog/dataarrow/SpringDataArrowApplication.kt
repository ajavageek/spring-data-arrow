package ch.frankel.blog.dataarrow

import java.time.LocalDate
import arrow.core.Either
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.repository.CrudRepository
import org.springframework.web.servlet.function.*

class Person(@Id val id: Long, var name: String, var birthdate: LocalDate?)

interface PersonRepository : CrudRepository<Person, Long>, CustomPersonRepository

interface CustomPersonRepository {
    fun arrowFindById(id: Long): Either<Unit, Person>
}

@Suppress("UNUSED")
class CustomPersonRepositoryImpl(private val ops: JdbcAggregateOperations) : CustomPersonRepository {

    override fun arrowFindById(id: Long) = Either.fromNullable(ops.findById(id, Person::class.java))
}

class PersonHandler(private val repository: PersonRepository) {

    fun getAll(req: ServerRequest) = ServerResponse.ok().body(repository.findAll())
    fun getOne(req: ServerRequest): ServerResponse = repository
        .arrowFindById(req.pathVariable("id").toLong())
        .fold(
            { ServerResponse.notFound().build() },
            { ServerResponse.ok().body(it) }
        )
}

fun beans() = beans {
    bean<PersonHandler>()
    bean {
        val handler = ref<PersonHandler>()
        router {
            GET("/", handler::getAll)
            GET("/{id}", handler::getOne)
        }
    }
}

@SpringBootApplication
class SpringDataArrowApplication

fun main(args: Array<String>) {
    runApplication<SpringDataArrowApplication>(*args) {
        addInitializers(beans())
    }
}
