package view;

import java.util.Scanner;

import dto.StudentDTO;

public class MainView {
    private final Scanner sc;

    public MainView() {
        this.sc = new Scanner(System.in);
    }

    public int showMainMenu() {
        System.out.println();
        System.out.println("========== ShinStudy Match ==========");
        System.out.println("1. 회원가입");
        System.out.println("2. 로그인");
        System.out.println("3. 스터디 카테고리 조회");
        System.out.println("0. 종료");
        System.out.print("선택 >> ");

        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해주세요.");
            return 0;
        }
    }

    public StudentDTO inputRegisterInfo() {
        try {
            StudentDTO student = new StudentDTO();

            System.out.println();
            System.out.println("========== 회원가입 ==========");
            System.out.print("아이디 >> ");
            student.setLoginId(sc.nextLine().trim());

            System.out.print("비밀번호 >> ");
            student.setPassword(sc.nextLine().trim());

            System.out.print("이름 >> ");
            student.setName(sc.nextLine().trim());

            System.out.print("이메일 >> ");
            student.setEmail(sc.nextLine().trim());

            student.setCohort(7);

            return student;
        } catch (Exception e) {
            System.out.println("회원가입 정보를 입력하는 중 오류가 발생했습니다.");
            return null;
        }
    }

    public String inputLoginId() {
        try {
            System.out.print("아이디 >> ");
            return sc.nextLine().trim();
        } catch (Exception e) {
            System.out.println("아이디 입력 중 오류가 발생했습니다.");
            return "";
        }
    }

    public String inputPassword() {
        try {
            System.out.print("비밀번호 >> ");
            return sc.nextLine().trim();
        } catch (Exception e) {
            System.out.println("비밀번호 입력 중 오류가 발생했습니다.");
            return "";
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
