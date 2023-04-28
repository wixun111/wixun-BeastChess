package controller;


import ai.AI;
import listener.GameListener;
import model.PlayerColor;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;
import view.CellComponent;
import view.ChessComponent;
import view.ChessboardComponent;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Stack;

/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 *
*/
public class GameController implements GameListener{
    private Chessboard model;
    private final ChessboardComponent view;
    private PlayerColor currentPlayer;
    private final PlayerColor Player;
    private ChessboardPoint selectedPoint;
    private final AI computer;
    private boolean over;
    private final int mode;
    private final Socket socket;
    private Stack<Object[]> stack = new Stack<>();
    private final String lock = "";
    private boolean canUndo;
    private int askUndo = 0;
    private int askType = 0;
    private int diffculty;
    Boolean permit = false;

    private Object[] output = new Object[3];
    public GameController(ChessboardComponent view, Chessboard model, int mode, Socket socket,int diffculy) throws IOException {
        this.view = view;
        this.model = model;
        this.Player = mode!=2?PlayerColor.BLUE:PlayerColor.RED;
        this.currentPlayer = PlayerColor.BLUE;
        this.mode = mode;
        this.canUndo = mode==0||mode==3;
        this.socket = mode==0?null:socket;
        this.computer = new AI(diffculy);
        this.diffculty = diffculy;
        view.initiateChessComponent(model);
        if(mode==1||mode==2){
            new Thread(this::createInThread).start();
            new Thread(this::createOutThread).start();
        }
        view.registerController(this);
        initialize();
    }
    public int getMode() {
        return mode;
    }

    public Chessboard getModel() {
        return model;
    }

    public Stack<Object[]> getStack() {
        return stack;
    }

    public boolean isOver() {
        return over;
    }

    public void setAskType(int askType) {
        this.askType = askType;
    }

