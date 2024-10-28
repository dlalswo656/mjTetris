import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

class Element{
    //중심점 x,y를 잡고 이것을 중심으로 +-으로 도형 표현할 것이다.
    int centerHeight; // 중심점 X좌표
    int centerWidth; // 중심점 Y좌표
    int colorNum;
    Element(int x, int y, int color){
        this.centerHeight = x;
        this.centerWidth = y;
        this.colorNum = color;
    }
}

class Shape{
    Element current[] = new Element[4];
    int height = 1; // row
    int width = MainTetris.formWidth/2; // column
    // x, y는 항상 도형의 좌측 최상단

    Shape(int shapeNum){
        switch (shapeNum) {
            //0~6 ㄱ ㅣ ㅁ ㄴ  어떤 모양이든 current[0]이 기준점(회전시 x,y / 내릴 나 옆으로 옮길때 x,y좌표 / colorNum 등
            case 0: // ㄱ 모양(세로로 긴)
                current[0] = new Element(height, width, 0);
                current[1] = new Element(height, width+1, 0);
                current[2] = new Element(height+1, width+1, 0);
                current[3] = new Element(height+2, width+1, 0);
                break;
            case 1: // ㅣ 모양
                current[0] = new Element(height, width, 1);
                current[1] = new Element(height+1, width, 1);
                current[2] = new Element(height+2, width, 1);
                current[3] = new Element(height+3, width, 1);
                break;
            case 2: // ㅁ 모양
                current[0] = new Element(height, width, 2);
                current[1] = new Element(height+1, width, 2);
                current[2] = new Element(height, width+1, 2);
                current[3] = new Element(height+1, width+1, 2);
                break;
            case 3: // ㄴ 모양 (가로로 긴)
                current[0] = new Element(height, width, 3);
                current[1] = new Element(height+1, width, 3);
                current[2] = new Element(height+1, width+1, 3);
                current[3] = new Element(height+1, width+2, 3);
                break;
            case 4: // z 모양
                current[0] = new Element(height, width, 4);
                current[1] = new Element(height, width+1, 4);
                current[2] = new Element(height+1, width+1, 4);
                current[3] = new Element(height+1, width+2, 4);
                break;
            case 5: // ㅗ모양
                current[0] = new Element(height, width, 5);
                current[1] = new Element(height+1, width-1, 5);
                current[2] = new Element(height+1, width, 5);
                current[3] = new Element(height+1, width+1, 5);
                break;
            case 6: // z' 모양
                current[0] = new Element(height, width, 6);
                current[1] = new Element(height, width+1, 6);
                current[2] = new Element(height+1, width, 6);
                current[3] = new Element(height+1, width-1, 6);
                break;
        }
    }
    public Element[] transferArray(){ // 랜덤 도형 넘버를 입력 받아서 모양배열을 구성한 뒤, 그리기 위해 배열을 리턴해준다.
        return current;
    }
}

public class MainTetris extends JFrame implements Runnable{
    static int formHeight = 20;
    static int formWidth = 10;
    static int rightDirection = 0;
    static int leftDirection = 1;
    static int downDirection = 2;
    static int rotationDirection = 3;

    static boolean isLeft = false;
    static boolean isRight = false;
    static boolean isDown = false;
    static boolean isRotation = false;

    static boolean needShape = true;

    static boolean fullRow = false;

    static boolean gameEnd = false;

    // 난이도 중 기본값
    int gameScore = 0;
    int plusScore = 10;
    int threadSpeed = 600;

    int recordArray[][]; // 기록용 배열

    JButton b[][];

    int shapeNumber; // random함수로 0~6까지 나와서 makeShape함수의 변수로 사용
    Shape randomFigure; // makeShape의 결과물(Element 배열을 가지고 있다)
    Element[] eleNew; // randomFigure의 리턴값을 받을 Element 배열
    Color colorBox[] = {Color.red, Color.blue, Color.yellow, Color.gray, Color.pink, Color.green, Color.orange};

    JPanel main;

    Thread tetris;

