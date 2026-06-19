package view;

import java.util.List;
import java.util.Scanner;

import dto.GroupResultDTO;
import dto.StudyCategoryDTO;
import dto.StudyRequestDTO;

public class StudentView {
    private final Scanner sc;

    public StudentView() {
        this.sc = new Scanner(System.in);
    }

    public int showStudentMenu() {
        System.out.println();
        System.out.println("========== 교육생 메뉴 ==========");
        System.out.println("1. 스터디 카테고리 조회");
        System.out.println("2. 희망 스터디 신청");
        System.out.println("3. 내 신청 정보 조회");
        System.out.println("4. 내 신청 정보 수정");
        System.out.println("5. 내 신청 취소");
        System.out.println("6. 내 조 조회");
        System.out.println("7. 로그아웃");
        System.out.println("0. 종료");
        System.out.print("선택 >> ");

        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return 0;
        }
    }

    public void printCategories(List<StudyCategoryDTO> categories) {
        System.out.println();
        System.out.println("========== 스터디 카테고리 ==========");

        if (categories == null || categories.isEmpty()) {
            System.out.println("등록된 카테고리가 없습니다.");
            return;
        }

        for (StudyCategoryDTO category : categories) {
            System.out.println(category.getCategoryId() + ". " + category.getCategoryName()
                    + " - " + nullToBlank(category.getDescription()));
        }
    }

    public int inputFirstCategory() {
        System.out.print("1순위 카테고리 ID >> ");
        return inputIntOrZero();
    }

    public Integer inputSecondCategory() {
        System.out.print("2순위 카테고리 ID (선택 안 함: 0) >> ");
        return inputOptionalCategory();
    }

    public Integer inputThirdCategory() {
        System.out.print("3순위 카테고리 ID (선택 안 함: 0) >> ");
        return inputOptionalCategory();
    }

    public void printMyRequest(StudyRequestDTO request) {
        System.out.println();
        System.out.println("========== 내 신청 정보 ==========");

        if (request == null) {
            System.out.println("신청 정보가 없습니다.");
            return;
        }

        System.out.println("신청 번호: " + request.getRequestId());
        System.out.println("1순위 카테고리 ID: " + request.getFirstCategoryId());
        System.out.println("2순위 카테고리 ID: " + optionalCategoryText(request.getSecondCategoryId()));
        System.out.println("3순위 카테고리 ID: " + optionalCategoryText(request.getThirdCategoryId()));
        System.out.println("매칭 여부: " + request.getMatchedYn());
    }

    public void printMyGroup(List<GroupResultDTO> groupResults) {
        System.out.println();
        System.out.println("========== 내 조 조회 ==========");

        if (groupResults == null || groupResults.isEmpty()) {
            System.out.println("아직 배정된 스터디 조가 없습니다.");
            return;
        }

        GroupResultDTO first = groupResults.get(0);
        System.out.println("카테고리: " + first.getCategoryName());
        System.out.println("조 이름: " + first.getGroupName());
        System.out.println("조원 목록");

        for (GroupResultDTO result : groupResults) {
            System.out.println("- " + result.getStudentName()
                    + " / " + result.getMemberRole()
                    + " / " + result.getMatchedPriority() + "순위");
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    private Integer inputOptionalCategory() {
        int categoryId = inputIntOrZero();
        if (categoryId == 0) {
            return null;
        }

        return categoryId;
    }

    private int inputIntOrZero() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return 0;
        }
    }

    private String optionalCategoryText(Integer categoryId) {
        if (categoryId == null) {
            return "선택 안 함";
        }

        return String.valueOf(categoryId);
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
