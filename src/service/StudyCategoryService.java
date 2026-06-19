package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.StudyCategoryDAO;
import dto.StudyCategoryDTO;

public class StudyCategoryService {
    private final StudyCategoryDAO studyCategoryDAO;

    public StudyCategoryService() {
        this.studyCategoryDAO = new StudyCategoryDAO();
    }

    public List<StudyCategoryDTO> getAllCategories() {
        try {
            return studyCategoryDAO.findAllCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean isValidCategoryId(int categoryId) {
        if (categoryId <= 0) {
            return false;
        }

        try {
            return studyCategoryDAO.existsCategoryId(categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