    JMenuBar menuBar;
    JMenu menu_game, menu_file, menu_guide;
    JMenuItem gameStart, gameExit, programExit, gameSave, gameLoad, gameTip;
    JButton btnGameReStart, btnGamePause;
    JLabel textGameScore, textLevelInfo;
    JRadioButton radioHighLevel, radioNormalLevel, radioLowLevel;
    ButtonGroup radioGroup = new ButtonGroup();

    public MainTetris() {
        // 메뉴 구성
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        main = new JPanel();

        menu_game = new JMenu("게임");
        menuBar.add(menu_game);

        menu_file = new JMenu("파일");
        menuBar.add(menu_file);

        menu_guide = new JMenu("도움말");
        menuBar.add(menu_guide);

        gameStart = new JMenuItem("게임 시작하기");
        gameStart.addActionListener(myActionListener);

        gameExit = new JMenuItem("게임 종료하기");
        gameExit.addActionListener(myActionListener);

        programExit = new JMenuItem("프로그램 종료하기");
        programExit.addActionListener(myActionListener);

        gameSave = new JMenuItem("게임 저장하기");
        gameSave.addActionListener(myActionListener);

        gameLoad = new JMenuItem("게임 불러오기");
        gameLoad.addActionListener(myActionListener);

        gameTip = new JMenuItem("게임 도움말");
        gameTip.addActionListener(myActionListener);

        btnGameReStart = new JButton("재시작");
        btnGameReStart.addActionListener(myActionListener);

        btnGamePause = new JButton("일시 정지");
        btnGamePause.addActionListener(myActionListener);

        textGameScore = new JLabel();
        textGameScore.setText("  Score : 0");

        textLevelInfo = new JLabel("  난이도  ");

        radioHighLevel = new JRadioButton("상");
        radioHighLevel.addActionListener(myActionListener);
        radioNormalLevel = new JRadioButton("중");
        radioNormalLevel.addActionListener(myActionListener);
        radioNormalLevel.setSelected(true);
        radioLowLevel = new JRadioButton("하");
        radioLowLevel.addActionListener(myActionListener);
        radioGroup.add(radioHighLevel);
        radioGroup.add(radioNormalLevel);
        radioGroup.add(radioLowLevel);

        menu_game.add(gameStart);
        menu_game.add(gameExit);
        menu_game.add(programExit);

        menu_file.add(gameSave);
        menu_file.add(gameLoad);

        menu_guide.add(gameTip);

        menuBar.add(btnGameReStart);
        menuBar.add(btnGamePause);

        menuBar.add(textLevelInfo);
        menuBar.add(radioHighLevel);
        menuBar.add(radioNormalLevel);
        menuBar.add(radioLowLevel);

        menuBar.add(textGameScore);

        setSize(600, 1000); // JFrame 사이즈

        // 테트리스 판 세팅
        main.setLayout(new GridLayout(formHeight, formWidth));
        main.setSize(550, 1000);

        getContentPane().add(main);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 정상 종료

        b = new JButton[formHeight][formWidth];

        for(int row = 0 ; row < formHeight ; row++) {
            for(int col = 0 ; col < formWidth ; col++) {
                b[row][col] = new JButton();
                main.add(b[row][col]);
                JButton bj = b[row][col];
                bj.addKeyListener(new MyKeyListener());
            }
        }
        makeRecordArray(); // 기록용 배열 세팅
        drawBackGround(); // 테트리스 배경 세팅
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainTetris();
    }

    public void start() { // 쓰레드 start 메소드
        fullRow = false;
        gameEnd = false;
        tetris = new Thread(this);
        tetris.start();
    }

