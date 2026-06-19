package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.StudyCategoryDTO;
import util.DBUtil;

public class StudyCategoryDAO {
    public StudyCategoryDAO() {
    }

    public List<StudyCategoryDTO> findAllCategories() throws SQLException {
        String sql = """
                SELECT
                    category_id,
                    category_name,
                    description
                FROM study_category
                ORDER BY category_id
                """;

        List<StudyCategoryDTO> categories = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(toStudyCategoryDTO(rs));
            }
        }

        return categories;
    }

    public boolean existsCategoryId(int categoryId) throws SQLException {
        String sql = "SELECT 1 FROM study_category WHERE category_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String findCategoryNameById(int categoryId) throws SQLException {
        String sql = "SELECT category_name FROM study_category WHERE category_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                }

                return null;
            }
        }
    }

    private StudyCategoryDTO toStudyCategoryDTO(ResultSet rs) throws SQLException {
        StudyCategoryDTO category = new StudyCategoryDTO();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));

        return category;
    }
}
