package view;

import java.util.List;
import java.util.Scanner;

import dto.GroupResultDTO;
import dto.RequestStatusDTO;
import dto.StudentDTO;
import dto.StudyCategoryDTO;

public class AdminView {
    private final Scanner sc;

    public AdminView() {
        this.sc = new Scanner(System.in);
    }

    public int showAdminMenu() {
        System.out.println();
        System.out.println("========== 관리자 메뉴 ==========");
        System.out.println("1. 전체 회원 조회");
        System.out.println("2. 스터디 카테고리 조회");
        System.out.println("3. 전체 신청 현황 조회");
        System.out.println("4. 자동 매칭 실행");
        System.out.println("5. 전체 조 편성 결과 조회");
        System.out.println("6. 매칭 결과 초기화");
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

    public void printStudents(List<StudentDTO> students) {
        System.out.println();
        System.out.println("========== 전체 회원 조회 ==========");

        if (students == null || students.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
            return;
        }

        for (StudentDTO student : students) {
            System.out.println(student.getStudentId()
                    + " / " + student.getLoginId()
                    + " / " + student.getName()
                    + " / " + student.getCohort() + "기"
                    + " / " + nullToBlank(student.getEmail())
                    + " / " + student.getRole());
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
            System.out.println(category.getCategoryId()
                    + ". " + category.getCategoryName()
                    + " - " + nullToBlank(category.getDescription()));
        }
    }

    public void printRequestStatus(List<RequestStatusDTO> requests) {
        System.out.println();
        System.out.println("========== 전체 신청 현황 ==========");

        if (requests == null || requests.isEmpty()) {
            System.out.println("신청자가 없습니다.");
            return;
        }

        for (RequestStatusDTO request : requests) {
            System.out.println("신청번호 " + request.getRequestId()
                    + " / 학생ID " + request.getStudentId()
                    + " / " + request.getStudentName()
                    + " / 1순위: " + request.getFirstCategoryName()
                    + " / 2순위: " + optionalText(request.getSecondCategoryName())
                    + " / 3순위: " + optionalText(request.getThirdCategoryName())
                    + " / 매칭: " + request.getMatchedYn());
        }
    }

    public void printGroupResults(List<GroupResultDTO> groups) {
        System.out.println();
        System.out.println("========== 전체 조 편성 결과 ==========");

        if (groups == null || groups.isEmpty()) {
            System.out.println("아직 생성된 스터디 조가 없습니다.");
            return;
        }

        int currentGroupId = -1;
        for (GroupResultDTO group : groups) {
            if (currentGroupId != group.getGroupId()) {
                currentGroupId = group.getGroupId();
                System.out.println();
                System.out.println("[" + group.getCategoryName() + "] " + group.getGroupName());
            }

            System.out.println("- " + group.getStudentName()
                    + " / " + group.getMemberRole()
                    + " / " + group.getMatchedPriority() + "순위");
        }
    }

    public void printUnmatchedRequests(List<RequestStatusDTO> requests) {
        System.out.println();
        System.out.println("========== 미배정 신청자 ==========");

        if (requests == null || requests.isEmpty()) {
            System.out.println("미배정 신청자가 없습니다.");
            return;
        }

        boolean hasUnmatched = false;
        for (RequestStatusDTO request : requests) {
            if (!"N".equalsIgnoreCase(request.getMatchedYn())) {
                continue;
            }

            hasUnmatched = true;
            System.out.println("신청번호 " + request.getRequestId()
                    + " / 학생ID " + request.getStudentId()
                    + " / " + request.getStudentName()
                    + " / 1순위: " + request.getFirstCategoryName()
                    + " / 2순위: " + optionalText(request.getSecondCategoryName())
                    + " / 3순위: " + optionalText(request.getThirdCategoryName()));
        }

        if (!hasUnmatched) {
            System.out.println("미배정 신청자가 없습니다.");
        }
    }

    public boolean confirmMatching() {
        System.out.print("자동 매칭을 실행하시겠습니까? (y/n) >> ");
        return isYes(sc.nextLine());
    }

    public boolean confirmReset() {
        System.out.print("매칭 결과를 초기화하시겠습니까? (y/n) >> ");
        return isYes(sc.nextLine());
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    private boolean isYes(String value) {
        return "y".equalsIgnoreCase(value == null ? "" : value.trim());
    }

    private String optionalText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "선택 안 함";
        }

        return value;
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
