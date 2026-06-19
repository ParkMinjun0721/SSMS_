package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dto.RequestStatusDTO;
import dto.StudyRequestDTO;
import util.DBUtil;

public class StudyRequestDAO {
    public StudyRequestDAO() {
    }

    public int insertRequest(StudyRequestDTO request) throws SQLException {
        String sql = """
                INSERT INTO study_request (
                    request_id,
                    student_id,
                    first_category_id,
                    second_category_id,
                    third_category_id,
                    matched_yn
                ) VALUES (
                    seq_request.NEXTVAL,
                    ?,
                    ?,
                    ?,
                    ?,
                    NVL(?, 'N')
                )
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getStudentId());
            ps.setInt(2, request.getFirstCategoryId());
            setNullableInteger(ps, 3, request.getSecondCategoryId());
            setNullableInteger(ps, 4, request.getThirdCategoryId());
            ps.setString(5, request.getMatchedYn());

            return ps.executeUpdate();
        }
    }

    public boolean existsRequestByStudentId(int studentId) throws SQLException {
        String sql = "SELECT 1 FROM study_request WHERE student_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public StudyRequestDTO findRequestByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT
                    request_id,
                    student_id,
                    first_category_id,
                    second_category_id,
                    third_category_id,
                    matched_yn,
                    created_at,
                    updated_at
                FROM study_request
                WHERE student_id = ?
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toStudyRequestDTO(rs);
                }

                return null;
            }
        }
    }

    public int updateRequest(StudyRequestDTO request) throws SQLException {
        String sql = """
                UPDATE study_request
                SET first_category_id = ?,
                    second_category_id = ?,
                    third_category_id = ?,
                    updated_at = SYSDATE
                WHERE student_id = ?
                  AND matched_yn = 'N'
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getFirstCategoryId());
            setNullableInteger(ps, 2, request.getSecondCategoryId());
            setNullableInteger(ps, 3, request.getThirdCategoryId());
            ps.setInt(4, request.getStudentId());

            return ps.executeUpdate();
        }
    }

    public int deleteRequestByStudentId(int studentId) throws SQLException {
        String sql = """
                DELETE FROM study_request
                WHERE student_id = ?
                  AND matched_yn = 'N'
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            return ps.executeUpdate();
        }
    }

    public List<RequestStatusDTO> findAllRequestStatus() throws SQLException {
        String sql = """
                SELECT
                    sr.request_id,
                    sr.student_id,
                    s.name AS student_name,
                    fc.category_name AS first_category_name,
                    sc.category_name AS second_category_name,
                    tc.category_name AS third_category_name,
                    sr.matched_yn
                FROM study_request sr
                JOIN student s
                  ON sr.student_id = s.student_id
                JOIN study_category fc
                  ON sr.first_category_id = fc.category_id
                LEFT JOIN study_category sc
                  ON sr.second_category_id = sc.category_id
                LEFT JOIN study_category tc
                  ON sr.third_category_id = tc.category_id
                ORDER BY sr.request_id
                """;

        List<RequestStatusDTO> statuses = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                statuses.add(toRequestStatusDTO(rs));
            }
        }

        return statuses;
    }

    public List<StudyRequestDTO> findUnmatchedRequests() throws SQLException {
        String sql = """
                SELECT
                    request_id,
                    student_id,
                    first_category_id,
                    second_category_id,
                    third_category_id,
                    matched_yn,
                    created_at,
                    updated_at
                FROM study_request
                WHERE matched_yn = 'N'
                ORDER BY request_id
                """;

        List<StudyRequestDTO> requests = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(toStudyRequestDTO(rs));
            }
        }

        return requests;
    }

    public int updateMatchedYn(int studentId, String matchedYn) throws SQLException {
        String sql = """
                UPDATE study_request
                SET matched_yn = ?,
                    updated_at = SYSDATE
                WHERE student_id = ?
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matchedYn);
            ps.setInt(2, studentId);

            return ps.executeUpdate();
        }
    }

    public int resetAllMatchedYn() throws SQLException {
        String sql = """
                UPDATE study_request
                SET matched_yn = 'N',
                    updated_at = SYSDATE
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    private StudyRequestDTO toStudyRequestDTO(ResultSet rs) throws SQLException {
        StudyRequestDTO request = new StudyRequestDTO();
        request.setRequestId(rs.getInt("request_id"));
        request.setStudentId(rs.getInt("student_id"));
        request.setFirstCategoryId(rs.getInt("first_category_id"));
        request.setSecondCategoryId(getNullableInteger(rs, "second_category_id"));
        request.setThirdCategoryId(getNullableInteger(rs, "third_category_id"));
        request.setMatchedYn(rs.getString("matched_yn"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setUpdatedAt(rs.getTimestamp("updated_at"));

        return request;
    }

    private RequestStatusDTO toRequestStatusDTO(ResultSet rs) throws SQLException {
        RequestStatusDTO status = new RequestStatusDTO();
        status.setRequestId(rs.getInt("request_id"));
        status.setStudentId(rs.getInt("student_id"));
        status.setStudentName(rs.getString("student_name"));
        status.setFirstCategoryName(rs.getString("first_category_name"));
        status.setSecondCategoryName(rs.getString("second_category_name"));
        status.setThirdCategoryName(rs.getString("third_category_name"));
        status.setMatchedYn(rs.getString("matched_yn"));

        return status;
    }

    private void setNullableInteger(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.NUMERIC);
            return;
        }

        ps.setInt(parameterIndex, value);
    }

    private Integer getNullableInteger(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);
        if (rs.wasNull()) {
            return null;
        }

        return value;
    }
}