    public void run() {
        try {
            while(!gameEnd) { // gameEnd flag에 따라서
                if(needShape) { // 블럭이 필요한 경우 랜덤 생성
                    shapeNumber = (int)Math.floor(Math.random()*7);// 0~6
                    randomFigure = makeShape(shapeNumber); // 랜덤 숫자를 함수에 넣어서 랜덤 도형 배열을 생성
                    eleNew = randomFigure.transferArray(); // eleNew 배열로 생성한 도형배열을 복사
                    needShape = false; // 생성 후 false로 바꿔준다.

                    if(checkShapetoShape(eleNew)) { // 종료조건 : 새로 생성되는 도형과 겹칠 시
                        drawCurrentShape(); // 도형 겹치는거 보여주고 종료
                        gameEnd = true;
                        JOptionPane.showMessageDialog(null, "Game Over!\n"
                                + "블럭 생성 구간까지 벽돌이 쌓이면 종료입니다.\n"
                                + "최종 스코어 : " + gameScore, "테트리스", JOptionPane.ERROR_MESSAGE);
                        resetRecordArray();
                        drawBackGround();
                        break;
                    }
                }
                // 한줄 지우기 코드
                eraseFullRow();
                // 배경 reset 코드
                drawBackGround();
                // 그리기 코드
                drawCurrentShape();
                // 방향에 맞춰서 도형 움직이는 코드 / default는 downDirection
                move();

                Thread.sleep(threadSpeed);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Shape makeShape(int shapeNumber) { // 랜덤 도형 생성 함수
        Shape randomShape = new Shape(shapeNumber);

        return randomShape;
    }

    public void eraseFullRow() { // 줄 지우기 코드
        for(int row = 0 ; row < formHeight ; row++) {
            fullRow = true;
            for(int col = 0 ; col < formWidth ; col++) {
                if(recordArray[row][col] == -1) {
                    fullRow = false;
                }
            }
            if(fullRow) { // 해당 row가 모두 0이 아닐 경우
                for(int col = 0 ; col <formWidth ; col++) {
                    recordArray[row][col] = -1; // 해당 row 값 0으로 만들기
                }
                gameScore += plusScore; // 점수 더해주기
                textGameScore.setText("  Score : "+gameScore);
                for(int tempRow = row ; tempRow >0 ; tempRow--) { // 한 줄씩 밑으로 내려주기
                    recordArray[tempRow] = recordArray[tempRow-1];
                }
            }
        }
    }

    public void drawBackGround() { // 기록 배열에 기록되어 있는 정보를 통해 테트리스판에 paint
        for(int row = 2 ; row < formHeight ; row++) {
            for(int col = 0 ; col < formWidth ; col++) {
                if(recordArray[row][col] == -1)
                    b[row][col].setBackground(Color.white);
                else
                    b[row][col].setBackground(colorBox[recordArray[row][col]]);
            }
        }
        for(int i = 0 ; i <= 1 ; i++) {
            for(int j = 0 ; j < formWidth ; j++) {
                b[i][j].setBackground(Color.black);
            }
        }
    }

    public void drawCurrentShape() { // 현재 도형을 그리기
        for(int i = 0 ; i < 4 ; i++) {
            JButton jb = b[eleNew[i].centerHeight][eleNew[i].centerWidth];
            jb.setBackground(colorBox[shapeNumber]);
        }
    }

    public void move() { // 도형 이동 함수(left, right, down, rotation)
        if(isLeft) {
            eleNew = moveShape(eleNew, leftDirection);
            isLeft = false;
        }
        else if(isRight) {
            eleNew = moveShape(eleNew, rightDirection);
            isRight = false;
        }
        else if(isDown) { // 두 칸씩
            eleNew = moveShape(eleNew, downDirection);
            eleNew = moveShape(eleNew, downDirection);
            isDown = false;
        }
        else if(isRotation) {
            eleNew = moveShape(eleNew, rotationDirection);
            isRotation = false;
        }
        else
            eleNew = moveShape(eleNew, downDirection);
    }

    public Element[] moveShape(Element[] currentElement, int direction) {
        // 1. 못 움직이는 경우 ( x나 y가 jFrame을 벗어나는 경우)
        // 2. 움직이는 경우 (좌표를 더해준다)
        boolean flag = false; // 충돌플래그 = true면 충돌
        boolean rotationFlag = false; // 회전시 양쪽 경계값 충돌 플래그 = true면 회전하면서 양쪽 경계값 벗어난것

        Element [] updateElement = new Element[4];
        for(int i = 0 ; i < 4 ; i++) { // 이동용 배열을 생성
            updateElement[i] = new Element(0,0,currentElement[i].colorNum);
        }
        switch (direction) {
            case 0: // right
                for(int i = 0 ; i < 4 ; i++) {
                    int tempHeight = currentElement[i].centerHeight; // 높이는 그대로
                    int tempWidth = currentElement[i].centerWidth + 1; // 오른쪽 이동이므로 가로좌표 + 1
                    // return용 배열 복사해두기
                    updateElement[i].centerHeight = tempHeight;
                    updateElement[i].centerWidth = tempWidth;

                    if(tempWidth > formWidth - 1) { // 오른쪽 이동이므로 Width의 경계값을 넘어가면 flag를 변경한다.
                        flag = true; // 충돌
                        break;
                    }
                }

                //경계는 넘지 않았지만 다른 도형에 닿은 경우, 이 도형은 끝나고 새로운 도형이 필요하다.
                if(!flag && checkShapetoShape(updateElement)) { // 경계체크
                    flag = true;
                    needShape = true;
                }

                if(flag == false) {
                    return updateElement; // 이동이 가능하면 update배열을 리턴
                }
                else {
                    // 왼쪽, 오른쪽은 움직이는것만 안되는것이고, recordArray에 기록하면 안됨.
                    return currentElement; // 이동이 불가능하면 기존 배열을 리턴
                }

            case 1: // left
                for(int i = 0 ; i < 4 ; i++) {
                    int tempHeight = currentElement[i].centerHeight;
                    int tempWidth = currentElement[i].centerWidth - 1; // 왼쪽 이동이므로 가로좌표 - 1
                    // return용 배열 복사해두기
                    updateElement[i].centerHeight = tempHeight;
                    updateElement[i].centerWidth = tempWidth;

                    if(tempWidth < 0) { // 왼쪽 이동이므로 0의 경계값을 넘어가면 flag를 변경한다.
                        flag = true;
                        break;
                    }
                }

                //경계는 넘지 않았지만 다른 도형에 닿은 경우, 이 도형은 끝나고 새로운 도형이 필요하다.
                if(!flag && checkShapetoShape(updateElement)) { // 경계체크
                    flag = true;
                    needShape = true;
                }

                if(flag == false) {
                    return updateElement; // 이동이 가능하면 update배열을 리턴
                }
                else {
                    //왼쪽, 오른쪽은 움직이는것만 안되는것이고, recordArray에 기록하면 안됨.
                    return currentElement; // 이동이 불가능하면 기존 배열을 리턴
                }

            case 2: // down
                for(int i = 0 ; i < 4 ; i++) {
                    int tempHeight = currentElement[i].centerHeight + 1;  // 아래 이동이므로 세로좌표 + 1
                    int tempWidth = currentElement[i].centerWidth;
                    // return용 배열 복사해두기
                    updateElement[i].centerHeight = tempHeight;
                    updateElement[i].centerWidth = tempWidth;

                    if(tempHeight > formHeight - 1) { // 아래 이동이므로 Height의 경계값을 넘어가면 flag를 변경한다.
                        flag = true;
                        needShape = true;
                        break;
                    }
                }

                if(!flag && checkShapetoShape(updateElement)) { // 경계체크
                    flag = true;
                    needShape = true;
                }

                if(flag == false) {
                    return updateElement; // 이동이 가능하면 update배열을 리턴
                }
                else {
                    addShapeToRecord(currentElement); // 아래의 경우는 기록을 해야함.
                    return currentElement; // 이동이 불가능하면 기존 배열을 리턴 후 새로운 도형 생성
                }

            case 3: // rotation
                if(currentElement[0].colorNum == 2) // 정사각형은 회전 X
                    return currentElement;
                int standardX = currentElement[0].centerHeight;
                int standardY = currentElement[0].centerWidth;
                for(int i = 0 ; i < 4 ; i++) { // 3번만 회전
                    int tempHeight = standardX - currentElement[i].centerHeight;
                    int tempWidth = standardY - currentElement[i].centerWidth;
                    // return용 배열 복사해두기
                    updateElement[i].centerHeight = standardX + tempWidth; // x = y
                    updateElement[i].centerWidth = standardY - tempHeight; // y = -x

                    if(updateElement[i].centerHeight > formHeight - 1)
                    {
                        flag = true; // 충돌
                        break;
                    }
                    if(updateElement[i].centerWidth > formWidth - 1 ||
                            updateElement[i].centerWidth < 0) {
                        rotationFlag = true;
                        break;
                    }
                }

                if(rotationFlag == true) { //회전하다가 양쪽 벽에 부딪혔을때는 배열에 기록하지 않고 그냥 현재 배열만 반환
                    return currentElement; // 종료를 안하면 checkShapetoShape함수에서 배열예외오류가 발생한다.
                }
                else {
                    if(!flag && checkShapetoShape(updateElement)) { // 경계체크
                        flag = true;
                        needShape = true;
                    }

                    if(flag == false && rotationFlag == false) {
                        return updateElement; // 이동이 가능하면 update배열을 리턴
                    }
                    else if(flag == true && rotationFlag == false){ // 기본 flag == true
                        addShapeToRecord(currentElement);
                        return currentElement; // 이동이 불가능하면 기존 배열을 리턴
                    }
                }

        } //switch 종료

        return updateElement;
    }

    public void makeRecordArray() { // record용 array를 20*10배열 초기화
        recordArray = new int[formHeight][formWidth];
        for(int i = 0 ; i < formHeight ; i++) {
            for(int j = 0 ; j < formWidth ; j++) {
                recordArray[i][j] = -1;
            }
        }
    }
    public void resetRecordArray() { // record용 array를 초기값으로 초기화
        for(int i = 0 ; i < formHeight ; i++) {
            for(int j = 0 ; j < formWidth ; j++) {
                recordArray[i][j] = -1;
            }
        }
    }

    public void addShapeToRecord(Element[] shape) { // 바닥에 닿았을시 RecordArray에 입력
        for(int i = 0 ; i < 4 ; i++) {
            recordArray[shape[i].centerHeight][shape[i].centerWidth] = shape[i].colorNum;
        }
    }
    public boolean checkShapetoShape(Element[] shape) { // 다른 도형과 충돌 체크 함수(도형이 움직이는 자리에 빈칸(-1)이 아닌 다른 수가 저장되어 있을 경우 false)
        boolean checkFlag = false;
        for(int i = 0 ; i < 4 ; i++) {
            if( recordArray[shape[i].centerHeight][shape[i].centerWidth] != -1) {
                checkFlag = true;
                break;
            }
        }
        return checkFlag;
    }

    public void saveRecordArray() { // 현재 게임 내용 저장
        String output = "C:\\homework\\tetrisResult.txt"; // c\:homework 폴더
        File file = new File(output);
        try {
            PrintWriter pw = new PrintWriter(file);
            for(int i = 0 ; i < formHeight ; i++) {
                for(int j = 0 ; j < formWidth ; j++) {
                    pw.print(recordArray[i][j]+ " ");
                }
                pw.println("");
            }
            pw.print(gameScore);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadRecordArray() { // 저장된 게임 내용 불러오기
        String output = "C:\\homework\\tetrisResult.txt"; // c\:homework 폴더
        File file = new File(output);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            for(int i = 0 ; i <= formHeight ; i++) {
                if(i == formHeight) {
                    str = br.readLine();
                    gameScore = Integer.parseInt(str);
                    return;
                }
                else {
                    str = br.readLine();
                }
                String [] splitNum = str.split(" ");
                for(int j = 0 ; j < formWidth ; j++) {
                    if(str==null)
                        break;
                    recordArray[i][j] = Integer.parseInt(splitNum[j]);
                }

                System.out.println("");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyKeyListener extends KeyAdapter{
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) { // 키 이벤트
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    break;

                case KeyEvent.VK_DOWN:
                    isDown = true;
                    break;

                case KeyEvent.VK_LEFT:
                    isLeft = true;
                    break;

                case KeyEvent.VK_RIGHT:
                    isRight = true;
                    break;

                case KeyEvent.VK_SPACE:
                    isRotation = true;
                    break;
            }
        }
    }

    ActionListener myActionListener = new ActionListener() { // 메뉴 클릭 리스너

        @Override
        public void actionPerformed(ActionEvent e) {
            String radio = e.getActionCommand();

            if(e.getSource() == gameStart) {
                focusUnCheck();
                needShape = true;
                main.setFocusable(true);
                start();

            }
            else if(e.getSource() == gameExit) {
                gameEnd = true;
                tetris = null; // thread에 null값을 넣어주기.
                resetRecordArray(); // 배열 초기화
                drawBackGround(); // 테트리스 판 초기화
                gameScore = 0; // 점수 초기화
                textGameScore.setText("  Score : "+gameScore); // 점수판 초기화
            }
            else if(e.getSource() == programExit) { // 완전 창을 종료
                setVisible(false);
                dispose();
                System.exit(0);
            }
            else if(e.getSource() == gameSave) {
                gameEnd = true; // 쓰레드 멈추기
                saveRecordArray(); // 게임 상태 저장
                JOptionPane.showMessageDialog(null, "저장되었습니다!", "테트리스", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(e.getSource() == gameLoad) {
                gameEnd = true; // 쓰레드 멈추기
                loadRecordArray(); // recordArray에 복사 완료
                drawBackGround(); // 불러온 배열 테트리스판에 그리기
                textGameScore.setText("  Score : "+gameScore); // 점수 내용도 불러오기
            }
            else if(e.getSource() == gameTip) {
                JOptionPane.showMessageDialog(null, "테트리스 게임 도움말\n기본 조작키 : 왼쪽,오른쪽,아래 방향키 + 회전 : 스페이스바"
                                + "\n**메뉴 설명**"
                                + "\n - 게임 시작 : 새 테트리스가 시작됩니다"
                                + "\n - 게임 종료 : 진행중인 테트리스가 종료됩니다"
                                + "\n - 프로그램 종료 : 게임 창이 종료됩니다"
                                + "\n - 게임 저장 : 진행중인 테트리스가 기록됩니다."
                                + "\n - 게임 불러오기 : 저장했던 테트리스를 불러옵니다."
                                + "\n ** 게임 저장 및 불러오기 이후 게임 시작을 다시 누르면 이어서 게임이 진행됩니다 **"
                                + "\n - 게임 재시작 및 일시중지 : 현재 게임을 일시중지하고 다시 재시작합니다."
                                + "\n - 난이도 : 블럭의 속도가 달라지며, 스코어도 난이도에 따라 부여됩니다."
                        , "테트리스 도움말", JOptionPane.PLAIN_MESSAGE);
            }
            else if(e.getSource() == btnGameReStart) { // 재시작함수(정지된 상태 그대로 다시 시작시킨다)
                needShape = false;
                start();
                focusUnCheck();
            }
            else if(e.getSource() == btnGamePause) { // 일시 정지
                gameEnd = true;
                focusUnCheck();
            }
            //난이도 radio button
            else if(radio.equals(radioHighLevel.getText())) {
                plusScore = 30;
                threadSpeed = 300;
            }
            else if(radio.equals(radioNormalLevel.getText())) {
                plusScore = 10;
                threadSpeed = 600;
            }
            else if(radio.equals(radioLowLevel.getText())) {
                plusScore = 5;
                threadSpeed = 1000;
            }
        }
    };

    void focusUnCheck() { // 버튼에 focus 해제
        btnGameReStart.setFocusable(false);
        btnGamePause.setFocusable(false);
        radioHighLevel.setFocusable(false);
        radioNormalLevel.setFocusable(false);
        radioLowLevel.setFocusable(false);
    }
}