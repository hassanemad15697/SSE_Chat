package com.test.pushnotification.response;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse implements Response{
    private String message;
}
