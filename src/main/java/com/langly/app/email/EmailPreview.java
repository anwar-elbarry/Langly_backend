package com.langly.app.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPreview {
    private String from;
    private String to;
    private String subject;
    private String message;
    private String loginLink;
    private String temporaryPassword;
}
