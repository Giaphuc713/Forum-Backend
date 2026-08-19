package com.backend.Forum.dto.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic API Response wrapper")
public class ApiResponse<T> {
    @Schema(description = "Response status code (e.g., 200 for success)", example = "200")
    private int code;

    @Schema(description = "Response message", example = "Success")
    private String message;

    @Schema(description = "Data result", nullable = true)
    private T result;

    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
