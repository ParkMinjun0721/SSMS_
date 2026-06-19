package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.GroupResultDTO;
import dto.StudyGroupDTO;
import util.DBUtil;

public class StudyGroupDAO {
    public StudyGroupDAO() {
    }

    public int insertGroup(StudyGroupDTO group) {
        String nextIdSql = "SELECT seq_group.NEXTVAL FROM dual";
        String insertSql = """
                INSERT INTO study_group (
                    group_id,
                    category_id,
                    group_name,
                    group_status
                ) VALUES (
                    ?,
                    ?,
                    ?,
                    NVL(?, 'MATCHED')
                )
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement nextIdPs = conn.prepareStatement(nextIdSql);
                ResultSet rs = nextIdPs.executeQuery()) {
            if (!rs.next()) {
                return 0;
            }

            int groupId = rs.getInt(1);

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, groupId);
                insertPs.setInt(2, group.getCategoryId());
                insertPs.setString(3, group.getGroupName());
                insertPs.setString(4, group.getGroupStatus());
                insertPs.executeUpdate();
            }

            return groupId;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<GroupResultDTO> findAllGroupResults() {
        String sql = """
                SELECT
                    sg.group_id,
                    sg.group_name,
                    sc.category_name,
                    s.student_id,
                    s.name AS student_name,
                    sgm.member_role,
                    sgm.matched_priority
                FROM study_group sg
                JOIN study_group_member sgm
                  ON sg.group_id = sgm.group_id
                JOIN student s
                  ON sgm.student_id = s.student_id
                JOIN study_category sc
                  ON sg.category_id = sc.category_id
                ORDER BY
                    sg.group_id,
                    CASE sgm.member_role
                        WHEN 'LEADER' THEN 0
                        ELSE 1
                    END,
                    sgm.group_member_id
                """;

        List<GroupResultDTO> results = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(toGroupResultDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<GroupResultDTO> findGroupByStudentId(int studentId) {
        String sql = """
                SELECT
                    sg.group_id,
                    sg.group_name,
                    sc.category_name,
                    s.student_id,
                    s.name AS student_name,
                    sgm.member_role,
                    sgm.matched_priority
                FROM study_group sg
                JOIN study_group_member sgm
                  ON sg.group_id = sgm.group_id
                JOIN student s
                  ON sgm.student_id = s.student_id
                JOIN study_category sc
                  ON sg.category_id = sc.category_id
                WHERE sg.group_id = (
                    SELECT group_id
                    FROM study_group_member
                    WHERE student_id = ?
                )
                ORDER BY
                    CASE sgm.member_role
                        WHEN 'LEADER' THEN 0
                        ELSE 1
                    END,
                    sgm.group_member_id
                """;

        List<GroupResultDTO> results = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(toGroupResultDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public int deleteAllGroups() {
        String sql = "DELETE FROM study_group";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private GroupResultDTO toGroupResultDTO(ResultSet rs) throws SQLException {
        GroupResultDTO result = new GroupResultDTO();
        result.setGroupId(rs.getInt("group_id"));
        result.setGroupName(rs.getString("group_name"));
        result.setCategoryName(rs.getString("category_name"));
        result.setStudentId(rs.getInt("student_id"));
        result.setStudentName(rs.getString("student_name"));
        result.setMemberRole(rs.getString("member_role"));
        result.setMatchedPriority(rs.getInt("matched_priority"));

        return result;
    }
}