    public PlayerColor getCurrentPlayer(){
        return currentPlayer;
    }
    private void initialize() {
        stack.removeAllElements();
        over = false;
        currentPlayer = PlayerColor.BLUE;
        selectedPoint = null;
        askType = 0;
        askUndo = 0;
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
                        view.repaint();
                        canUndo = false;
                    }else if ((int)in == 2){//收到认输回复
                        if(Player==PlayerColor.BLUE) JOptionPane.showMessageDialog(view, "Blue player wins!");
                        else JOptionPane.showMessageDialog(view, "Red player wins!");
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
                    }else if ((int)in == 5){
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否读取棋谱？","读谱请求", JOptionPane.YES_NO_OPTION)==0){
                            oos.writeObject(6);
                        }else {
                            oos.writeObject(-1);
                        }
                    }else if ((int)in == 6){
                        permit = true;
                        restart(true);
                        permit = false;
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(new Object[]{currentPlayer,over,model,stack});
                        JOptionPane.showMessageDialog(view.getParent(),"读取成功");
                    }
                    else continue;
                }else  {
                    Object[] input = (Object[]) in;
                    System.out.println(input[2]);
                    if(input[0].getClass().equals(PlayerColor.class)){
                        loadData(input);
                        continue;
                    }
                    stack.push(input);
                    concludeMove((ChessboardPoint) input[0],(ChessboardPoint) input[1],(ChessPiece) input[2]);
                }
                System.out.println("接收对象成功！");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getParent(),"链接中断！！！");
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void createOutThread(){
        synchronized (lock){
            while (true){
                try {
                    lock.wait();
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    if (askType==1){
                        oos.writeObject(3);
                    }else if(askType==2){
                        askType=0;
                        oos.writeObject(5);
                    }
                    else if(over){
                        oos.writeObject(2);
                    }
                    else if(askUndo==1&&Player!=currentPlayer){
                        oos.writeObject(0);
                        askUndo = 2;
                    }else oos.writeObject(output);
                    oos.flush();
                    System.out.println("成功发送对象！");
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
        view.getParent().getComponent(view.getParent().getComponentCount()-1).setForeground(currentPlayer.getColor());
    }

    public void win(ChessboardPoint point, ChessPiece chess) {
//        System.out.printf("(blue:%d red:%d)\n",blueCount,redCount);
        if((view.denCell.contains(point)&&view.getGridComponentAt(point).getPlayerColor()!=chess.getOwner())|| model.getBlueCount() ==0||model.getRedCount()==0){
            if(currentPlayer == PlayerColor.BLUE||model.getRedCount()==0) JOptionPane.showMessageDialog(view, "Blue player wins!");
            else JOptionPane.showMessageDialog(view, "Red player wins!");
            over = true;
        }
    }
    public void save() throws IOException {
        File save = new File("save.txt");
        if(!save.exists()) save.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("save.txt")));
        oos.writeObject(new Object[]{currentPlayer,over,model,stack});
        oos.close();
    }
    private void load() throws IOException, ClassNotFoundException {
        if(mode==1||mode==2){
            if (!permit){
                askType = 2;
                assume();
                return;
            }
        }
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("save.txt")));
        Object[] in = (Object[]) ois.readObject();
        loadData(in);
        ois.close();
        view.getParent().getComponent(view.getParent().getComponentCount()-1).setForeground(currentPlayer.getColor());
    }
    private void loadData(Object[] in){
        currentPlayer = (PlayerColor) in[0];
        over = (boolean) in[1];
        model = (Chessboard) in[2];
        stack = (Stack<Object[]>) in[3];
        view.removeChessComponent();
        view.initiateChessComponent(model);
        view.repaint();
        if(!permit) JOptionPane.showMessageDialog(view.getParent(),"读取成功");
    }
    public void capitulate(){
        if(over) return;
        if(JOptionPane.showConfirmDialog(view.getParent(),"你确认认输么？","认输确认", JOptionPane.YES_NO_OPTION)==1) return;
        over = true;
        assume();
        if(Player == PlayerColor.BLUE) JOptionPane.showMessageDialog(view, "Red player wins!");
        else JOptionPane.showMessageDialog(view, "Blue player wins!");
    }

    public void assume(){
        new Thread(() ->{
            synchronized (lock){
                lock.notifyAll();
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
            Object[] output = stack.pop();
            ChessboardPoint src = (ChessboardPoint)output[0];
            ChessboardPoint des = (ChessboardPoint)output[1];
            ChessPiece target = (ChessPiece)output[2];
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
                model.addCount(target.getOwner().equals(PlayerColor.BLUE));
                model.setChessPiece(des,target);
                view.setChessComponentAtGrid(des,new ChessComponent(target,target.getOwner(), view.getChessSize()));
            }
            swapColor();
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
        if(mode==3||mode==0)push(src,des,target);
        ChessPiece chess = model.getChessPieceAt(src);
        if(target!=null&&model.isValidCapture(view,src,des)){
            model.minusCount(target.getOwner()==PlayerColor.RED);
            model.captureChessPiece(view,src,des);
            view.removeChessComponentAtGrid(des);
        }else model.moveChessPiece(src, des);
        onTrap(src,des,chess);
        view.setChessComponentAtGrid(des,view.removeChessComponentAtGrid(src));
        view.repaint();
        win(des,chess);
        this.selectedPoint = null;
//        System.out.println(chess+" move from "+src+" to "+des);
        swapColor();
        if(model.getRedCount()+model.getBlueCount()<6) computer.setDifficulty(diffculty+4);
        else if(model.getRedCount()+model.getBlueCount()<8) computer.setDifficulty(diffculty+2);
        else if(model.getRedCount()+model.getBlueCount()<11) computer.setDifficulty(diffculty+1);
        else computer.setDifficulty(diffculty);
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
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            view.removeChessComponent();
            model.removePieces();
            model.initPieces();
            view.initiateChessComponent(model);
            initialize();
        }
        view.getParent().getComponent(view.getParent().getComponentCount()-1).setForeground(currentPlayer.getColor());
        view.revalidate();
        view.repaint();
    }
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) throws IOException, ClassNotFoundException {
        if (!over&&selectedPoint != null && model.isValidMove(view,selectedPoint,point)) {
            ChessPiece target = model.getChessPieceAt(point);
            if(mode==1||mode==2)  {
                push(selectedPoint,point,target);
                assume();
            }
            concludeMove(selectedPoint,point,target);
//            if(mode==3&&!over) computer.AiTurn();
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
                if(mode==1||mode==2)  {
                    push(selectedPoint,point,model.getChessPieceAt(point));
                    assume();
                }
                concludeMove(selectedPoint,point,target);
//                if(mode==3&&!over) computer.AiTurn();
                if(mode==3) new Thread(() -> computer.AiTurn(model,this)).start();
            }
            view.repaint();
        }
    }
}
