package util;

import dto.StudentDTO;

public class Session {
    public static StudentDTO loginUser;

    private Session() {
    }

    public static void login(StudentDTO student) {
        loginUser = student;
    }

    public static void logout() {
        loginUser = null;
    }

    public static boolean isLogin() {
        return loginUser != null;
    }
}
