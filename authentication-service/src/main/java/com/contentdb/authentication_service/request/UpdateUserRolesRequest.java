package com.contentdb.authentication_service.request;

import java.util.List;

public record UpdateUserRolesRequest(List<String> newRoles) {
}
