/*김태우.png 출처: https://imgnews.pstatic.net/image/108/2008/12/09/2008120914523202415_1.jpg?type=w647
최종훈.png 출처 : https://postfiles.pstatic.net/MjAxNzEyMDFfMTE1/MDAxNTEyMTE0NjgzNDQ2.rZPwXK59F8KZtjFLkz6zWojPZg5RZBgQBEKOSkbO7cgg.DDtQG_r67uuMD22yyd_nvrASMD1B9Tr-EXl18KhsHvog.JPEG.sjw608/%EB%A7%90%EB%85%84%EB%B3%91%EC%9E%A5.jpg?type=w966
굳건이.png 출처 : https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Military_Manpower_Administration_Gutgeoni1.png/200px-Military_Manpower_Administration_Gutgeoni1.png
나백중.png 출처 : https://img.insight.co.kr/static/2019/02/13/700/97300lv5mvgz35ydca9u.jpg
군.png 출처 : https://i.namu.wiki/i/8ZvD7obEx8NqEZMfmJPFTqinmCY2-c-v7P409tLGvPbG0HSD9rx-AAEf0eBxB8vohWyHwR4nU5GrqaKjNZnsPQ.webp*/

//"병사 관리 시뮬레이션" 게임의 초기 버전(게임이라기보다는 관리 시스템)
//병사들의 스케줄, 만족도, 휴가 등을 관리하며 이벤트를 처리하고 병사들의 상태를 유지하도록 설계
//병사의 이름, 계급, 만족도, 휴가 일수 등을 HashMap으로 관리
//병사들의 만족도를 JOptionPane으로 표시
//triggerRandomEvent 메소드에서 무작위로 이벤트(야간 경계, 체력 단련, 전술 훈련)를 선택하여 병사들의 만족도를 변경
//특정 병사에게 훈련, 근무, 휴가를 할당하며, 만족도, 훈련 점수, 휴가를 조정(manageSchedule 메소드, assignTask 메소드)
//병사들의 남은 휴가 일수를 확인(manageLeave 메소드)
//스케줄이나 이벤트에 따라 만족도가 변경

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class MilitaryLifeSimulation_1 {

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

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10)); // 메뉴 버튼 배열을 위한 그리드 레이아웃 설정

        JButton scheduleButton = new JButton("1. 병사 스케줄 관리");
        JButton satisfactionButton = new JButton("2. 병사 만족도 확인");
        JButton leaveButton = new JButton("3. 휴가 관리");
        JButton randomEventButton = new JButton("4. 이벤트 발생");
        JButton exitButton = new JButton("5. 종료");

        // 버튼을 패널에 추가
        panel.add(scheduleButton);
        panel.add(satisfactionButton);
        panel.add(leaveButton);
        panel.add(randomEventButton);
        panel.add(exitButton);

        frame.add(panel); // 패널을 창에 추가
        frame.revalidate();
        frame.repaint();

        // 버튼 이벤트 리스너 설정
        scheduleButton.addActionListener(new ScheduleActionListener(frame));
        satisfactionButton.addActionListener(new SatisfactionActionListener(frame));
        leaveButton.addActionListener(new LeaveActionListener(frame));
        randomEventButton.addActionListener(new RandomEventActionListener(frame));
        exitButton.addActionListener(new ExitActionListener(frame));
    }

    // 병사 스케줄 관리 화면 생성 메서드
    private static void manageSchedule(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 병사 선택 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JLabel comboBoxLabel = new JLabel("병사를 선택하세요: "); // 병사 선택 안내 텍스트
        JComboBox<String> soldierComboBox = new JComboBox<>(soldierNames); // 병사 선택 드롭다운
        soldierComboBox.setPreferredSize(new Dimension(150, 30)); // 콤보박스 크기 설정 (setPreferredSize 참고 출처: https://blog.naver.com/kdy0573/150171645002)

        JLabel soldierImageLabel = new JLabel(); // 병사 이미지 표시 라벨
        soldierComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = soldierComboBox.getSelectedIndex(); // 선택된 병사 인덱스 가져오기 참고 p641
                if (selectedIndex >= 0) {
                    ImageIcon icon = new ImageIcon(soldierImages[selectedIndex]);
                    Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH); //getScaledInstancec 참고 출처: https://blog.naver.com/wonsukdream/30138796350
                    soldierImageLabel.setIcon(new ImageIcon(scaledImage)); // 이미지 설정
                }
            }
        });

        topPanel.add(comboBoxLabel);
        topPanel.add(soldierComboBox);
        topPanel.add(soldierImageLabel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton assignButton = new JButton("스케줄 할당");
        JButton backButton = new JButton("뒤로가기");

        assignButton.setPreferredSize(new Dimension(120, 40)); // 버튼 크기 설정
        backButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(assignButton);
        buttonPanel.add(backButton);

        // 중앙 패널로 병사 선택 패널과 버튼 패널 조합
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0)); // 여백 추가

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();

        // 버튼 클릭 이벤트 설정
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSoldier = (String) soldierComboBox.getSelectedItem();
                if (selectedSoldier != null) {
                    assignTask(selectedSoldier); // 병사 스케줄 할당
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createShowGUI(frame); // 메인 메뉴로 돌아가기
            }
        });
    }

    // 병사 스케줄 할당 메서드
    private static void assignTask(String soldier) {
        String[] options = { "훈련", "근무", "휴가" };
        String task = (String) JOptionPane.showInputDialog(null,"스케줄을 선택해주세요.","스케줄 선택",JOptionPane.PLAIN_MESSAGE,null,options,options[0]); 

        if (task != null) {
            int satisfaction = satisfactionMap.get(soldier);
            int points = taskPointsMap.get(soldier);
            int leave = leaveMap.get(soldier);

            switch (task) {
                case "훈련":
                    satisfaction -= 5; // 훈련 시 만족도 감소
                    points += 10; // 훈련 점수 증가
                    JOptionPane.showMessageDialog(null,soldier + " 스케줄이 할당되었습니다: " + task,"스케줄 할당 결과",JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "근무":
                    satisfaction -= 2; // 근무 시 만족도 감소
                    JOptionPane.showMessageDialog(null,soldier + " 근무 할당 완료.","근무 할당 결과",JOptionPane.INFORMATION_MESSAGE);// JOptionPane 참고 출처: p789 ~791
                    break;
                case "휴가":
                    if (leave > 0) {
                        leave -= 1; // 휴가 사용
                        satisfaction += 10; // 만족도 증가
                        JOptionPane.showMessageDialog(null,soldier + " 휴가 사용. 만족도 +10, 남은 휴가: " + leave,"휴가 사용 결과",JOptionPane.INFORMATION_MESSAGE );
                    } else {
                        JOptionPane.showMessageDialog(null,soldier + "는 휴가가 부족합니다.","휴가 부족 경고",JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }

            satisfactionMap.put(soldier, satisfaction);
            taskPointsMap.put(soldier, points);
            leaveMap.put(soldier, leave);
        }
    }

    // 병사 만족도 확인 메서드
    private static void showSatisfactionStatus(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 만족도 현황:\n");
        for (String name : satisfactionMap.keySet()) {
            status.append(name).append(" - 만족도: ").append(satisfactionMap.get(name)).append("\n");
        }
        JOptionPane.showMessageDialog(null,status.toString(),"만족도 현황",JOptionPane.INFORMATION_MESSAGE);

        for (String name : satisfactionMap.keySet()) {
            int satisfaction = satisfactionMap.get(name);
            satisfactionMap.put(name, satisfaction);
        }
    }

    // 병사 휴가 상태 확인 메서드
    private static void manageLeave(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 휴가 현황:\n");
        for (String name : leaveMap.keySet()) {
            status.append(name).append(" - 남은 휴가: ").append(leaveMap.get(name)).append("일\n");
        }
        JOptionPane.showMessageDialog(frame,status.toString(),"휴가 현황",JOptionPane.INFORMATION_MESSAGE);
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

    // 프로그램 종료 메서드
    private static void exitApplication(JFrame frame) {
        JOptionPane.showMessageDialog(null, "프로그램이 종료됩니다.", "종료", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // 프로그램 종료
    }
}
