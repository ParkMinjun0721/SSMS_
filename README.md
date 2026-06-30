# SSMS_

## 한국어

SSMS_는 Java 콘솔 기반 스터디 매칭 관리 시스템입니다. 학생은 스터디를 신청하고 신청 현황을 확인할 수 있으며, 관리자는 카테고리와 신청 데이터를 기반으로 스터디 그룹을 관리할 수 있습니다.

### 주요 기능

- 학생 회원가입 및 로그인
- 스터디 카테고리 조회
- 스터디 신청 등록 및 신청 현황 확인
- 관리자용 스터디 신청/그룹 관리
- 신청 데이터를 기반으로 한 스터디 매칭 처리
- Oracle Database 기반 데이터 저장

### 프로젝트 구조

```text
src/
  controller/   사용자 입력 흐름과 화면 전환 제어
  dao/          데이터베이스 접근 로직
  dto/          데이터 전달 객체
  main/         애플리케이션 진입점
  service/      비즈니스 로직
  util/         세션 및 DB 연결 유틸리티
  view/         콘솔 화면 출력과 입력 처리
```

### 실행 환경

- Java
- Eclipse IDE 또는 Java 프로젝트 실행이 가능한 IDE
- Oracle JDBC Driver
- Oracle Database

### 실행 방법

1. 프로젝트를 Eclipse에 import합니다.
2. Oracle JDBC Driver를 빌드 경로에 추가합니다.
3. 데이터베이스 연결 정보가 현재 환경에 맞는지 확인합니다.
4. `src/main/Main.java`의 `main` 메서드를 실행합니다.

### 참고

- 컴파일 산출물인 `bin/`과 `.class` 파일은 Git에서 제외됩니다.
- 로컬 환경의 민감한 설정 파일은 `.env`로 관리하고 Git에 커밋하지 않는 것을 권장합니다.

---

## English

SSMS_ is a Java console-based study matching management system. Students can submit study requests and check their request status, while administrators can manage study categories, requests, and matched study groups.

### Features

- Student sign-up and login
- Study category lookup
- Study request registration and status checking
- Admin management for study requests and groups
- Study matching based on request data
- Oracle Database-backed persistence

### Project Structure

```text
src/
  controller/   Controls user flow and screen transitions
  dao/          Database access logic
  dto/          Data transfer objects
  main/         Application entry point
  service/      Business logic
  util/         Session and database connection utilities
  view/         Console output and input handling
```

### Requirements

- Java
- Eclipse IDE or another IDE that can run Java projects
- Oracle JDBC Driver
- Oracle Database

### How to Run

1. Import the project into Eclipse.
2. Add the Oracle JDBC Driver to the build path.
3. Check that the database connection settings match your environment.
4. Run the `main` method in `src/main/Main.java`.

### Notes

- Build outputs such as `bin/` and `.class` files are ignored by Git.
- Keep sensitive local configuration in `.env` files and do not commit them to Git.
