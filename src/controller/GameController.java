package controller;

import ai.AI;
import listener.GameListener;
import model.*;
import model.Timer;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Stack;

import static java.lang.Thread.sleep;

/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 *
*/
public class GameController implements GameListener{
    private int askUndo = 0;
    private int askType = 0;
    private final int difficult;
    private int mode;
    private boolean over;
    private boolean isReplay;
    private boolean isObserver;
    private boolean permit = false;
    private boolean canUndo;
    private boolean netBreak = false;
    private final String lock = "";
    private final String hostLock = "1";
    private final AI computer;
    private Chessboard model;
    private final ChessboardComponent view;
    private PlayerColor currentPlayer;
    private final PlayerColor Player;
    private ChessboardPoint selectedPoint;
    private Socket socket;
    private Socket hostSocket;
    private Stack<Object[]> stack = new Stack<>();
    private ServerSocket serverSocket;
    private ServerSocket ObserverSocket;
    protected ArrayList<User> users = new ArrayList<>();
    private User user;
    private final Font pixel;
    private final Timer timer;
    private JLabel playerLabel;
    private Object[] output = new Object[3];
    public GameController(ChessboardComponent view, Chessboard model, int mode, Socket socket,int difficulty) throws IOException {
        this.view = view;
        this.model = model;
        this.Player = mode!=2?PlayerColor.BLUE:PlayerColor.RED;
        this.currentPlayer = PlayerColor.BLUE;
        this.mode = mode;
        this.canUndo = mode==0||mode==3||mode==4;
        this.socket = mode==0?null:socket;
        if(socket!=null) socket.setReuseAddress(true);
        this.computer = new AI(difficulty);
        this.difficult = difficulty;
        this.timer = new Timer(this);
        try {
            pixel = Font.createFont(Font.TRUETYPE_FONT,new File("resource\\Character\\pixel4.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        view.initiateChessComponent(model);
        if(mode==1||mode==2){
            new Thread(this::createInThread).start();
            new Thread(this::createOutThread).start();
        }
        else if(mode==4){
            new Thread(this::createInThread).start();
        }
        view.registerController(this);
        initialize();
        timer.start();
    }
    public int getMode() {
        return mode;
    }

    public AI getComputer() {
        return computer;
    }

    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }

    public Chessboard getModel() {
        return model;
    }

    public Stack<Object[]> getStack() {
        return stack;
    }

    public ChessboardComponent getView() {
        return view;
    }

    public boolean isReplay() {
        return isReplay;
    }

    public boolean isOver() {
        return over;
    }

    public void setAskType(int askType) {
        this.askType = askType;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPlayerLabel(JLabel playerLabel) {
        this.playerLabel = playerLabel;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public PlayerColor getCurrentPlayer(){
        return currentPlayer;
    }
    private void initialize() {
        if(mode==1){
            new Thread(() -> {
                try {
                    ObserverSocket = new ServerSocket(8889);
                    hostSocket = ObserverSocket.accept();
                    isObserver = true;
                    new Thread(this::createOutThreadObserver).start();
                    JOptionPane.showMessageDialog(view.getParent(), "观战者链接成功！");
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }).start();
        }
        stack.removeAllElements();
        over = false;
        currentPlayer = PlayerColor.BLUE;
        selectedPoint = null;
        askType = 0;
        askUndo = 0;
        timer.setTime(45);
    }
    private void createInThread(){
        while (true){
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object in = ois.readObject();
                if(in.getClass().equals(Integer.class)){
                    if((int)in == 0){//收到悔棋请求
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否允许对方悔棋？","悔棋请求", JOptionPane.YES_NO_OPTION)==0){
                            oos.writeObject(1);
                            System.out.println(stack.size());
                            canUndo = true;
                            undo();
                            canUndo = false;
                        }else {
                            oos.writeObject(-1);
                        }
                        System.out.println("成功发送对象！");
                        oos.flush();
                    }else if ((int)in == 1){//收到悔棋回复
                        canUndo = true;
                        undo();
                        canUndo = false;
                    }else if ((int)in == 2){//收到认输回复
                        over = true;
                    }else if ((int)in == 3){//收到重开请求
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否开始新的一局？","重开请求", JOptionPane.YES_NO_OPTION)==0){
                            restart(false);
                            oos.writeObject(4);
                        }else {
                            oos.writeObject(-1);
                        }
                    }else if ((int)in == 4){//收到重开回复
                        restart(false);
                    }else if ((int)in == 5){//收到读谱请求
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否读取棋谱？","读谱请求", JOptionPane.YES_NO_OPTION)==0){
                            oos.writeObject(6);
                        }else {
                            oos.writeObject(-1);
                        }
                    }else if ((int)in == 6){//收到读谱回复
                        permit = true;
                        restart(true);
                        permit = false;
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(new Object[]{currentPlayer,over,model,stack});
                    }
                    else continue;
                }else  {
                    if(isObserver){
                        output = (Object[]) in;
                        assumeHost();
                    }
                    Object[] input = (Object[]) in;
                    if(input[0].getClass().equals(PlayerColor.class)){
                        loadData(input);
                        continue;
                    }else if(model.getChessPieceAt((ChessboardPoint) input[0])==null&&canUndo){
                        undo();
                        continue;
                    }
                    concludeMove((ChessboardPoint) input[0],(ChessboardPoint) input[1],(ChessPiece) input[2]);
                }
                System.out.println("接收对象成功！");
            } catch (IOException e) {
                timer.setPause(true);
                JOptionPane.showMessageDialog(view.getParent(),"链接中断!");
                e.printStackTrace();
                netBreak = true;over = true;
                assume();
                try {
                    sleep(1000);
                    if(mode==1){
                        setSocket(serverSocket.accept());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(new Object[]{currentPlayer,false,model,stack});
                    }
                    else {
                        while (true){
                            try {
                                socket = new Socket(socket.getInetAddress().getHostName(),socket.getPort());
                                if(!socket.isClosed()) break;
                                sleep(5000);
                            } catch (IOException ee) {
                                System.out.println("连接失败！");
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(new Object[]{currentPlayer,false,model,stack});
                    }
                    timer.setPause(false);
                    JOptionPane.showMessageDialog(view.getParent(),"重新链接成功！！！");
                    netBreak = false;over = false;
                } catch (IOException exc) {
                    exc.printStackTrace();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void createOutThreadObserver(){
        synchronized (hostLock){
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(hostSocket.getOutputStream());
                oos.writeObject(new Object[]{currentPlayer,false,model,stack,timer.getTime()});
                while (true){
                    try {
                        hostLock.wait();
                        oos = new ObjectOutputStream(hostSocket.getOutputStream());
                        oos.writeObject(output);
                        oos.flush();
                        System.out.println("成功发送对象给观战者!");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(view.getParent(),"与观战者链接中断！！！");
                        e.printStackTrace();
                        hostSocket = ObserverSocket.accept();
                        JOptionPane.showMessageDialog(view.getParent(),"与观战者重新链接成功！！！");
                        oos = new ObjectOutputStream(hostSocket.getOutputStream());
                        oos.writeObject(new Object[]{currentPlayer,false,model,stack,timer.getTime()});
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void createOutThread(){
        synchronized (lock){
            while (true){
                try {
                    lock.wait();
                    if(netBreak) continue;
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    if (askType==1){
                        oos.writeObject(3);
                    }else if(askType==2){
                        askType=0;
                        oos.writeObject(5);
                    }else if(over){
                        oos.writeObject(2);
                    }else if(askUndo==1&&Player!=currentPlayer){
                        oos.writeObject(0);
                        askUndo = 2;
                    }else{
                        oos.writeObject(output);
                        if(isObserver) assumeHost();
                    }
                    oos.flush();
                    System.out.println("成功发送对象!");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(view.getParent(),"链接中断！！！");
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void swapColor() {//变化当前回合颜色
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        playerLabel.setForeground(currentPlayer.getColor());
    }

    public void win(ChessboardPoint point, ChessPiece chess) {
        System.out.printf("(blue:%d red:%d)\n",model.getBlueCount(),model.getRedCount());
        if((view.denCell.contains(point)&&view.getGridComponentAt(point).getPlayerColor()!=chess.getOwner())|| model.getBlueCount() ==0||model.getRedCount()==0){
            if(currentPlayer == PlayerColor.BLUE||model.getRedCount()==0) {
                if(mode==3){
                    int score;
                    if (difficult == Constant.EASY.getNum()) {
                        score = 1;
                    } else if (difficult == Constant.NORMAL.getNum()) {
                        score = 3;
                    } else if (difficult == Constant.DIFFICULT.getNum()) {
                        score = 10;
                    } else {
                        score = 0;
                    }
                    user.setScore(user.getScore()+score);
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("users.txt")));
                        oos.writeObject(users);
                        oos.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                setMessageDialog("蓝方获胜!","获胜信息",Font.BOLD,15);
            }
            else setMessageDialog("红方获胜!","获胜信息",Font.BOLD,15);
            over = true;
        }
    }
    public void setMessageDialog(String text,String title,int style,int size){
        String[] options = { "确认" };
        UIManager.put("OptionPane.buttonFont", getPixel(style,size));
        JLabel titleLabel = new JLabel("自定义标题");
        titleLabel.setFont(getPixel(style,size));
        UIManager.put("OptionPane.message", titleLabel);
        JLabel message = new JLabel(text);
        message.setFont(getPixel(style,size));
        message.setHorizontalTextPosition(SwingConstants.CENTER);
        JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }
    public void save() throws IOException {
        if(netBreak) return;
        String fileName = JOptionPane.showInputDialog("存档名");
        if (fileName==null||fileName.equals("")) return;
        System.out.println("1"+fileName+"1");
        File save = new File("save\\" + fileName + ".txt");
        try {
            if(!new File("save").exists()) Files.createDirectory(Path.of("save"));
            if(save.exists()){
                int n = JOptionPane.showConfirmDialog(view.getParent(), "存档已存在，是否覆盖?", "", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    save.delete();
                }else return;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        if(!save.exists()) save.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(save)));
        oos.writeObject(new Object[]{currentPlayer,over,model,stack});
        oos.close();
        JOptionPane.showMessageDialog(view.getParent(),"保存成功");
    }
    private void load() throws IOException, ClassNotFoundException {
        if(netBreak) return;
        if(mode==1||mode==2){
            if (!permit){
                askType = 2;
                assume();
                return;
            }
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("save"));
        int state = chooser.showOpenDialog(view.getParent());
        File file = chooser.getSelectedFile();
        if(state==JFileChooser.CANCEL_OPTION) return;
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        Object[] in = (Object[]) ois.readObject();
        loadData(in);
        ois.close();
        playerLabel.setForeground(getCurrentPlayer().getColor());
    }
    private void loadData(Object[] in){
        currentPlayer = (PlayerColor) in[0];
        over = (boolean) in[1];
        model = (Chessboard) in[2];
        stack = (Stack<Object[]>) in[3];
        if(in.length==5) timer.setTime((int) in[4]);
        playerLabel.setForeground(currentPlayer.getColor());
        view.removeChessComponent();
        view.initiateChessComponent(model);
        view.repaint();
        if(!permit) JOptionPane.showMessageDialog(view.getParent(),"读取成功");
    }
    public void capitulate(){
        if(over||netBreak) return;
        if(JOptionPane.showConfirmDialog(view.getParent(),"你确认认输么？","认输确认", JOptionPane.YES_NO_OPTION)==1) return;
        over = true;
        assume();
        if(Player == PlayerColor.BLUE) setMessageDialog("蓝方获胜!","获胜信息",Font.BOLD,15);
        else setMessageDialog("红方获胜!","获胜信息",Font.BOLD,15);
    }

    public void assume(){
        new Thread(() ->{
            synchronized (lock){
                lock.notifyAll();
            }
        }).start();
    }
    public void assumeHost(){
        new Thread(() ->{
            synchronized (hostLock){
                hostLock.notifyAll();
            }
        }).start();
    }

    public void push(ChessboardPoint src,ChessboardPoint des, ChessPiece target){
        output = new Object[]{src, des, target};
        stack.push(output);
    }
    public void undo(){
        if(canUndo){
            if(stack.empty()) return;
            if(selectedPoint!=null){
                view.getChessComponentAt(selectedPoint).setSelected(false);
                selectedPoint = null;
            }
            output = stack.pop();
            ChessboardPoint src = (ChessboardPoint)output[0];
            ChessboardPoint des = (ChessboardPoint)output[1];
            ChessPiece target = (ChessPiece)output[2];
            if(mode==1&&isObserver){
                assumeHost();
            }
            ChessPiece chess = model.getChessPieceAt(des);
            if(chess!=null&&view.getGridComponentAt(des).getPlayerColor()!=chess.getOwner()){
                if(view.getGridComponentAt(src).getPlayerColor()!=chess.getOwner()&&view.trapCell.contains(src)){
                    chess.setRank(0);
                }
                else if(view.trapCell.contains(des)){
                    chess.setRank();
                }
            }
            model.moveChessPiece(des,src);
            view.setChessComponentAtGrid(src, view.removeChessComponentAtGrid(des));
            if(target!=null){
                System.out.println(target.getOwner().equals(PlayerColor.BLUE));
                model.addCount(target.getOwner().equals(PlayerColor.BLUE));
                model.setChessPiece(des,target);
                view.setChessComponentAtGrid(des,new ChessComponent(target, view.getChessSize()));
            }
            swapColor();
            view.revalidate();
            view.repaint();
            askUndo=0;
            over = false;
//            System.out.println(chess+" undo from "+des+" to "+src);
        }
        if((mode==1||mode==2)&&Player!=currentPlayer&&!canUndo&&askUndo==0) {
            askUndo = 1;
            assume();
        }
    }
    public void concludeMove(ChessboardPoint src,ChessboardPoint des,ChessPiece target){
        push(src,des,target);
        ChessPiece chess = model.getChessPieceAt(src);
        if(target!=null&&model.isValidCapture(view,src,des)){
            model.minusCount(target.getOwner()==PlayerColor.BLUE);
            model.captureChessPiece(src,des);
            view.removeChessComponentAtGrid(des);
        }else model.moveChessPiece(src, des);
        onTrap(src,des,chess);
        view.setChessComponentAtGrid(des,view.removeChessComponentAtGrid(src));
        view.repaint();
        win(des,chess);
        this.selectedPoint = null;
        swapColor();
        if(model.getRedCount()+model.getBlueCount()<6) computer.setDifficulty(difficult + 4);
        else if(model.getRedCount()+model.getBlueCount()<8) computer.setDifficulty(difficult + 2);
        else if(model.getRedCount()+model.getBlueCount()<11) computer.setDifficulty(difficult + 1);
        else computer.setDifficulty(difficult);
        if((mode==1&&currentPlayer==PlayerColor.RED)||(mode==2&&currentPlayer==PlayerColor.BLUE)){
            assume();
        }
        timer.setPause(false);
        timer.setTime(45);
        timer.getTimeLabel().setText(("时间:" + timer.getTime()));
    }
    private void onTrap(ChessboardPoint selectedPoint,ChessboardPoint point,ChessPiece chess){
        if(view.getGridComponentAt(point).getPlayerColor()!=chess.getOwner()){
            if(view.trapCell.contains(point)){
                chess.setRank(0);
            }
            else if(view.trapCell.contains(selectedPoint)){
                chess.setRank();
            }
        }
    }
    public void restart(Boolean load){
        if(load) {
            try {
                load();
                playerLabel.setForeground(currentPlayer.getColor());
                timer.getTimeLabel().setText(("时间:" + 45));
                view.revalidate();
                view.repaint();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            initialize();
            resetBoard();
        }
    }
    public void resetBoard(){
        view.removeChessComponent();
        model.removePieces();
        model.initPieces();
        playerLabel.setForeground(currentPlayer.getColor());
        timer.getTimeLabel().setText(("时间:" + 45));
        view.initiateChessComponent(model);
        view.revalidate();
        view.repaint();
    }
    public void replay(){
        Chessboard temp = model;
        new Thread(() ->{
            resetBoard();
            int n = stack.size();
            System.out.println(n);
            isReplay = true;
            for (Object[] objects : stack) {
                try {
                    sleep(500);
                    Object[] output = objects;
                    ChessboardPoint src = (ChessboardPoint) output[0];
                    ChessboardPoint des = (ChessboardPoint) output[1];
                    ChessPiece target = (ChessPiece) output[2];
                    if (target != null) {
                        model.captureChessPiece(src, des);
                        view.removeChessComponentAtGrid(des);
                    } else model.moveChessPiece(src, des);
                    view.setChessComponentAtGrid(des, view.removeChessComponentAtGrid(src));
                    view.repaint();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            isReplay = false;
        }).start();
        model = temp;
    }
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) throws IOException, ClassNotFoundException {
        if (!over&&selectedPoint != null && model.isValidMove(view,selectedPoint,point)) {
            ChessPiece target = model.getChessPieceAt(point);
            concludeMove(selectedPoint,point,target);
            if(mode==3) new Thread(() -> computer.AiTurn(model,this)).start();
        }
    }
    @Override
    public void onPlayerClickChessPiece(ChessboardPoint point, ChessComponent component) {
        if(!over&&(Player==currentPlayer||mode==0)){
            ChessPiece target = model.getChessPieceAt(point);
            if (selectedPoint == null) {
                if(model.getChessPieceOwner(point).equals(currentPlayer)){
                    selectedPoint = point;
                    component.setSelected(true);
                    component.repaint();
                }else return;
            } else if (selectedPoint.equals(point)) {
                selectedPoint = null;
                component.setSelected(false);
                component.repaint();
            } else if(model.getChessPieceOwner(point).equals(model.getChessPieceOwner(selectedPoint))){
                component.setSelected(true);
                view.getChessComponentAt(selectedPoint).setSelected(false);
                selectedPoint = point;
                component.repaint();
                view.getChessComponentAt(selectedPoint).repaint();
            } else if(target!=null&&model.isValidCapture(view,selectedPoint,point)){
                concludeMove(selectedPoint,point,target);
                if(mode==3) new Thread(() -> computer.AiTurn(model,this)).start();
            }
            view.repaint();
        }
    }
}
