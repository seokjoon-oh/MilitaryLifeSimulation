/*김태우.png 출처: https://imgnews.pstatic.net/image/108/2008/12/09/2008120914523202415_1.jpg?type=w647
최종훈.png 출처 : https://postfiles.pstatic.net/MjAxNzEyMDFfMTE1/MDAxNTEyMTE0NjgzNDQ2.rZPwXK59F8KZtjFLkz6zWojPZg5RZBgQBEKOSkbO7cgg.DDtQG_r67uuMD22yyd_nvrASMD1B9Tr-EXl18KhsHvog.JPEG.sjw608/%EB%A7%90%EB%85%84%EB%B3%91%EC%9E%A5.jpg?type=w966
굳건이.png 출처 : https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Military_Manpower_Administration_Gutgeoni1.png/200px-Military_Manpower_Administration_Gutgeoni1.png
나백중.png 출처 : https://img.insight.co.kr/static/2019/02/13/700/97300lv5mvgz35ydca9u.jpg
군.png 출처 : https://i.namu.wiki/i/8ZvD7obEx8NqEZMfmJPFTqinmCY2-c-v7P409tLGvPbG0HSD9rx-AAEf0eBxB8vohWyHwR4nU5GrqaKjNZnsPQ.webp*/

//playMusic 메서드가 추가 - 오디오 브금 사용 하지만 미완성( 아직 브금도 안넣음)
// 최종 결과에 추가 보상 및 통계 표시( 게임 종료 시 최종 만족도가 가장 높은 병사에게 추가 휴가 1일을 부여)
//가장 많은 휴가를 가진 병사를 통계로 표시
//updateSoldierStates 메서드를 개선하여 soldierPanel에서 병사 상태를 실시간으로 반영


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MilitaryLifeSimulation_5 {

    static class StartButtonActionListener implements ActionListener {
        private JFrame frame;

        public StartButtonActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            playMusic("src/audio/start_bgm.wav");
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
        startGameMenuItem.addActionListener(e -> startGameLoop(frame));
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

    private static void playMusic(String filePath) { // 참고 : p 809
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("음악을 재생할 수 없습니다: " + e.getMessage());
        }
    }

    private static void manageSchedule(JFrame frame) {
        StringBuilder scheduleMessage = new StringBuilder("병사 스케줄 관리:");//StringBuilder 참고 출처 : https://onlyfor-me-blog.tistory.com/317
        for (String name : soldierNames) {
            scheduleMessage.append("\n").append(name).append(" - 훈련 대기 중");
        }
        JOptionPane.showMessageDialog(frame, scheduleMessage.toString(), "스케줄 관리", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showSatisfactionStatus(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 만족도 현황:");
        for (String name : soldierNames) {
            status.append("\n").append(name).append(" - 만족도: ").append(satisfactionMap.get(name));
        }
        JOptionPane.showMessageDialog(frame, status.toString(), "만족도 확인", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void manageLeave(JFrame frame) {
        StringBuilder leaveStatus = new StringBuilder("병사 휴가 현황:");
        for (String name : soldierNames) {
            leaveStatus.append("\n").append(name).append(" - 남은 휴가: ").append(leaveMap.get(name));
        }
        JOptionPane.showMessageDialog(frame, leaveStatus.toString(), "휴가 관리", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void triggerRandomEvent(JFrame frame) {
        String[] events = {"야간 경계", "체력 단련", "전술 훈련"};
        Random random = new Random();
        String selectedEvent = events[random.nextInt(events.length)];

        for (String name : soldierNames) {
            int satisfaction = satisfactionMap.get(name);
            int points = taskPointsMap.get(name);
            switch (selectedEvent) {
                case "야간 경계":
                    satisfaction -= 5;
                    points += 3;
                    break;
                case "체력 단련":
                    satisfaction += 5;
                    break;
                case "전술 훈련":
                    satisfaction += 3;
                    points += 5;
                    break;
            }
            satisfactionMap.put(name, Math.max(satisfaction, 0));
            taskPointsMap.put(name, points);
        }

        JOptionPane.showMessageDialog(frame, "랜덤 이벤트 발생: " + selectedEvent, "이벤트 발생", JOptionPane.INFORMATION_MESSAGE);
    }

    private static synchronized void updateSoldierStates(JPanel soldierPanel) {
        for (int i = 0; i < soldierNames.length; i++) {
            int satisfaction = satisfactionMap.get(soldierNames[i]);
            int points = taskPointsMap.get(soldierNames[i]);

            // 훈련 점수 및 만족도 업데이트
            if (points >= 10) {
                points -= 10;
                taskPointsMap.put(soldierNames[i], points);

                int leave = leaveMap.get(soldierNames[i]) + 1;
                leaveMap.put(soldierNames[i], leave);

                satisfaction += 5;
                satisfactionMap.put(soldierNames[i], satisfaction);
            }

            satisfaction = Math.max(0, satisfaction - 1); // 매일 만족도 감소
            satisfactionMap.put(soldierNames[i], satisfaction);

            // soldierPanel에서 업데이트된 정보 반영
            JPanel individualPanel = (JPanel) soldierPanel.getComponent(i);
            JLabel statsLabel = (JLabel) individualPanel.getComponent(2); 
            statsLabel.setText("만족도: " + satisfaction +
                               " | 훈련 점수: " + points +
                               " | 휴가: " + leaveMap.get(soldierNames[i]));
        }
    }

    private static synchronized void updateAllGUIStates() {
        System.out.println("업데이트하여 만족도 및 휴가 정보가 동기화됩니다.");
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

            JLabel statsLabel = new JLabel("만족도: " + satisfactionMap.get(soldierNames[i]) +" | 훈련 점수: " + taskPointsMap.get(soldierNames[i]) + " | 휴가: " + leaveMap.get(soldierNames[i]), SwingConstants.CENTER);
            statsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

            JButton actionButton = new JButton(soldierNames[i] + " 훈련");
            int index = i;
            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int satisfaction = satisfactionMap.get(soldierNames[index]);
                    int points = taskPointsMap.get(soldierNames[index]);
                    satisfaction -= 3;
                    points += 5;
                    satisfactionMap.put(soldierNames[index], satisfaction);
                    taskPointsMap.put(soldierNames[index], points);
                    statsLabel.setText("만족도: " + satisfaction +" | 훈련 점수: " + points +" | 휴가: " + leaveMap.get(soldierNames[index]));
                }
            });

            individualPanel.add(imageLabel, BorderLayout.CENTER);
            individualPanel.add(nameLabel, BorderLayout.NORTH);
            individualPanel.add(statsLabel, BorderLayout.SOUTH);
            individualPanel.add(actionButton, BorderLayout.EAST);

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

                for (int i = 0; i < soldierNames.length; i++) {
                    String name = soldierNames[i];
                    int satisfaction = satisfactionMap.get(name);
                    int points = taskPointsMap.get(name);

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

                    JPanel individualPanel = (JPanel) soldierPanel.getComponent(i);
                    JLabel statsLabel = (JLabel) individualPanel.getComponent(2);
                    statsLabel.setText("만족도: " + satisfaction +
                                       " | 훈련 점수: " + points +
                                       " | 휴가: " + leaveMap.get(name));
                }

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

    private static void showGameResults(JFrame frame) {
        // 최종 결과 계산
        String highestSatisfactionSoldier = "없음";
        int maxSatisfaction = Integer.MIN_VALUE;

        // satisfactionMap 순회
        for (String key : satisfactionMap.keySet()) {
            int value = satisfactionMap.get(key);
            if (value > maxSatisfaction) {
                maxSatisfaction = value;
                highestSatisfactionSoldier = key;
            }
        }

        String highestLeaveSoldier = "없음";
        int maxLeave = Integer.MIN_VALUE;

        // leaveMap 순회
        for (String key : leaveMap.keySet()) {
            int value = leaveMap.get(key);
            if (value > maxLeave) {
                maxLeave = value;
                highestLeaveSoldier = key;
            }
        }

        leaveMap.put(highestSatisfactionSoldier, leaveMap.getOrDefault(highestSatisfactionSoldier, 0) + 1);

        String result = "게임 종료! 최종 결과:\n\n";
        for (int i = 0; i < soldierNames.length; i++) {
            String name = soldierNames[i];
            result += name + " - 만족도: " + satisfactionMap.get(name) + ", 훈련 점수: " + taskPointsMap.get(name) + ", 휴가: " + leaveMap.get(name) + "\n";
        }

        result += "\n축하합니다! " + highestSatisfactionSoldier + " 님께 추가 휴가 1일이 부여되었습니다!";
        result += "\n가장 많은 휴가를 가진 병사는 " + highestLeaveSoldier + " 입니다.\n";

        JOptionPane.showMessageDialog(frame, result, "게임 결과", JOptionPane.INFORMATION_MESSAGE);
        createShowGUI(frame);
    }


    private static void exitApplication(JFrame frame) {
        JOptionPane.showMessageDialog(null, "프로그램이 종료됩니다.", "종료", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}



