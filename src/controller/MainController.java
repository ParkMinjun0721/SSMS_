package controller;

import dto.StudentDTO;
import service.StudentService;
import service.StudyCategoryService;
import util.Session;
import view.MainView;
import view.StudentView;

public class MainController {
    private final MainView mainView;
    private final StudentView studentView;
    private final StudentService studentService;
    private final StudyCategoryService studyCategoryService;
    private final StudentController studentController;
    private final AdminController adminController;

    public MainController() {
        this.mainView = new MainView();
        this.studentView = new StudentView();
        this.studentService = new StudentService();
        this.studyCategoryService = new StudyCategoryService();
        this.studentController = new StudentController();
        this.adminController = new AdminController();
    }

    public void run() {
        boolean running = true;

        while (running) {
            int menu = mainView.showMainMenu();

            switch (menu) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                studentView.printCategories(studyCategoryService.getAllCategories());
                break;
            case 0:
                mainView.printMessage("프로그램을 종료합니다.");
                running = false;
                break;
            default:
                mainView.printMessage("잘못된 메뉴입니다.");
                break;
            }
        }
    }

    private void register() {
        StudentDTO student = mainView.inputRegisterInfo();

        if (studentService.register(student)) {
            mainView.printMessage("회원가입이 완료되었습니다.");
            return;
        }

        mainView.printMessage("회원가입에 실패했습니다. 입력값 또는 중복 아이디/이메일을 확인해주세요.");
    }

    private void login() {
        String loginId = mainView.inputLoginId();
        String password = mainView.inputPassword();
        StudentDTO loginUser = studentService.login(loginId, password);

        if (loginUser == null) {
            mainView.printMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
            return;
        }

        Session.login(loginUser);
        mainView.printMessage(loginUser.getName() + "님 로그인되었습니다.");

        if ("ADMIN".equalsIgnoreCase(loginUser.getRole())) {
            adminController.run();
        } else if ("USER".equalsIgnoreCase(loginUser.getRole())) {
            studentController.run();
        } else {
            mainView.printMessage("알 수 없는 권한입니다.");
        }

        Session.logout();
    }
}
