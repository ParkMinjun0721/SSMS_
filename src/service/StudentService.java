package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.StudentDAO;
import dto.StudentDTO;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public boolean register(StudentDTO student) {
        if (student == null) {
            return false;
        }
        if (isBlank(student.getLoginId()) || isBlank(student.getPassword()) || isBlank(student.getName())) {
            return false;
        }

        try {
            if (studentDAO.existsLoginId(student.getLoginId())) {
                return false;
            }

            if (!isBlank(student.getEmail()) && studentDAO.existsEmail(student.getEmail())) {
                return false;
            }

            student.setRole("USER");
            if (student.getCohort() == 0) {
                student.setCohort(7);
            }
            if (isBlank(student.getEmail())) {
                student.setEmail(null);
            }

            return studentDAO.insertStudent(student) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public StudentDTO login(String loginId, String password) {
        if (isBlank(loginId) || isBlank(password)) {
            return null;
        }

        try {
            return studentDAO.login(loginId, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<StudentDTO> getAllStudents() {
        try {
            return studentDAO.findAllStudents();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
