package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.StudyCategoryDAO;
import dao.StudyRequestDAO;
import dto.RequestStatusDTO;
import dto.StudyRequestDTO;

public class StudyRequestService {
    private final StudyRequestDAO studyRequestDAO;
    private final StudyCategoryDAO studyCategoryDAO;

    public StudyRequestService() {
        this.studyRequestDAO = new StudyRequestDAO();
        this.studyCategoryDAO = new StudyCategoryDAO();
    }

    public boolean createRequest(int studentId, int firstId, Integer secondId, Integer thirdId) {
        if (!isValidRequestInput(firstId, secondId, thirdId)) {
            return false;
        }

        try {
            if (studyRequestDAO.existsRequestByStudentId(studentId)) {
                return false;
            }

            StudyRequestDTO request = new StudyRequestDTO();
            request.setStudentId(studentId);
            request.setFirstCategoryId(firstId);
            request.setSecondCategoryId(secondId);
            request.setThirdCategoryId(thirdId);
            request.setMatchedYn("N");

            return studyRequestDAO.insertRequest(request) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public StudyRequestDTO getMyRequest(int studentId) {
        try {
            return studyRequestDAO.findRequestByStudentId(studentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateRequest(int studentId, int firstId, Integer secondId, Integer thirdId) {
        if (!isValidRequestInput(firstId, secondId, thirdId)) {
            return false;
        }

        try {
            StudyRequestDTO existingRequest = studyRequestDAO.findRequestByStudentId(studentId);
            if (existingRequest == null || isMatched(existingRequest)) {
                return false;
            }

            StudyRequestDTO request = new StudyRequestDTO();
            request.setStudentId(studentId);
            request.setFirstCategoryId(firstId);
            request.setSecondCategoryId(secondId);
            request.setThirdCategoryId(thirdId);

            return studyRequestDAO.updateRequest(request) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRequest(int studentId) {
        try {
            StudyRequestDTO existingRequest = studyRequestDAO.findRequestByStudentId(studentId);
            if (existingRequest == null || isMatched(existingRequest)) {
                return false;
            }

            return studyRequestDAO.deleteRequestByStudentId(studentId) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RequestStatusDTO> getAllRequestStatus() {
        try {
            return studyRequestDAO.findAllRequestStatus();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isValidRequestInput(int firstId, Integer secondId, Integer thirdId) {
        if (firstId <= 0) {
            return false;
        }
        if (hasDuplicateCategory(firstId, secondId, thirdId)) {
            return false;
        }

        try {
            return studyCategoryDAO.existsCategoryId(firstId)
                    && isValidOptionalCategoryId(secondId)
                    && isValidOptionalCategoryId(thirdId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidOptionalCategoryId(Integer categoryId) throws SQLException {
        return categoryId == null || (categoryId > 0 && studyCategoryDAO.existsCategoryId(categoryId));
    }

    private boolean hasDuplicateCategory(int firstId, Integer secondId, Integer thirdId) {
        Set<Integer> categoryIds = new HashSet<>();
        categoryIds.add(firstId);

        if (secondId != null && !categoryIds.add(secondId)) {
            return true;
        }

        return thirdId != null && !categoryIds.add(thirdId);
    }

    private boolean isMatched(StudyRequestDTO request) {
        return "Y".equalsIgnoreCase(request.getMatchedYn());
    }
}
