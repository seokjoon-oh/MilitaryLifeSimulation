/*김태우.png 출처: https://imgnews.pstatic.net/image/108/2008/12/09/2008120914523202415_1.jpg?type=w647
최종훈.png 출처 : https://postfiles.pstatic.net/MjAxNzEyMDFfMTE1/MDAxNTEyMTE0NjgzNDQ2.rZPwXK59F8KZtjFLkz6zWojPZg5RZBgQBEKOSkbO7cgg.DDtQG_r67uuMD22yyd_nvrASMD1B9Tr-EXl18KhsHvog.JPEG.sjw608/%EB%A7%90%EB%85%84%EB%B3%91%EC%9E%A5.jpg?type=w966
굳건이.png 출처 : https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Military_Manpower_Administration_Gutgeoni1.png/200px-Military_Manpower_Administration_Gutgeoni1.png
나백중.png 출처 : https://img.insight.co.kr/static/2019/02/13/700/97300lv5mvgz35ydca9u.jpg
군.png 출처 : https://i.namu.wiki/i/8ZvD7obEx8NqEZMfmJPFTqinmCY2-c-v7P409tLGvPbG0HSD9rx-AAEf0eBxB8vohWyHwR4nU5GrqaKjNZnsPQ.webp*/

//JMenuBar 메뉴 추가 - 메뉴를 통해 게임 시작, 병사 관리, 이벤트 확인, 종료 등 기능에 접근
//startGameLoop 메소드 추가 -  게임이 실제로 진행되는 루프를 구현, 날짜를 업데이트하며, 병사 상태를 관리하고 랜덤 이벤트를 발생시킬 수 있는 기능이 포함
//랜덤 이벤트는 새 코드에서 주기적으로 발생하도록 변경(7일마다) 
//updateSoldierStates 메소드 추가 - 병사의 상태(만족도, 훈련 점수 등)를 자동으로 업데이트하는 기능이 추가
//showGameResults 메소드 추가 - 게임 종료 후 병사의 상태 표시
//훈련 점수는 아직  수정중


