package controller;

import dto.StudyRequestDTO;
import service.MatchingService;
import service.StudyCategoryService;
import service.StudyRequestService;
import util.Session;
import view.StudentView;

public class StudentController {
    private final StudentView studentView;
    private final StudyCategoryService studyCategoryService;
    private final StudyRequestService studyRequestService;
    private final MatchingService matchingService;

    public StudentController() {
        this.studentView = new StudentView();
        this.studyCategoryService = new StudyCategoryService();
        this.studyRequestService = new StudyRequestService();
        this.matchingService = new MatchingService();
    }

    public void run() {
        if (!Session.isLogin() || Session.loginUser == null) {
            studentView.printMessage("로그인이 필요합니다.");
            return;
        }

        boolean running = true;

        while (running) {
            int menu = studentView.showStudentMenu();

            switch (menu) {
            case 1:
                showCategories();
                break;
            case 2:
                createRequest();
                break;
            case 3:
                showMyRequest();
                break;
            case 4:
                updateRequest();
                break;
            case 5:
                cancelRequest();
                break;
            case 6:
                showMyGroup();
                break;
            case 7:
                studentView.printMessage("로그아웃합니다.");
                running = false;
                break;
            case 0:
                studentView.printMessage("프로그램을 종료합니다.");
                System.exit(0);
                break;
            default:
                studentView.printMessage("잘못된 메뉴입니다.");
                break;
            }
        }
    }

    private void showCategories() {
        studentView.printCategories(studyCategoryService.getAllCategories());
    }

    private void createRequest() {
        int studentId = Session.loginUser.getStudentId();
        StudyRequestDTO currentRequest = studyRequestService.getMyRequest(studentId);

        if (currentRequest != null) {
            studentView.printMessage("이미 신청 정보가 존재합니다. 수정 메뉴를 이용해주세요.");
            return;
        }

        showCategories();

        int firstId = studentView.inputFirstCategory();
        Integer secondId = studentView.inputSecondCategory();
        Integer thirdId = studentView.inputThirdCategory();

        if (studyRequestService.createRequest(studentId, firstId, secondId, thirdId)) {
            studentView.printMessage("스터디 신청이 완료되었습니다.");
            return;
        }

        studentView.printMessage("스터디 신청에 실패했습니다. 입력값, 중복 신청, 카테고리 중복 여부를 확인해주세요.");
    }

    private void showMyRequest() {
        int studentId = Session.loginUser.getStudentId();
        studentView.printMyRequest(studyRequestService.getMyRequest(studentId));
    }

    private void updateRequest() {
        int studentId = Session.loginUser.getStudentId();
        StudyRequestDTO currentRequest = studyRequestService.getMyRequest(studentId);

        if (currentRequest == null) {
            studentView.printMessage("신청 정보가 없습니다. 먼저 희망 스터디 신청을 해주세요.");
            return;
        }
        if ("Y".equalsIgnoreCase(currentRequest.getMatchedYn())) {
            studentView.printMessage("매칭이 완료되어 신청 정보를 수정할 수 없습니다.");
            return;
        }

        showCategories();

        int firstId = studentView.inputFirstCategory();
        Integer secondId = studentView.inputSecondCategory();
        Integer thirdId = studentView.inputThirdCategory();

        if (studyRequestService.updateRequest(studentId, firstId, secondId, thirdId)) {
            studentView.printMessage("신청 정보가 수정되었습니다.");
            return;
        }

        studentView.printMessage("신청 정보 수정에 실패했습니다. 신청 정보, 매칭 여부, 입력값을 확인해주세요.");
    }

    private void cancelRequest() {
        int studentId = Session.loginUser.getStudentId();

        if (studyRequestService.cancelRequest(studentId)) {
            studentView.printMessage("신청이 취소되었습니다.");
            return;
        }

        studentView.printMessage("신청 취소에 실패했습니다. 신청 정보 또는 매칭 여부를 확인해주세요.");
    }

    private void showMyGroup() {
        int studentId = Session.loginUser.getStudentId();
        studentView.printMyGroup(matchingService.getMyGroup(studentId));
    }
}
