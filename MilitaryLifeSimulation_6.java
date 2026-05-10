/*김태우.png 출처: https://imgnews.pstatic.net/image/108/2008/12/09/2008120914523202415_1.jpg?type=w647
  최종훈.png 출처 : https://postfiles.pstatic.net/MjAxNzEyMDFfMTE1/MDAxNTEyMTE0NjgzNDQ2.rZPwXK59F8KZtjFLkz6zWojPZg5RZBgQBEKOSkbO7cgg.DDtQG_r67uuMD22yyd_nvrASMD1B9Tr-EXl18KhsHvog.JPEG.sjw608/%EB%A7%90%EB%85%84%EB%B3%91%EC%9E%A5.jpg?type=w966
  굳건이.png 출처 : https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Military_Manpower_Administration_Gutgeoni1.png/200px-Military_Manpower_Administration_Gutgeoni1.png
  나백중.png 출처 : https://img.insight.co.kr/static/2019/02/13/700/97300lv5mvgz35ydca9u.jpg
  군.png 출처 : https://i.namu.wiki/i/8ZvD7obEx8NqEZMfmJPFTqinmCY2-c-v7P409tLGvPbG0HSD9rx-AAEf0eBxB8vohWyHwR4nU5GrqaKjNZnsPQ.webp
  혹한기.png 출처: https://dispatch.cdnser.be/wp-content/uploads/2017/02/20170207162821_1-2.jpg
  군가2 출처 : https://youtu.be/Nb8p3dfh0XA?si=KLaRQndz6qhTPGRn*/

/* 시나리오 제대로 잡기
제목- 병장 혹한기 탈출 시뮬레이션

이벤트 - 두발 단장(단발령) 추가

상황설명- 전역일이 50일 남은 말년 병장 4명이 있다. 전역일 20일 전에 혹한기 훈련이 예정. 이들은 병장이기에 절대 혹한기 훈련을 받지 않을려고 함.
병장 4명이 휴가 10일을 각각 동일하게 가지고 있다. 
휴가 20일을 다모으면 혹한기 훈련을 가지 않는 시스템을 만들자.
휴가를 못모은 사람은 혹한기 훈련을 받다가 휴가를 나가는걸로 한다.*/