// 만족도에 따른 보상 제공 구상중

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class MilitaryLifeSimulation_2 {

    // 시작 버튼 클릭 이벤트 처리 클래스
    static class StartButtonActionListener implements ActionListener {
        private JFrame frame;

        public StartButtonActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            createShowGUI(frame); // 메인 메뉴 화면 생성
        }
    }

    // 병사 스케줄 관리 버튼 클릭 이벤트 처리 클래스
    static class ScheduleActionListener implements ActionListener {
        private JFrame frame;

        public ScheduleActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manageSchedule(frame); // 병사 스케줄 관리 화면 생성
        }
    }

    // 병사 만족도 확인 버튼 클릭 이벤트 처리 클래스
    static class SatisfactionActionListener implements ActionListener {
        private JFrame frame;

        public SatisfactionActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showSatisfactionStatus(frame); // 병사 만족도 확인
        }
    }

    // 병사 휴가 관리 버튼 클릭 이벤트 처리 클래스
    static class LeaveActionListener implements ActionListener {
        private JFrame frame;

        public LeaveActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manageLeave(frame); // 병사 휴가 상태 확인
        }
    }

    // 랜덤 이벤트 발생 버튼 클릭 이벤트 처리 클래스
    static class RandomEventActionListener implements ActionListener {
        private JFrame frame;

        public RandomEventActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            triggerRandomEvent(frame); // 랜덤 이벤트 발생
        }
    }

    // 종료 버튼 클릭 이벤트 처리 클래스
    static class ExitActionListener implements ActionListener {
        private JFrame frame;

        public ExitActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exitApplication(frame); // 프로그램 종료
        }
    }

    // 병사 상태를 저장하는 맵
    private static HashMap<String, Integer> satisfactionMap = new HashMap<>(); // 병사 만족도
    private static HashMap<String, Integer> leaveMap = new HashMap<>(); // 병사 휴가 일수
    private static HashMap<String, Integer> taskPointsMap = new HashMap<>(); // 병사 훈련 점수
    private static HashMap<String, String> rankMap = new HashMap<>(); // 병사 계급 정보

    // 병사 초기 데이터
    private static String[] soldierNames = { "최종훈", "김태우", "굳건이", "나백중" };
    private static String[] soldierRanks = { "병장", "상병", "일병", "이병" };
    private static String[] soldierImages = { "src/images/최종훈.png", "src/images/김태우.png", "src/images/굳건이.png", "src/images/나백중.png" };

    public static void main(String[] args) {
        // 병사 초기 데이터 설정
        for (int i = 0; i < soldierNames.length; i++) {
            satisfactionMap.put(soldierNames[i], 100); // 기본 만족도 100
            leaveMap.put(soldierNames[i], 10); // 기본 휴가 10일
            taskPointsMap.put(soldierNames[i], 0); // 기본 훈련 점수 0
            rankMap.put(soldierNames[i], soldierRanks[i]); // 병사 계급 설정
        }

        // 시작 화면 생성
        createStartScreen();
    }

    // 시작 화면 생성 메서드
    private static void createStartScreen() {
        JFrame frame = new JFrame("Korea Army Simulator"); // 프로그램 제목 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 동작 설정
        frame.setSize(800, 600); // 창 크기 설정

        try {
            // 배경 이미지 로드
            ImageIcon backgroundImage = new ImageIcon("src/images/군.png");
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundLabel);
        } catch (Exception ex) {
            System.out.println("배경 이미지를 로드할 수 없습니다.");
        }

        JButton startButton = new JButton("시작하기"); // 시작 버튼 생성
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 버튼 글꼴 설정
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);

        startButton.addActionListener(new StartButtonActionListener(frame)); // 버튼 클릭 이벤트 설정

        JPanel buttonPanel = new JPanel(); // 버튼 패널 생성
        buttonPanel.setOpaque(false); // 패널 배경 투명 설정
        buttonPanel.add(startButton); // 버튼 추가

        frame.add(buttonPanel, BorderLayout.SOUTH); // 패널을 창 하단에 추가
        frame.setVisible(true); // 창 표시
    }

    // 메인 메뉴 화면 생성 메서드
    private static void createShowGUI(JFrame frame) {
        frame.getContentPane().removeAll(); // 기존 화면 삭제
        frame.revalidate();
        frame.repaint();

        JMenuBar menuBar = new JMenuBar();// 메뉴바 생성 참조 : p 765

        // 메뉴 생성
        JMenu menu = new JMenu("메뉴");
        JMenuItem startGameMenuItem = new JMenuItem("게임 시작");
        JMenuItem scheduleMenuItem = new JMenuItem("병사 스케줄 관리");
        JMenuItem satisfactionMenuItem = new JMenuItem("병사 만족도 확인");
        JMenuItem leaveMenuItem = new JMenuItem("휴가 관리");
        JMenuItem randomEventMenuItem = new JMenuItem("이벤트 발생");
        JMenuItem exitMenuItem = new JMenuItem("종료");

        // 메뉴 항목 추가
        menu.add(startGameMenuItem);
        menu.add(scheduleMenuItem);
        menu.add(satisfactionMenuItem);
        menu.add(leaveMenuItem);
        menu.add(randomEventMenuItem);
        menu.addSeparator();
        menu.add(exitMenuItem);

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // 이벤트 연결
        startGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGameLoop(frame); // 게임 루프 시작
            }
        });
        scheduleMenuItem.addActionListener(new ScheduleActionListener(frame));
        satisfactionMenuItem.addActionListener(new SatisfactionActionListener(frame));
        leaveMenuItem.addActionListener(new LeaveActionListener(frame));
        randomEventMenuItem.addActionListener(new RandomEventActionListener(frame));
        exitMenuItem.addActionListener(new ExitActionListener(frame));

        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("메인 메뉴", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    // 게임 루프 화면 생성 및 진행 메서드
    private static void startGameLoop(JFrame frame) {
        JPanel gamePanel = new JPanel(new BorderLayout());
        JLabel dayLabel = new JLabel("Day: 1", SwingConstants.CENTER); // 날짜 표시 라벨
        dayLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        gamePanel.add(dayLabel, BorderLayout.NORTH);

        JTextArea statusArea = new JTextArea(); // 병사 상태 표시 영역
        statusArea.setEditable(false); //setEditable 참조 출처: https://yoo11052.tistory.com/17
        JScrollPane scrollPane = new JScrollPane(statusArea); //JScrollPane 참조 출처: https://blog.naver.com/sks6624/150165616213
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        JButton nextDayButton = new JButton("다음 날 진행"); // 다음 날 버튼
        JButton backToMenuButton = new JButton("메인 메뉴로"); // 메인 메뉴 버튼
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(nextDayButton);
        buttonPanel.add(backToMenuButton);
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();

        final int[] day = {1};
        nextDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                day[0]++;
                dayLabel.setText("Day: " + day[0]); // 날짜 업데이트
                updateSoldierStates(statusArea); // 병사 상태 업데이트
                updateAllGUIStates(); // 기타 상태 동기화
                if (day[0] % 7 == 0) {
                    triggerRandomEvent(frame); // 7일마다 랜덤 이벤트 발생
                }
                if (day[0] > 30) {
                    showGameResults(frame); // 30일 후 게임 종료 및 결과 표시
                }
            }
        });

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createShowGUI(frame); // 메인 메뉴로 돌아가기
            }
        });
    }

    // 병사 상태 업데이트 메서드
    private static void updateSoldierStates(JTextArea statusArea) {
        StringBuilder status = new StringBuilder(); //StringBuilder 참고 출처 : https://onlyfor-me-blog.tistory.com/317
        for (String name : soldierNames) {
            int satisfaction = satisfactionMap.get(name);
            satisfaction = Math.max(0, satisfaction - 1); // 만족도 감소 처리
            satisfactionMap.put(name, satisfaction);

            status.append(name).append(" - 만족도: ").append(satisfaction).append(", 훈련 점수: ").append(taskPointsMap.get(name)).append("\n");
        }
        statusArea.setText(status.toString()); // 상태 텍스트 영역 업데이트
    }

    // GUI 상태 동기화 메서드
    private static void updateAllGUIStates() {
        System.out.println("GUI 상태가 업데이트되었습니다. 만족도 및 휴가 정보가 동기화됩니다.");
    }

    // 병사 스케줄 관리 메서드 (추후 확장 가능)
    private static void manageSchedule(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "스케줄 관리 기능입니다.", "스케줄 관리", JOptionPane.INFORMATION_MESSAGE); // JOptionPane 참고 출처: p789 ~791
    }

    // 병사 만족도 확인 메서드
    private static void showSatisfactionStatus(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 만족도 현황:\n");
        for (String name : satisfactionMap.keySet()) {
            status.append(name).append(" - 만족도: ").append(satisfactionMap.get(name)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, status.toString(), "만족도 확인", JOptionPane.INFORMATION_MESSAGE);
    }

    // 병사 휴가 관리 메서드
    private static void manageLeave(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 휴가 현황:\n");
        for (String name : leaveMap.keySet()) {
            status.append(name).append(" - 남은 휴가: ").append(leaveMap.get(name)).append("일\n");
        }
        JOptionPane.showMessageDialog(frame, status.toString(), "휴가 관리", JOptionPane.INFORMATION_MESSAGE);
    }

    // 랜덤 이벤트 발생 메서드
    private static void triggerRandomEvent(JFrame frame) {
        String[] events = { "야간 경계", "체력 단련", "전술 훈련" };
        Random rand = new Random();
        String event = events[rand.nextInt(events.length)]; // 무작위 이벤트 선택

        JOptionPane.showMessageDialog(frame, "특별 이벤트: " + event + " 발생!", "이벤트 발생", JOptionPane.INFORMATION_MESSAGE);

        for (String name : satisfactionMap.keySet()) {
            int satisfaction = satisfactionMap.get(name);
            switch (event) {
                case "야간 경계":
                    satisfaction -= 5; // 만족도 감소
                    break;
                case "체력 단련":
                    satisfaction += 5; // 만족도 증가
                    break;
                case "전술 훈련":
                    satisfaction += 10; // 만족도 증가
                    break;
            }
            satisfactionMap.put(name, satisfaction);
        }
    }

    // 게임 결과 표시 메서드
    private static void showGameResults(JFrame frame) {
        StringBuilder result = new StringBuilder("게임 종료! 최종 결과:\n\n");
        for (String name : soldierNames) {
            result.append(name).append(" - 만족도: ").append(satisfactionMap.get(name)).append(", 훈련 점수: ").append(taskPointsMap.get(name)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, result.toString(), "게임 결과", JOptionPane.INFORMATION_MESSAGE);
        createShowGUI(frame); // 메인 메뉴로 돌아가기
    }

    // 프로그램 종료 메서드
    private static void exitApplication(JFrame frame) {
        JOptionPane.showMessageDialog(null, "프로그램이 종료됩니다.", "종료", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // 프로그램 종료
    }
}
