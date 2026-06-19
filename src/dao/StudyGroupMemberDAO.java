package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.StudyGroupMemberDTO;
import util.DBUtil;

public class StudyGroupMemberDAO {
    public StudyGroupMemberDAO() {
    }

    public int insertGroupMember(StudyGroupMemberDTO member) {
        String sql = """
                INSERT INTO study_group_member (
                    group_member_id,
                    group_id,
                    student_id,
                    member_role,
                    matched_priority
                ) VALUES (
                    seq_group_member.NEXTVAL,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, member.getGroupId());
            ps.setInt(2, member.getStudentId());
            ps.setString(3, member.getMemberRole());
            ps.setInt(4, member.getMatchedPriority());

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countMembersByGroupId(int groupId) {
        String sql = "SELECT COUNT(*) FROM study_group_member WHERE group_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Integer> findAvailableGroupIdsByCategoryId(int categoryId) {
        String sql = """
                SELECT sg.group_id
                FROM study_group sg
                LEFT JOIN study_group_member sgm
                  ON sg.group_id = sgm.group_id
                WHERE sg.category_id = ?
                GROUP BY sg.group_id
                HAVING COUNT(sgm.group_member_id) < 6
                ORDER BY sg.group_id
                """;

        List<Integer> groupIds = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    groupIds.add(rs.getInt("group_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupIds;
    }

    public int deleteAllGroupMembers() {
        String sql = "DELETE FROM study_group_member";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
