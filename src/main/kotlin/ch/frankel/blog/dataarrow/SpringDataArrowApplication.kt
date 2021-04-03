package ch.frankel.blog.dataarrow

import java.time.LocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.*

class Person(@Id val id: Long, var name: String, var birthdate: LocalDate?)

interface PersonRepository : CrudRepository<Person, Long>

@RestController
class PersonController(private val repository: PersonRepository) {

    @GetMapping
    fun getAll(): Iterable<Person> = repository.findAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long) = repository.findById(id)
}

@SpringBootApplication
class SpringDataArrowApplication

fun main(args: Array<String>) {
    runApplication<SpringDataArrowApplication>(*args)
}
