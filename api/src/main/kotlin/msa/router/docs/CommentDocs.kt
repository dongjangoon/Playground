package msa.router.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import msa.comment.dto.CommentResponse
import msa.comment.dto.CreateCommentRequest
import msa.comment.dto.UpdateCommentRequest
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@RouterOperations(
    RouterOperation(
        path = "/v1/api/comments",
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation =
        Operation(
            tags = ["Comment"],
            operationId = "createComment",
            summary = "댓글 작성",
            requestBody =
            RequestBody(
                content = [
                    Content(
                        schema = Schema(implementation = CreateCommentRequest::class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                    ),
                ],
            ),
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = CommentResponse::class))],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/{id}",
        method = [RequestMethod.PUT],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation =
        Operation(
            tags = ["Comment"],
            operationId = "updateComment",
            summary = "댓글 수정",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    description = "댓글 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            requestBody = RequestBody(
                content = [
                    Content(
                        schema = Schema(implementation = UpdateCommentRequest::class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                    ),
                ],
            ),
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = CommentResponse::class))],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/{id}",
        method = [RequestMethod.DELETE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation =
        Operation(
            tags = ["Comment"],
            operationId = "deleteComment",
            summary = "댓글 삭제",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    description = "댓글 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [
                        Content(
                            schema = Schema(
                                type = "string",
                                example = "댓글이 삭제되었습니다."
                            )
                        )
                    ],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/{id}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation =
        Operation(
            tags = ["Comment"],
            operationId = "getComment",
            summary = "댓글 조회",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    description = "댓글 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = CommentResponse::class))],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/post/{postId}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            tags = ["Comment"],
            operationId = "getCommentsByPost",
            summary = "게시글의 댓글 조회",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "postId",
                    required = false,
                    description = "게시글 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = Array<CommentResponse>::class))],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/author/{authorId}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            tags = ["Comment"],
            operationId = "getCommentsByAuthor",
            summary = "작성자의 댓글 조회",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "authorId",
                    required = true,
                    description = "작성자 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = Array<CommentResponse>::class))],
                ),
            ],
        ),
    ),
    RouterOperation(
        path = "/v1/api/comments/replies/{parentId}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            tags = ["Comment"],
            operationId = "getReplies",
            summary = "대댓글 조회",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "parentId",
                    required = true,
                    description = "부모 댓글 ID",
                    schema = Schema(type = "string"),
                ),
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = Array<CommentResponse>::class))],
                ),
            ],
        ),
    ),
)
annotation class CommentDocs()
