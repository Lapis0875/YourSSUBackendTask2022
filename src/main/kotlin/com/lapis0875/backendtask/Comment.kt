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
@Table(name = "comment")
class Comment (
    @Column(name = "content", columnDefinition = "VARCHAR(255)")
    @field:NotBlank
    var content: String,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id", columnDefinition = "BIGINT")
    val commentId: Long = 0,

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    @ColumnDefault(value="NOW()")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT NOW() ON UPDATE NOW()")
    @ColumnDefault(value="NOW()")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(optional = false, cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE])
    @JoinColumn(name = "article_id")
    val article: Article,

    @ManyToOne(optional = false, cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE])
    @JoinColumn(name = "user_id")
    val user: User
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        return commentId == (other as Comment).commentId
    }

    override fun hashCode() = Objects.hashCode(commentId)
}

data class CommentDTO(
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val commentId: Long,
    val userId: Long,
    val email: String,
    val password: String,
    val articleId: Long
)
data class CommentRequest(val email: String, val password: String, val content: String)
data class CommentResponse(val commentId: Long, val email: String, val content: String)

fun Comment.toDTO(): CommentDTO {
    return CommentDTO(
        content, createdAt, updatedAt, commentId,
        this.user!!.userId, this.user!!.email, this.user!!.password,
        this.article!!.articleId
    )
}
fun CommentDTO.toResponse(): CommentResponse {
    return CommentResponse(this.commentId, this.email, this.content)
}


@Repository
interface CommentRepository: JpaRepository<Comment, Long>

@Service
class CommentService(private val repository: CommentRepository) {
    fun getAll(): List<CommentDTO> = repository.findAll().map{ it.toDTO() }

    fun getById(id: Long): Comment = repository.findByIdOrNull(id) ?:
    throw EntityNotFoundException()

    fun getByIdWrapped(id: Long): CommentDTO = getById(id).toDTO()

    fun create(comment: Comment): CommentDTO = repository.save(comment).toDTO()

    fun remove(id: Long) {
        if (repository.existsById(id)) repository.deleteById(id)
        else throw EntityNotFoundException()
    }

    fun update(id: Long, comment: Comment): CommentDTO {
        return if (repository.existsById(id)) {
            repository.save(comment).toDTO()
        } else throw EntityNotFoundException()
    }
}