//음악 파일 넣었습니다. 중단 버튼도 넣었습니다.
//synchronized는 활용을 잘 못하였습니다.
//휴가 20일 달성 시: 해당 병사를 onLeaveSoldiers에 추가해 휴가 상태로 전환. 버튼 비활성화로 추가 조작 불가
//훈련 점수 달성 시 만족도 회복 가능.
//훈련점수 10점 씩 휴가 1일
// 나머지 메소드들도 조금씩 수정




	

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MilitaryLifeSimulation_6 {

    private static Clip clip;

    static class StartButtonActionListener implements ActionListener {
        private JFrame frame;

        public StartButtonActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            playMusic("src/music/군가2.wav");
            showIntroPopup(frame);
        }
    }

    static class MuteButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopMusic();
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

    static class StartGameActionListener implements ActionListener {
        private JFrame frame;

        public StartGameActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "게임을 시작합니다! 병사들을 관리하여 목표를 달성하세요.", "게임 시작", JOptionPane.INFORMATION_MESSAGE);
            startGameLoop(frame);
        }
    }

    private static HashMap<String, Integer> satisfactionMap = new HashMap<>(); // 병사 만족도
    private static HashMap<String, Integer> leaveMap = new HashMap<>(); // 휴가 일수
    private static HashMap<String, Integer> taskPointsMap = new HashMap<>(); // 훈련 점수
    private static HashMap<String, String> rankMap = new HashMap<>(); // 계급 정보
    private static Set<String> onLeaveSoldiers = new HashSet<>(); // 휴가 중인 병사 목록 Set 참고 출처 : https://blog.naver.com/heartflow89/220994601249
    private static String[] soldierNames = { "최종훈", "김태우", "굳건이", "나백중" };
    private static String[] soldierRanks = { "병장", "병장", "병장", "병장" };
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
        JFrame frame = new JFrame("병장 혹한기 탈출 시뮬레이션");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        try {
            ImageIcon backgroundImage = new ImageIcon("src/images/군.png");
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundLabel);
        } catch (Exception ex) {
            System.out.println("배경 이미지를 로드할 수 없습니다: " + ex.getMessage());
        }

        JButton startButton = new JButton("시작하기");
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);

        startButton.addActionListener(new StartButtonActionListener(frame));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        addMuteButton(buttonPanel);

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void showIntroPopup(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "전역일이 50일 남은 말년 병장 4명이 있습니다.\n전역일 20일 전에 혹한기 훈련이 예정되어있습니다.\n이들은 말년 병장이기에 '절대' 혹한기 훈련을 받지 않으려 하고 있습니다.\n", "상황 설명", JOptionPane.INFORMATION_MESSAGE);

        int response = JOptionPane.showConfirmDialog(frame, "다음으로 진행하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION); // JOptionPane 참고 출처: p789 ~791
        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(frame, "병장 4명이 휴가 10일을 각각 동일하게 가지고 있습니다.\n휴가 20일을 다 모으면 혹한기 훈련을 가지 않고 조기전역을 합니다.\n휴가를 못 모은 사람은 혹한기 훈련을 받다가 휴가를 나가는 안타까운 상황입니다.\n이들의 휴가와 훈련은 당신의 손에 맡겨져있습니다.", "상황 설명", JOptionPane.INFORMATION_MESSAGE);
            createShowGUI(frame);
        }
    }

    private static void createShowGUI(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        try {
            ImageIcon backgroundImage = new ImageIcon("src/images/혹한기.png");
            JLabel backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundLabel);
        } catch (Exception ex) {
            System.out.println("메인 메뉴 배경 이미지를 로드할 수 없습니다: " + ex.getMessage());
        }

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("메뉴");
        JMenuItem startGameMenuItem = new JMenuItem("게임 시작");
        JMenuItem scheduleMenuItem = new JMenuItem("병사 스케줄 관리");
        JMenuItem satisfactionMenuItem = new JMenuItem("병사 만족도 확인");
        JMenuItem leaveMenuItem = new JMenuItem("휴가 관리");
        JMenuItem randomEventMenuItem = new JMenuItem("이벤트 발생");
        JMenuItem exitMenuItem = new JMenuItem("종료");

        menu.add(startGameMenuItem);
        menu.add(scheduleMenuItem);
        menu.add(satisfactionMenuItem);
        menu.add(leaveMenuItem);
        menu.add(randomEventMenuItem);
        menu.addSeparator();
        menu.add(exitMenuItem);

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        startGameMenuItem.addActionListener(new StartGameActionListener(frame));
        scheduleMenuItem.addActionListener(new ScheduleActionListener(frame));
        satisfactionMenuItem.addActionListener(new SatisfactionActionListener(frame));
        leaveMenuItem.addActionListener(new LeaveActionListener(frame));
        randomEventMenuItem.addActionListener(new RandomEventActionListener(frame));
        exitMenuItem.addActionListener(new ExitActionListener(frame));


        JPanel mutePanel = new JPanel();
        mutePanel.setOpaque(false);
        addMuteButton(mutePanel);
        frame.add(mutePanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();

        JOptionPane.showMessageDialog(frame, "메인 메뉴로 이동했습니다. 게임을 시작하거나 원하는 작업을 선택하세요.", "메인 메뉴", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void playMusic(String filePath) { // 참고 : p 809
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("음악을 재생할 수 없습니다: " + e.getMessage());
        }
    }

    private static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private static void addMuteButton(JPanel panel) {
        JButton muteButton = new JButton("음악 끄기");
        muteButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        muteButton.setBackground(Color.WHITE);
        muteButton.setForeground(Color.RED);
        muteButton.addActionListener(new MuteButtonActionListener());
        panel.add(muteButton);
    }

    private static void manageSchedule(JFrame frame) {
        StringBuilder scheduleMessage = new StringBuilder("병사 스케줄 관리:\n");
        for (String name : soldierNames) {
            if (onLeaveSoldiers.contains(name)) {
                scheduleMessage.append(name).append(" - 휴가 중\n");
            } else if (leaveMap.get(name) >= 20) {
                scheduleMessage.append(name).append(" - 휴가 중\n");
                onLeaveSoldiers.add(name); // 휴가 상태로 전환
            } else {
                scheduleMessage.append(name).append(" - 훈련 대기 중\n");
            }
        }
        JOptionPane.showMessageDialog(frame, scheduleMessage.toString(), "스케줄 관리", JOptionPane.INFORMATION_MESSAGE);
    }


    private static void showSatisfactionStatus(JFrame frame) {
        StringBuilder status = new StringBuilder("병사 만족도 현황:\n"); //StringBuilder 참고 출처 : https://onlyfor-me-blog.tistory.com/317
        for (String name : soldierNames) {
            status.append(name).append(" - 만족도: ").append(satisfactionMap.get(name)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, status.toString(), "만족도 확인", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void manageLeave(JFrame frame) {
        StringBuilder leaveStatus = new StringBuilder("병사 휴가 현황:\n");
        for (String name : soldierNames) {
            leaveStatus.append(name).append(" - 남은 휴가: ").append(leaveMap.get(name)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, leaveStatus.toString(), "휴가 관리", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void triggerRandomEvent(JFrame frame) {
        String[] events = {"야간 경계", "체력 단련", "전술 훈련", "단발령"};
        Random random = new Random();
        String selectedEvent = events[random.nextInt(events.length)];

        for (String name : soldierNames) {
            if (onLeaveSoldiers.contains(name)) continue;

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
                case "단발령":
                    satisfaction -= 15;
                    break;
            }
            satisfactionMap.put(name, Math.max(satisfaction, 0));
            taskPointsMap.put(name, points);
        }

        JOptionPane.showMessageDialog(frame, "랜덤 이벤트 발생: " + selectedEvent, "이벤트 발생", JOptionPane.INFORMATION_MESSAGE);
    }

    

    private static void exitApplication(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "프로그램이 종료됩니다.", "종료", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private static synchronized void updateSoldierStates(JPanel soldierPanel) {
        for (int i = 0; i < soldierNames.length; i++) {
            // 휴가 중인 병사는 최종 상태를 유지하며 업데이트
            if (onLeaveSoldiers.contains(soldierNames[i])) {
                int leave = leaveMap.get(soldierNames[i]);
                int satisfaction = satisfactionMap.get(soldierNames[i]);
                int points = taskPointsMap.get(soldierNames[i]);

                // soldierPanel에서 정보를 동기화
                JPanel individualPanel = (JPanel) soldierPanel.getComponent(i);
                JLabel statsLabel = (JLabel) individualPanel.getComponent(2);
                statsLabel.setText("만족도: " + satisfaction + " | 훈련 점수: " + points + " | 휴가: " + leave);
                continue; 
            }

            int satisfaction = satisfactionMap.get(soldierNames[i]);
            int points = taskPointsMap.get(soldierNames[i]);
            int leave = leaveMap.get(soldierNames[i]);

            // 훈련 점수 및 만족도 업데이트
            if (points >= 10) {
                points -= 10;
                taskPointsMap.put(soldierNames[i], points);

                leave += 1;
                leaveMap.put(soldierNames[i], leave);

                satisfaction += 5;
                satisfactionMap.put(soldierNames[i], satisfaction);
            }

            // 휴가 20일 달성 시 처리
            if (leave >= 20) {
                JOptionPane.showMessageDialog(null, soldierNames[i] + " 병사가 휴가 20일을 달성했습니다! 휴가를 떠납니다!", "휴가 이벤트", JOptionPane.INFORMATION_MESSAGE);

                // 병사 비활성화
                JPanel individualPanel = (JPanel) soldierPanel.getComponent(i);
                JButton actionButton = (JButton) individualPanel.getComponent(3); 
                actionButton.setEnabled(false); // 버튼 비활성화 참고 출저 :https://yoo11052.tistory.com/17

                onLeaveSoldiers.add(soldierNames[i]); // 휴가 중인 병사 목록에 추가
                continue; 
            }

            satisfaction = Math.max(0, satisfaction - 1); // 매일 만족도 감소
            satisfactionMap.put(soldierNames[i], satisfaction);

            // soldierPanel에서 업데이트된 정보 반영
            JPanel individualPanel = (JPanel) soldierPanel.getComponent(i);
            JLabel statsLabel = (JLabel) individualPanel.getComponent(2); 
            statsLabel.setText("만족도: " + satisfaction + " | 훈련 점수: " + points + " | 휴가: " + leave);
        }
    }

    private static void showGameResults(JFrame frame) {
        StringBuilder result = new StringBuilder("게임 종료! 최종 결과:\n");
        List<String> leaveAchievers = new ArrayList<>();
        List<String> winterCampers = new ArrayList<>();

        // 휴가 20일 모은 병사 처리
        for (String name : soldierNames) {
            int leaveDays = leaveMap.get(name);
            if (leaveDays >= 20) {
                leaveAchievers.add(name);
            } else {
                winterCampers.add(name);
            }
        }

        // 비활성화된 병사 정보도 결과에 반영
        for (String name : onLeaveSoldiers) {
            if (!leaveAchievers.contains(name)) {
                leaveAchievers.add(name);
            }
        }

        // 결과 출력
        for (String name : soldierNames) {
            int satisfaction = satisfactionMap.get(name);
            int taskPoints = taskPointsMap.get(name);
            int leaveDays = leaveMap.get(name);

            result.append(name).append(" - 만족도: ").append(satisfaction).append(", 훈련 점수: ").append(taskPoints).append(", 휴가: ").append(leaveDays).append("\n");
        }

        JOptionPane.showMessageDialog(frame, result.toString(), "게임 결과", JOptionPane.INFORMATION_MESSAGE);

        if (!leaveAchievers.isEmpty()) {
            for (String name : leaveAchievers) {
                JOptionPane.showMessageDialog(frame, "휴가자 " + name + " 그는 혹한기를 가지 않게되었습니다. 축하드립니다!", "결과", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (!winterCampers.isEmpty()) {
            for (String name : winterCampers) {
                JOptionPane.showMessageDialog(frame, name + " 그는 혹한기 훈련을 받고 휴가를 나가게 되었습니다. 안타깝네요. ㅠㅠ", "결과", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        createShowGUI(frame);
    }

    // showGameResults 호출 추가
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
            Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); //참조 출처: https://blog.naver.com/wonsukdream/30138796350
            imageLabel.setIcon(new ImageIcon(scaledImage));

            JLabel nameLabel = new JLabel(soldierNames[i], SwingConstants.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

            JLabel statsLabel = new JLabel("만족도: " + satisfactionMap.get(soldierNames[i]) +" | 훈련 점수: " + taskPointsMap.get(soldierNames[i]) +" | 휴가: " + leaveMap.get(soldierNames[i]),SwingConstants.CENTER);
            statsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

            JButton actionButton = new JButton(soldierNames[i] + " 훈련");
            int index = i;
            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (onLeaveSoldiers.contains(soldierNames[index])) return; // 휴가 중인 병사는 무시

                    int satisfaction = satisfactionMap.get(soldierNames[index]);
                    int points = taskPointsMap.get(soldierNames[index]);
                    satisfaction -= 3;
                    points += 5;
                    satisfactionMap.put(soldierNames[index], satisfaction);
                    taskPointsMap.put(soldierNames[index], points);
                    statsLabel.setText("만족도: " + satisfaction + " | 훈련 점수: " + points + " | 휴가: " + leaveMap.get(soldierNames[index]));
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
        addMuteButton(buttonPanel);
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

                // soldierPanel 업데이트와 GUI 업데이트를 같은 스레드에서 처리
                updateSoldierStates(soldierPanel);
                updateAllGUIStates();

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
                JOptionPane.showMessageDialog(frame, "메인 메뉴로 돌아갑니다. 관리할 메뉴를 선택하세요!", "메인 메뉴", JOptionPane.INFORMATION_MESSAGE);
                createShowGUI(frame);
            }
        });
    }

    private static synchronized void updateAllGUIStates() {
        System.out.println("GUI 상태가 업데이트되었습니다. 만족도 및 휴가 정보가 동기화됩니다.");
    }
}
