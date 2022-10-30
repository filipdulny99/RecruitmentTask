package pl.miquido.java.recruitmenttask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String exceptionType;
    private String message;

    public ApiError(Exception e) {
        exceptionType = e.getClass().getSimpleName();
        message = e.getLocalizedMessage();
    }
}
