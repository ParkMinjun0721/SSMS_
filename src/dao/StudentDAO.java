package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.StudentDTO;
import util.DBUtil;

public class StudentDAO {
    public StudentDAO() {
    }

    public int insertStudent(StudentDTO student) throws SQLException {
        String sql = """
                INSERT INTO student (
                    student_id,
                    login_id,
                    password,
                    name,
                    cohort,
                    email,
                    role
                ) VALUES (
                    seq_student.NEXTVAL,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getLoginId());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getName());
            ps.setInt(4, student.getCohort());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getRole());

            return ps.executeUpdate();
        }
    }

    public boolean existsLoginId(String loginId) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE login_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM student WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public StudentDTO login(String loginId, String password) throws SQLException {
        String sql = """
                SELECT
                    student_id,
                    login_id,
                    password,
                    name,
                    cohort,
                    email,
                    role,
                    created_at
                FROM student
                WHERE login_id = ?
                  AND password = ?
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toStudentDTO(rs);
                }

                return null;
            }
        }
    }

    public List<StudentDTO> findAllStudents() throws SQLException {
        String sql = """
                SELECT
                    student_id,
                    login_id,
                    password,
                    name,
                    cohort,
                    email,
                    role,
                    created_at
                FROM student
                ORDER BY student_id
                """;

        List<StudentDTO> students = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(toStudentDTO(rs));
            }
        }

        return students;
    }

    private StudentDTO toStudentDTO(ResultSet rs) throws SQLException {
        StudentDTO student = new StudentDTO();
        student.setStudentId(rs.getInt("student_id"));
        student.setLoginId(rs.getString("login_id"));
        student.setPassword(rs.getString("password"));
        student.setName(rs.getString("name"));
        student.setCohort(rs.getInt("cohort"));
        student.setEmail(rs.getString("email"));
        student.setRole(rs.getString("role"));
        student.setCreatedAt(rs.getTimestamp("created_at"));

        return student;
    }
}
