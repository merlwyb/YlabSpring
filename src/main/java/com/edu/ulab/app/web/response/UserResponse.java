package com.edu.ulab.app.web.response;

import com.edu.ulab.app.dto.BookDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String fullName;
    private String title;
    private int age;
    private List<BookDto> booksList;
}