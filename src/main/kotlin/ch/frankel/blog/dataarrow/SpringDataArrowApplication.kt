package ch.frankel.blog.dataarrow

import java.time.LocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.web.servlet.function.*

class Person(@Id val id: Long, var name: String, var birthdate: LocalDate?)

interface PersonRepository : ArrowRepository<Person, Long>

class PersonHandler(private val repository: PersonRepository) {

    fun getAll(req: ServerRequest) = ServerResponse.ok().body(repository.findAll())
    fun getOne(req: ServerRequest): ServerResponse = repository
        .findById(req.pathVariable("id").toLong())
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
@EnableJdbcRepositories(repositoryBaseClass = SimpleArrowRepository::class)
class SpringDataArrowApplication

fun main(args: Array<String>) {
    runApplication<SpringDataArrowApplication>(*args) {
        addInitializers(beans())
    }
}
