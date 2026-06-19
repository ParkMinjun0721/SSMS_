package controller;

import service.MatchingService;
import service.StudentService;
import service.StudyCategoryService;
import service.StudyRequestService;
import view.AdminView;

public class AdminController {
    private final AdminView adminView;
    private final StudentService studentService;
    private final StudyCategoryService studyCategoryService;
    private final StudyRequestService studyRequestService;
    private final MatchingService matchingService;

    public AdminController() {
        this.adminView = new AdminView();
        this.studentService = new StudentService();
        this.studyCategoryService = new StudyCategoryService();
        this.studyRequestService = new StudyRequestService();
        this.matchingService = new MatchingService();
    }

    public void run() {
        boolean running = true;

        while (running) {
            int menu = adminView.showAdminMenu();

            switch (menu) {
            case 1:
                showStudents();
                break;
            case 2:
                showCategories();
                break;
            case 3:
                showRequestStatus();
                break;
            case 4:
                runMatching();
                break;
            case 5:
                showGroupResults();
                break;
            case 6:
                resetMatching();
                break;
            case 7:
                adminView.printMessage("로그아웃합니다.");
                running = false;
                break;
            case 0:
                adminView.printMessage("프로그램을 종료합니다.");
                System.exit(0);
                break;
            default:
                adminView.printMessage("잘못된 메뉴입니다.");
                break;
            }
        }
    }

    private void showStudents() {
        adminView.printStudents(studentService.getAllStudents());
    }

    private void showCategories() {
        adminView.printCategories(studyCategoryService.getAllCategories());
    }

    private void showRequestStatus() {
        adminView.printRequestStatus(studyRequestService.getAllRequestStatus());
    }

    private void runMatching() {
        if (!adminView.confirmMatching()) {
            adminView.printMessage("자동 매칭을 취소했습니다.");
            return;
        }

        if (matchingService.runMatching()) {
            adminView.printMessage("자동 매칭이 완료되었습니다.");
            return;
        }

        adminView.printMessage("자동 매칭에 실패했습니다. 매칭 가능한 신청자가 없거나 조 편성이 불가능합니다.");
    }

    private void showGroupResults() {
        adminView.printGroupResults(matchingService.getAllGroupResults());
        adminView.printUnmatchedRequests(studyRequestService.getAllRequestStatus());
    }

    private void resetMatching() {
        if (!adminView.confirmReset()) {
            adminView.printMessage("매칭 결과 초기화를 취소했습니다.");
            return;
        }

        if (matchingService.resetMatching()) {
            adminView.printMessage("매칭 결과가 초기화되었습니다.");
            return;
        }

        adminView.printMessage("매칭 결과 초기화에 실패했습니다.");
    }
}
