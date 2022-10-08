package com.lapis0875.backendtask

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException
import kotlin.jvm.Throws

@RestController
@RequestMapping("/")
class APIController {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var commentService: CommentService

    @PostMapping("/signup")
    @ResponseBody
    fun signup(@RequestBody req: UserRequest): UserResponse {
        // 기존에 가입된 유저가 있는지 확인
        try {
            userService.getByPayload(req.email, req.password)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot sign in with already existing email")       // 기존 유저 존재시, 같은 정보로의 가입을 막아야 함.
        } catch (e: EntityNotFoundException) {
            val user: UserDTO = userService.create(User(req.email, req.password, req.username!!))       // 신규 유저 생성.
            return user.toResponse()
        }
    }

    @DeleteMapping("/signout")
    @ResponseStatus(HttpStatus.OK)
    fun signout(@RequestBody req: UserRequest) {
        val user: User = try {
            userService.getByPayload(req.email, req.password)
        } catch (e: EntityNotFoundException) { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }  // 유저가 존재하지 않을 때 오류 발생.
        userService.remove(user.userId)
    }

    @PostMapping("/article")
    fun createArticle(@RequestBody req: ArticleRequest): ArticleResponse {
        try {
            val user: User = userService.getByPayload(req.email, req.password)
            return articleService.create(Article(content = req.content, title = req.title, user = user)).toResponse()
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot create article with invalid account",
                e
            )
        } catch (e: TransactionSystemException) {
            if (e.rootCause is ConstraintViolationException) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot have article with empty title or content",
                    e
                )
            } else { throw e }
        }
    }

    @PutMapping("/article/{id}")
    fun editArticle(@PathVariable id: Long, @RequestBody req: ArticleRequest): ArticleResponse {
        try {
            val user: User = userService.getByPayload(req.email, req.password)
            return articleService.update(id, Article(content = req.content, title = req.title, articleId = id, user = user)).toResponse()
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot create article with invalid account",
                e
            )
        }  catch (e: TransactionSystemException) {
            if (e.rootCause is ConstraintViolationException) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot have article with empty title or content",
                    e
                )
            } else { throw e }
        }
    }

    @DeleteMapping("/article/{id}")
    fun removeArticle(@PathVariable id: Long, @RequestBody req: UserRequest) {
        val article: ArticleDTO = articleService.getByIdWrapped(id)
        if (article.run {email == req.email && password == req.password})
            articleService.remove(id)
        else
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only author of article can remove it",
            )
    }

    @PostMapping("/comment/{id}")
    @Throws(ResponseStatusException::class)
    fun writeComment(@PathVariable id: Long, @RequestBody req: CommentRequest): CommentResponse {
        try {
            val author: User = userService.getByPayload(req.email, req.password)
            val article: Article = articleService.getById(id)
            return commentService.create(Comment(req.content, article = article, user = author)).toResponse()
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot create comment with invalid account",
                e
            )
        } catch (e: TransactionSystemException) {
            if (e.rootCause is ConstraintViolationException) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot have comment with empty content",
                    e
                )
            } else { throw e }
        }
    }

    @PutMapping("/comment/{articleId}/{commentId}")
    fun editComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody req: CommentRequest
    ): CommentResponse {
        val article: Article = articleService.getById(articleId)
        if (article.run { user.email != req.email || user.password != req.password }) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only author of the comment can edit it"
            )
        }
        try {
            return commentService.update(commentId, Comment(req.content, commentId = commentId, article = article, user = article.user)).toResponse()
        } catch (e: TransactionSystemException) {
            if (e.rootCause is ConstraintViolationException) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot have comment with content",
                    e
                )
            } else { throw e }
        }
    }

    @DeleteMapping("/comment/{articleId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    fun removeComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody req: UserRequest
    ) {
        val article: ArticleDTO = articleService.getByIdWrapped(articleId)
        val comment: CommentDTO = commentService.getByIdWrapped(commentId)
        if (comment.commentId == commentId && article.run {
            this.articleId == articleId
            && this.email == req.email
            && this.password == req.password
        }) return commentService.remove(commentId)
        else
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only author of comment can remove it",
            )
    }
}