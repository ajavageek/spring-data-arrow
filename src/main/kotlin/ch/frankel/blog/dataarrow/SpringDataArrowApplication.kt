package ch.frankel.blog.dataarrow

import java.time.LocalDate
import java.util.*
import arrow.core.Either
import arrow.core.left
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.web.servlet.function.*

class Person(@Id val id: Long, var name: String, var birthdate: LocalDate?)

interface PersonRepository : CrudRepository<Person, Long>

private fun <T> Optional<T>.toEither() =
    if (isPresent) Either.right(get())
    else Unit.left()

class PersonHandler(private val repository: PersonRepository) {

    fun getAll(req: ServerRequest) = ServerResponse.ok().body(repository.findAll())
    fun getOne(req: ServerRequest): ServerResponse = repository
        .findById(req.pathVariable("id").toLong())
        .toEither()
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
