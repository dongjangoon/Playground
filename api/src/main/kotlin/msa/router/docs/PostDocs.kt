package msa.router.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import msa.post.dto.CreatePostRequest
import msa.post.dto.PostResponse
import msa.post.dto.UpdatePostRequest
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@RouterOperations(
    RouterOperation(
        path = "/v1/api/posts",
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "createPost",
            requestBody = RequestBody(
                content = [
                    Content(
                        schema = Schema(implementation = CreatePostRequest::class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ]
            ),
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = PostResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = "/v1/posts/{id}",
        method = [RequestMethod.PUT],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "updatePost",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    schema = Schema(type = "string")
                )
             ],
            requestBody = RequestBody(
                content = [
                    Content(
                        schema = Schema(implementation = UpdatePostRequest::class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ]
            ),
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = PostResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = "/v1/api/posts/{id}/publish",
        method = [RequestMethod.PATCH],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "publishPost",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    schema = Schema(type = "string")
                )
             ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = PostResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = "/v1/api/posts/{id}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "getPost",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    schema = Schema(type = "string")
                )
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = PostResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = "/v1/api/posts",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "getPosts",
            parameters = [
                Parameter(
                    `in` = ParameterIn.QUERY,
                    name = "category",
                    required = false,
                    schema = Schema(type = "string", example = "BACKEND")
                ),
                Parameter(
                    `in` = ParameterIn.QUERY,
                    name = "status",
                    required = false,
                    schema = Schema(type = "string", example = "PUBLISHED")
                )
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = PostResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = "/v1/api/posts/author/{authorId}",
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        operation = Operation(
            operationId = "getPostsByAuthor",
            parameters = [
                Parameter(
                    `in` = ParameterIn.PATH,
                    name = "authorId",
                    required = true,
                    schema = Schema(type = "string")
                )
            ],
            responses = [
                ApiResponse(
                    responseCode = "200",
                    content = [Content(schema = Schema(implementation = Array<PostResponse>::class))]
                )
            ]
        )
    ),
)
annotation class PostDocs
