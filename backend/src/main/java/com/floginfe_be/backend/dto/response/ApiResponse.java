package com.floginfe_be.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private String message;
	private boolean success;
	private T data;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp = LocalDateTime.now();

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(message, true, data, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> failure(String message) {
		return new ApiResponse<>(message, false, null, LocalDateTime.now());
	}
}
