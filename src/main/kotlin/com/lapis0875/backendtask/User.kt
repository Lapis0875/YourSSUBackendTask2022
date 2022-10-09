package com.lapis0875.backendtask

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.DynamicInsert
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@DynamicInsert
@Table(name = "user")
class User(
    @Column(name = "email") var email: String,

    @Column(name = "password") var password: String,

    @Column(name = "username") var username: String,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", unique = true, updatable = false)
    val userId: Long = 0,        // DB 내에 존재하지 않는 PK값. val로 선언하더라도 리플렉션을 통해 자신의 id값을 할당받는다.

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    @ColumnDefault(value="NOW()")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT NOW() ON UPDATE NOW()")
    @ColumnDefault(value="NOW()")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val articles: List<Article> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val comments: List<Comment> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        return userId == (other as User).userId
    }

    override fun hashCode() = Objects.hashCode(userId)

    fun toDTO(): UserDTO {
        return UserDTO(
            email, password, username, createdAt, updatedAt, userId
        )
    }
}

data class UserRequest(val email: String, val password: String, val username: String? = null)
data class UserResponse(val email: String, val username: String)
data class UserDTO(
    val email: String,
    val password: String,
    val username: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val userId: Long
) {
    fun toResponse(): UserResponse {
        return UserResponse(this.email, this.username)
    }
}

@Repository
interface UserRepository: JpaRepository<User, Long>

@Service
@Transactional
class UserService(private val repository: UserRepository) {
    fun getAll(): List<UserDTO> = repository.findAll().map { it.toDTO() }

    fun getById(id: Long): User = repository.findByIdOrNull(id) ?:
        throw EntityNotFoundException()

    fun getByIdWrapped(id: Long): UserDTO = getById(id).toDTO()

    fun getByPayload(email: String, password: String): User = repository.findOne(
        Example.of(
            User(email, password, "NONE"),
            ExampleMatcher.matchingAll()
                .withIgnorePaths("createdAt", "updatedAt", "userId", "username")
                .withMatcher("email", ExampleMatcher.GenericPropertyMatcher().exact())
                .withMatcher("password", ExampleMatcher.GenericPropertyMatcher().exact())
        )).orElseThrow { throw EntityNotFoundException() }

    fun create(user: User): UserDTO {
        val exist: Boolean = repository.findOne(Example.of(
            user,
            ExampleMatcher.matchingAll().withIgnorePaths("updatedAt"
        ))).isPresent
        if (exist) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        return repository.save(user).toDTO()
    }

    fun remove(id: Long) {
        if (repository.existsById(id)) repository.deleteById(id)
        else throw EntityNotFoundException()
    }

    fun update(id: Long, user: User): UserDTO {
        return if (repository.existsById(id)) {
            repository.save(user).toDTO()
        } else throw EntityNotFoundException()
    }
}