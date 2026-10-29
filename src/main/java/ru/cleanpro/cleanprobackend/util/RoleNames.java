package ru.cleanpro.cleanprobackend.util;

import java.util.Map;

public final class RoleNames {

    private static final Map<String, String> NAMES = Map.of(
            "ADMIN", "Админ",
            "EMPLOYEE", "Сотрудник",
            "USER", "Юзер"
    );

    private RoleNames() {}

    // Превращает код роли в читаемое название
    public static String humanize(String role) {
        if (role == null) return "—";
        return NAMES.getOrDefault(role, role);
    }
}