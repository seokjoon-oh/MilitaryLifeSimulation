/*김태우.png 출처: https://imgnews.pstatic.net/image/108/2008/12/09/2008120914523202415_1.jpg?type=w647
최종훈.png 출처 : https://postfiles.pstatic.net/MjAxNzEyMDFfMTE1/MDAxNTEyMTE0NjgzNDQ2.rZPwXK59F8KZtjFLkz6zWojPZg5RZBgQBEKOSkbO7cgg.DDtQG_r67uuMD22yyd_nvrASMD1B9Tr-EXl18KhsHvog.JPEG.sjw608/%EB%A7%90%EB%85%84%EB%B3%91%EC%9E%A5.jpg?type=w966
굳건이.png 출처 : https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Military_Manpower_Administration_Gutgeoni1.png/200px-Military_Manpower_Administration_Gutgeoni1.png
나백중.png 출처 : https://img.insight.co.kr/static/2019/02/13/700/97300lv5mvgz35ydca9u.jpg
군.png 출처 : https://i.namu.wiki/i/8ZvD7obEx8NqEZMfmJPFTqinmCY2-c-v7P409tLGvPbG0HSD9rx-AAEf0eBxB8vohWyHwR4nU5GrqaKjNZnsPQ.webp*/

//updateSoldierStates 메소드 - 훈련 점수 관리 추가: 병사의 훈련 점수가 10 이상일 경우 휴가 1일을 추가하고 만족도를 증가
//startGameLoop 메소드 - JPanel 추가 - 각 병사의 이미지를 포함한 개별 패널을 만들어 병사별 상태를 보여줍니다.
//startGameLoop 메소드 - 병사 상태 업데이트를 별도의 스레드(Thread)로 실행(병사 상태를 JPanel에 동기화)
//triggerRandomEvent 메소드 - 이벤트가 병사의 훈련 점수와 만족도에 각각 다른 영향을 미치도록 함
//synchronized 를 이용하여 다른 메뉴들과 동기화 (아직 잘안된것같다)
//뭔가 상황을 더 자세하게 주면 좋을 것 같다.


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class MilitaryLifeSimulation_3 {

    static class StartButtonActionListener implements ActionListener {
        private JFrame frame;

        public StartButtonActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            createShowGUI(frame);
        }
    }

    static class ScheduleActionListener implements ActionListener {
        private JFrame frame;

        public ScheduleActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manageSchedule(frame);
        }
    }

    static class SatisfactionActionListener implements ActionListener {
        private JFrame frame;

        public SatisfactionActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showSatisfactionStatus(frame);
        }
    }

    static class LeaveActionListener implements ActionListener {
        private JFrame frame;

        public LeaveActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manageLeave(frame);
        }
    }

    static class RandomEventActionListener implements ActionListener {
        private JFrame frame;

        public RandomEventActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            triggerRandomEvent(frame);
        }
    }

    static class ExitActionListener implements ActionListener {
        private JFrame frame;

        public ExitActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exitApplication(frame);
        }
    }

    private static HashMap<String, Integer> satisfactionMap = new HashMap<>(); // 병사 만족도
    private static HashMap<String, Integer> leaveMap = new HashMap<>(); // 휴가 일수
    private static HashMap<String, Integer> taskPointsMap = new HashMap<>(); // 훈련 점수
    private static HashMap<String, String> rankMap = new HashMap<>(); // 계급 정보
    private static String[] soldierNames = { "최종훈", "김태우", "굳건이", "나백중" };
    private static String[] soldierRanks = { "병장", "상병", "일병", "이병" };
    private static String[] soldierImages = { "src/images/최종훈.png", "src/images/김태우.png", "src/images/굳건이.png", "src/images/나백중.png" };

    public static void main(String[] args) {
        // 초기 데이터 설정
        for (int i = 0; i < soldierNames.length; i++) {
            satisfactionMap.put(soldierNames[i], 100); // 기본 만족도 100
            leaveMap.put(soldierNames[i], 10); // 기본 휴가 10일
            taskPointsMap.put(soldierNames[i], 0); // 기본 훈련 점수 0
            rankMap.put(soldierNames[i], soldierRanks[i]);
        }

        // 시작 화면 실행
        createStartScreen();
    }

    private static void createStartScreen() {
        JFrame frame = new JFrame("Korea Army Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        try {
            // 업로드된 배경 이미지 사용
            ImageIcon backgroundImage = new ImageIcon("src/images/군.png");
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundLabel);
        } catch (Exception ex) {
            System.out.println("배경 이미지를 로드할 수 없습니다.");
        }

        JButton startButton = new JButton("시작하기");
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);

        startButton.addActionListener(new StartButtonActionListener(frame));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // 배경 투명 처리
        buttonPanel.add(startButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void createShowGUI(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        JMenuBar menuBar = new JMenuBar();

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
                startGameLoop(frame);
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
    //synchronized 동기화
    private static synchronized void updateSoldierStates(JTextArea statusArea) {
        StringBuilder status = new StringBuilder(); //StringBuilder 참고 출처 : https://onlyfor-me-blog.tistory.com/317
        for (String name : soldierNames) {
            int satisfaction = satisfactionMap.get(name);
            int points = taskPointsMap.get(name);

            // 훈련 점수가 10 이상이면 휴가 1일 추가 및 만족도 증가
            if (points >= 10) {
                points -= 10;
                taskPointsMap.put(name, points);

                int leave = leaveMap.get(name) + 1;
                leaveMap.put(name, leave);

                satisfaction += 5;
                satisfactionMap.put(name, satisfaction);
            }

            satisfaction = Math.max(0, satisfaction - 1); // 매일 만족도 감소
            satisfactionMap.put(name, satisfaction);

            status.append(name).append(" - 만족도: ").append(satisfaction).append(", 훈련 점수: ").append(points).append(", 휴가: ").append(leaveMap.get(name)).append("\n");
        }
        statusArea.setText(status.toString());
    }
    //synchronized 동기화
    private static synchronized void updateAllGUIStates() {
        System.out.println("GUI 상태가 업데이트되었습니다. 만족도 및 휴가 정보가 동기화됩니다.");
    }

    private static void startGameLoop(JFrame frame) {
        JPanel gamePanel = new JPanel(new BorderLayout());
        JLabel dayLabel = new JLabel("Day: 1", SwingConstants.CENTER); 
        dayLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        gamePanel.add(dayLabel, BorderLayout.NORTH);

        JPanel soldierPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        for (int i = 0; i < soldierNames.length; i++) {
            JPanel individualPanel = new JPanel(new BorderLayout());
            JLabel imageLabel = new JLabel();
            ImageIcon icon = new ImageIcon(soldierImages[i]);
            Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));

            JLabel nameLabel = new JLabel(soldierNames[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

            JLabel statsLabel = new JLabel("만족도: " + satisfactionMap.get(soldierNames[i]) +" | 훈련 점수: " + taskPointsMap.get(soldierNames[i]) + " | 휴가: " + leaveMap.get(soldierNames[i]),SwingConstants.CENTER);
            statsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

            individualPanel.add(imageLabel, BorderLayout.CENTER);
            individualPanel.add(nameLabel, BorderLayout.NORTH);
            individualPanel.add(statsLabel, BorderLayout.SOUTH);

            soldierPanel.add(individualPanel);
        }

        gamePanel.add(soldierPanel, BorderLayout.CENTER);

        JButton nextDayButton = new JButton("다음 날 진행");
        JButton backToMenuButton = new JButton("메인 메뉴로");
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
                dayLabel.setText("Day: " + day[0]);

                Thread updateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateSoldierStates(new JTextArea());
                        updateAllGUIStates();

                        for (Component comp : soldierPanel.getComponents()) {
                            if (comp instanceof JPanel) {
                                JPanel panel = (JPanel) comp;
                                JLabel statsLabel = (JLabel) panel.getComponent(2);
                                int index = soldierPanel.getComponentZOrder(panel);
                                statsLabel.setText("만족도: " + satisfactionMap.get(soldierNames[index]) + " | 훈련 점수: " + taskPointsMap.get(soldierNames[index]) + " | 휴가: " + leaveMap.get(soldierNames[index]));
                            }
                        }
                    }
                });
                updateThread.start();

                if (day[0] % 7 == 0) {
                    triggerRandomEvent(frame);
                }
                if (day[0] > 30) {
                    showGameResults(frame);
                }
            }
        });

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createShowGUI(frame);
            }
        });
    }

    private static void manageSchedule(JFrame frame) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel comboBoxLabel = new JLabel("병사를 선택하세요: ");
        JComboBox<String> soldierComboBox = new JComboBox<>(soldierNames);
        JLabel soldierImageLabel = new JLabel();

        soldierComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = soldierComboBox.getSelectedIndex();
                if (selectedIndex >= 0) {
                    ImageIcon icon = new ImageIcon(soldierImages[selectedIndex]);
                    Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    soldierImageLabel.setIcon(new ImageIcon(scaledImage));
                }
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(comboBoxLabel);
        topPanel.add(soldierComboBox);
        topPanel.add(soldierImageLabel);

        JButton assignButton = new JButton("스케줄 할당");
        JButton backButton = new JButton("뒤로가기");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(assignButton);
        buttonPanel.add(backButton);

        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createShowGUI(frame);
            }
        });
        
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSoldier = (String) soldierComboBox.getSelectedItem();
                if (selectedSoldier != null) {
                    assignTask(selectedSoldier);
                }
            }
        });
    }

    private static void assignTask(String soldier) {
        String[] options = { "훈련", "근무", "휴가" };
        String task = (String) JOptionPane.showInputDialog(null,"스케줄을 선택해주세요.","스케줄 선택",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);

        if (task != null) {
            int satisfaction = satisfactionMap.get(soldier);
            int points = taskPointsMap.get(soldier);
            int leave = leaveMap.get(soldier);

            switch (task) {
                case "훈련":
                    satisfaction -= 5;
                    points += 10;
                    JOptionPane.showMessageDialog(null, soldier + " 스케줄이 할당되었습니다: " + task, "스케줄 할당 결과", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "근무":
                    satisfaction -= 2;
                    JOptionPane.showMessageDialog(null, soldier + " 근무 할당 완료.", "근무 할당 결과", JOptionPane.INFORMATION_MESSAGE); // JOptionPane 참고 출처: p789 ~791
                    break;
                case "휴가":
                    if (leave > 0) {
                        leave -= 1;
                        satisfaction += 10;
                        JOptionPane.showMessageDialog(null, soldier + " 휴가 사용. 만족도 +10, 남은 휴가: " + leave, "휴가 사용 결과", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, soldier + "는 휴가가 부족합니다.", "휴가 부족 경고", JOptionPane.WARNING_MESSAGE);
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
        String event = events[rand.nextInt(events.length)];

        JOptionPane.showMessageDialog(frame, "특별 이벤트: " + event + " 발생!", "이벤트 발생", JOptionPane.INFORMATION_MESSAGE);

        synchronized (satisfactionMap) {
            for (String name : satisfactionMap.keySet()) {
                int satisfaction = satisfactionMap.get(name);
                int points = taskPointsMap.get(name);
                switch (event) {
                    case "야간 경계":
                        satisfaction -= 5;
                        points += 5;
                        break;
                    case "체력 단련":
                        satisfaction += 5;
                        break;
                    case "전술 훈련":
                        satisfaction += 10;
                        points += 10;
                        break;
                }
                satisfactionMap.put(name, satisfaction);
                taskPointsMap.put(name, points);
            }
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