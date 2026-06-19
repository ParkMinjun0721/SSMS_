package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.StudyCategoryDAO;
import dao.StudyGroupDAO;
import dao.StudyGroupMemberDAO;
import dao.StudyRequestDAO;
import dto.GroupResultDTO;
import dto.StudyGroupDTO;
import dto.StudyGroupMemberDTO;
import dto.StudyRequestDTO;

public class MatchingService {
    private static final int MIN_GROUP_SIZE = 3;
    private static final int TARGET_GROUP_SIZE = 5;
    private static final int MAX_GROUP_SIZE = 6;

    private final StudyRequestDAO studyRequestDAO;
    private final StudyGroupDAO studyGroupDAO;
    private final StudyGroupMemberDAO studyGroupMemberDAO;
    private final StudyCategoryDAO studyCategoryDAO;

    public MatchingService() {
        this.studyRequestDAO = new StudyRequestDAO();
        this.studyGroupDAO = new StudyGroupDAO();
        this.studyGroupMemberDAO = new StudyGroupMemberDAO();
        this.studyCategoryDAO = new StudyCategoryDAO();
    }

    public boolean runMatching() {
        try {
            List<StudyRequestDTO> unmatchedRequests = studyRequestDAO.findUnmatchedRequests();
            if (unmatchedRequests.isEmpty()) {
                return false;
            }

            Map<Integer, List<StudyRequestDTO>> requestsByCategory = groupByFirstCategoryId(unmatchedRequests);
            Map<Integer, Integer> groupNumbersByCategory = new HashMap<>();
            List<StudyRequestDTO> leftovers = new ArrayList<>();
            int matchedCount = 0;

            for (Map.Entry<Integer, List<StudyRequestDTO>> entry : requestsByCategory.entrySet()) {
                int categoryId = entry.getKey();
                List<StudyRequestDTO> categoryRequests = entry.getValue();
                Collections.shuffle(categoryRequests);

                if (categoryRequests.size() < MIN_GROUP_SIZE) {
                    leftovers.addAll(categoryRequests);
                    continue;
                }

                List<List<StudyRequestDTO>> groups = splitGroups(categoryRequests);
                for (List<StudyRequestDTO> groupRequests : groups) {
                    if (groupRequests.size() < MIN_GROUP_SIZE) {
                        leftovers.addAll(groupRequests);
                        continue;
                    }

                    int groupNumber = groupNumbersByCategory.getOrDefault(categoryId, 0) + 1;
                    groupNumbersByCategory.put(categoryId, groupNumber);

                    int groupId = createGroup(categoryId, groupNumber);
                    if (groupId == 0) {
                        leftovers.addAll(groupRequests);
                        continue;
                    }

                    matchedCount += addMembersToGroup(groupId, groupRequests, 1);
                }
            }

            matchedCount += assignLeftovers(leftovers);

            return matchedCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GroupResultDTO> getAllGroupResults() {
        return studyGroupDAO.findAllGroupResults();
    }

    public List<GroupResultDTO> getMyGroup(int studentId) {
        return studyGroupDAO.findGroupByStudentId(studentId);
    }

    public boolean resetMatching() {
        try {
            studyGroupMemberDAO.deleteAllGroupMembers();
            studyGroupDAO.deleteAllGroups();
            studyRequestDAO.resetAllMatchedYn();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<Integer, List<StudyRequestDTO>> groupByFirstCategoryId(List<StudyRequestDTO> requests) {
        Map<Integer, List<StudyRequestDTO>> requestsByCategory = new HashMap<>();

        for (StudyRequestDTO request : requests) {
            requestsByCategory
                    .computeIfAbsent(request.getFirstCategoryId(), key -> new ArrayList<>())
                    .add(request);
        }

        return requestsByCategory;
    }

    private List<List<StudyRequestDTO>> splitGroups(List<StudyRequestDTO> requests) {
        List<List<StudyRequestDTO>> groups = new ArrayList<>();

        if (requests.size() <= MAX_GROUP_SIZE) {
            groups.add(new ArrayList<>(requests));
            return groups;
        }

        for (int start = 0; start < requests.size(); start += TARGET_GROUP_SIZE) {
            int end = Math.min(start + TARGET_GROUP_SIZE, requests.size());
            groups.add(new ArrayList<>(requests.subList(start, end)));
        }

        List<StudyRequestDTO> lastGroup = groups.get(groups.size() - 1);
        if (lastGroup.size() < MIN_GROUP_SIZE && groups.size() >= 2) {
            List<StudyRequestDTO> previousGroup = groups.get(groups.size() - 2);

            while (!lastGroup.isEmpty() && previousGroup.size() < MAX_GROUP_SIZE) {
                previousGroup.add(lastGroup.remove(0));
            }

            if (lastGroup.isEmpty()) {
                groups.remove(groups.size() - 1);
            }
        }

        return groups;
    }

    private int createGroup(int categoryId, int groupNumber) throws SQLException {
        String categoryName = studyCategoryDAO.findCategoryNameById(categoryId);
        if (categoryName == null) {
            categoryName = "Category " + categoryId;
        }

        StudyGroupDTO group = new StudyGroupDTO();
        group.setCategoryId(categoryId);
        group.setGroupName(categoryName + " " + groupNumber + "조");
        group.setGroupStatus("MATCHED");

        return studyGroupDAO.insertGroup(group);
    }

    private int addMembersToGroup(int groupId, List<StudyRequestDTO> requests, int matchedPriority) throws SQLException {
        int matchedCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            StudyRequestDTO request = requests.get(i);
            String memberRole = i == 0 && matchedPriority == 1 ? "LEADER" : "MEMBER";

            if (addMemberToGroup(groupId, request, memberRole, matchedPriority)) {
                matchedCount++;
            }
        }

        return matchedCount;
    }

    private int assignLeftovers(List<StudyRequestDTO> leftovers) throws SQLException {
        int matchedCount = 0;

        for (StudyRequestDTO request : leftovers) {
            if (assignLeftoverByPriority(request, request.getSecondCategoryId(), 2)) {
                matchedCount++;
                continue;
            }

            if (assignLeftoverByPriority(request, request.getThirdCategoryId(), 3)) {
                matchedCount++;
            }
        }

        return matchedCount;
    }

    private boolean assignLeftoverByPriority(StudyRequestDTO request, Integer categoryId, int matchedPriority)
            throws SQLException {
        if (categoryId == null) {
            return false;
        }

        List<Integer> availableGroupIds = studyGroupMemberDAO.findAvailableGroupIdsByCategoryId(categoryId);
        for (Integer groupId : availableGroupIds) {
            if (groupId == null || studyGroupMemberDAO.countMembersByGroupId(groupId) >= MAX_GROUP_SIZE) {
                continue;
            }

            return addMemberToGroup(groupId, request, "MEMBER", matchedPriority);
        }

        return false;
    }

    private boolean addMemberToGroup(int groupId, StudyRequestDTO request, String memberRole, int matchedPriority)
            throws SQLException {
        StudyGroupMemberDTO member = new StudyGroupMemberDTO();
        member.setGroupId(groupId);
        member.setStudentId(request.getStudentId());
        member.setMemberRole(memberRole);
        member.setMatchedPriority(matchedPriority);

        int insertedCount = studyGroupMemberDAO.insertGroupMember(member);
        if (insertedCount != 1) {
            return false;
        }

        return studyRequestDAO.updateMatchedYn(request.getStudentId(), "Y") == 1;
    }
}
