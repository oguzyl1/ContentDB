package com.contentdb.authentication_service.request;

public record CompletePasswordResetRequest(
        String resetToken,
        ResetPasswordRequest resetPasswordRequest) {
}
