package com.lapis0875.backendtask

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.DynamicInsert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@DynamicInsert
@Table(name = "article")
class Article (
    @Column(name = "content", nullable = false, columnDefinition = "VARCHAR(255)")
    @field:NotBlank
    var content: String,

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    @field:NotBlank
    var title: String,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "article_id", columnDefinition = "BIGINT")
    val articleId: Long = 0,

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    @ColumnDefault(value="NOW()")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT NOW() ON UPDATE NOW()")
    @ColumnDefault(value="NOW()")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL])
    val comments: List<Comment> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        return articleId == (other as Article).articleId
    }

    override fun hashCode() = Objects.hashCode(articleId)
}

data class ArticleDTO(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val title: String,
    val content: String,
    val articleId: Long,
    val userId: Long,
    val email: String,
    val password: String
)
data class ArticleRequest(val email: String, val password: String, val title: String, val content: String)
data class ArticleResponse(val articleId: Long, val email: String, val title: String, val content: String)

fun Article.toDTO(): ArticleDTO {
    return ArticleDTO(
        createdAt, updatedAt, title, content, articleId, user.userId, user.email, user.password
    )
}
fun ArticleDTO.toResponse(): ArticleResponse {
    return ArticleResponse(
        this.articleId,
        this.email,
        this.title,
        this.content
    )
}

@Repository
interface ArticleRepository: JpaRepository<Article, Long>

@Service
class ArticleService(private val repository: ArticleRepository) {
    fun getAll(): List<ArticleDTO> = repository.findAll().map{ it.toDTO() }

    fun getById(id: Long): Article = repository.findByIdOrNull(id) ?:
    throw EntityNotFoundException()

    fun getByIdWrapped(id: Long): ArticleDTO = getById(id).toDTO()

    fun create(article: Article): ArticleDTO = repository.save(article).toDTO()

    fun remove(id: Long) {
        if (repository.existsById(id)) repository.deleteById(id)
        else throw EntityNotFoundException()
    }

    fun update(id: Long, article: Article): ArticleDTO {
        return if (repository.existsById(id)) {
            repository.save(article).toDTO()
        } else throw EntityNotFoundException()
    }
}