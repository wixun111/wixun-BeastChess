package controller;


import listener.GameListener;
import model.PlayerColor;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;
import view.CellComponent;
import view.ChessComponent;
import view.ChessboardComponent;

import javax.swing.*;
import java.awt.*;
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
public class GameController implements GameListener {

    private final Chessboard model;
    private final ChessboardComponent view;
    private PlayerColor currentPlayer;
    private final PlayerColor Player;
    private ChessboardPoint selectedPoint;
    private boolean over;
    private int redCount,blueCount;
    private final int mode;
    private final Socket socket;
    private final Stack<Object[]> stack = new Stack<>();
    private final Object lock = new Object();
    private boolean canUndo;
    private int askUndo = 0;
    private int askType = 0;

    private Object[] output = new Object[3];
    public GameController(ChessboardComponent view, Chessboard model, int mode, Socket socket) throws IOException {
        this.view = view;
        this.model = model;
        this.Player = (mode==0||mode==1)?PlayerColor.BLUE:PlayerColor.RED;
        this.currentPlayer = PlayerColor.BLUE;
        this.mode = mode;
        this.canUndo = mode==0;
        this.socket = mode==0?null:socket;
        view.initiateChessComponent(model);
        if(mode!=0){
            new Thread(this::createInThread).start();
            new Thread(this::createOutThread).start();
        }
        view.registerController(this);
        initialize();
    }
    public int getMode() {
        return mode;
    }
    public boolean isOver() {
        return over;
    }

    public void setAskType(int askType) {
        this.askType = askType;
    }

    public ChessboardComponent getView(){
        return view;
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
        redCount = 8;
        blueCount = 8;
    }
    private void createInThread(){
        while (true){
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object in = ois.readObject();
                if(in.getClass().equals(Integer.class)){
                    if((int)in == 0){//收到悔棋请求
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否允许对方悔棋？","悔棋请求",0)==0){
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
                    }else if ((int)in == 2){//收到认输回复
                        if(Player==PlayerColor.BLUE) JOptionPane.showMessageDialog(view, "Blue player wins!");
                        else JOptionPane.showMessageDialog(view, "Red player wins!");
                        over = true;
                    }else if ((int)in == 3){//收到重开请求
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        if(JOptionPane.showConfirmDialog(view.getParent(),"是否开始新的一局？","重开请求",0)==0){
                            restart(false);
                            oos.writeObject(4);
                        }else {
                            oos.writeObject(-1);
                        }
                    }else if ((int)in == 4){//收到重开回复
                        restart(false);
                    }
                    else continue;
                }else {
                    Object[] input = (Object[]) in;
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

    private void swapColor() {//变化当前回合颜色
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        view.getParent().getComponent(view.getParent().getComponentCount()-1).setForeground(currentPlayer.getColor());
    }

    private void win(ChessboardPoint point,ChessPiece chess) {
        System.out.printf("(blue:%d red:%d)\n",blueCount,redCount);
        if((view.denCell.contains(point)&&view.getGridComponentAt(point).getPlayerColor()!=chess.getOwner())||blueCount==0||redCount==0){
            if(currentPlayer == PlayerColor.BLUE||redCount==0) JOptionPane.showMessageDialog(view, "Blue player wins!");
            else JOptionPane.showMessageDialog(view, "Red player wins!");
            over = true;
        }
    }
    public void save() throws IOException {
        File save = new File("save.txt");
        if(!save.exists())save.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter("save.txt"));
        bw.write(blueCount+" ");
        bw.write(redCount+"\n");
        bw.write(currentPlayer+"\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint temp = new ChessboardPoint(i,j);
                if(model.getChessPieceAt(temp)!=null){
                    bw.write(model.getChessPieceAt(temp).getOwner()+" ");
                    bw.write(model.getChessPieceAt(temp).getName()+" ");
                    bw.write(model.getChessPieceAt(temp).getRank()+" ");
                    bw.write(i+" ");
                    bw.write(j+"\n");
                }
            }
        }
        bw.write(stack.size() + "\n");
        for (int i = 0; i < stack.size(); i++) {
            bw.write(stack.get(i)[0] + " ");
            bw.write(stack.get(i)[1] + " ");
            if(stack.get(i)[2]!=null) {
                bw.write(((ChessPiece)stack.get(i)[2]).getOwner()+" ");
                bw.write(((ChessPiece)stack.get(i)[2]).getName()+" ");
                bw.write(((ChessPiece)stack.get(i)[2]).getRank()+" ");
            }else {
                bw.write("null");
            }
            bw.write("\n");
        }
        bw.close();
    }
    private void load() throws IOException{
        initialize();
        model.removePieces();
        view.removeChessComponent();
        BufferedReader br = new BufferedReader(new FileReader("save.txt"));
        System.out.println("读取");
        String line;
        line = br.readLine();
        blueCount = Integer.parseInt(line.split(" ")[0]);
        redCount = Integer.parseInt(line.split(" ")[1]);
        int Count = blueCount + redCount;
        currentPlayer = br.readLine().equals("BLUE")?PlayerColor.BLUE:PlayerColor.RED;
        for (int i = 0; i < Count; i++) {
            line = br.readLine();
            String[] argument = line.split(" ");
            int row = Integer.parseInt(argument[3]);
            int col = Integer.parseInt(argument[4]);
            if(!over&&view.denCell.contains(new ChessboardPoint(row,col))) over=true;
            model.setGrid(row, col, Integer.parseInt(argument[2]), argument[0], argument[1]);
        }
        view.initiateChessComponent(model);
        int n = Integer.parseInt(br.readLine());
        for (int i = 0; i < n; i++) {
            String temp = br.readLine();
            String[] argument = temp.split(" +");
            ChessboardPoint des = new ChessboardPoint(Integer.parseInt(argument[0]),Integer.parseInt(argument[1]));
            ChessboardPoint src = new ChessboardPoint(Integer.parseInt(argument[2]),Integer.parseInt(argument[3]));
            ChessPiece chess = argument[4].equals("null")?null:new ChessPiece(argument[4].equals("BLUE")?PlayerColor.BLUE:PlayerColor.RED,argument[5],Integer.parseInt(argument[6]));
            push(des,src,chess);
        }
        getView().repaint();
        br.close();
    }
    public void capitulate(){
        if(over) return;
        if(JOptionPane.showConfirmDialog(view.getParent(),"你确认认输么？","认输确认",0)==1) return;
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

    public void push(ChessboardPoint des,ChessboardPoint src, ChessPiece chess){
        output = new Object[]{des, src, chess};
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
            ChessboardPoint des = (ChessboardPoint)output[0];
            ChessboardPoint src = (ChessboardPoint)output[1];
            ChessPiece chess = (ChessPiece)output[2];
            if(view.trapCell.contains(des)){
                model.getChessPieceAt(src).setRank(0);
            }
            else if(view.trapCell.contains(src)){
                model.getChessPieceAt(src).setRank();
            }
            model.moveChessPiece(src,des);
            view.setChessComponentAtGrid(des, view.removeChessComponentAtGrid(src));
            if(chess!=null){
                if(chess.getOwner().equals(PlayerColor.BLUE)) blueCount++;
                else redCount++;
                model.setChessPiece(src,chess);
                view.setChessComponentAtGrid(src,new ChessComponent(chess,chess.getOwner(), view.getChessSize()));
            }
            swapColor();
            view.repaint();
            askUndo=0;
            over = false;
        }
        if(mode!=0&&Player!=currentPlayer&&!canUndo&&askUndo==0) {
            askUndo = 1;
            assume();
        }
    }
    public void concludeMove(ChessboardPoint selectedPoint,ChessboardPoint point,ChessPiece target){
        ChessPiece chess = model.getChessPieceAt(selectedPoint);
        if(target!=null&&chess.canCapture(target)&&model.isValidCapture(view,selectedPoint,point)){
            if(chess.getOwner()==PlayerColor.RED)redCount--;
            else blueCount--;
            model.captureChessPiece(view,selectedPoint,point);
        }else model.moveChessPiece(selectedPoint, point);
        onTrap(selectedPoint,point,chess);
        view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
        view.repaint();
        win(point,chess);
        this.selectedPoint = null;
        swapColor();
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            view.removeChessComponent();
            model.removePieces();
            model.initPieces();
            view.initiateChessComponent(model);
            initialize();
            view.repaint();
        }
        view.getParent().getComponent(view.getParent().getComponentCount()-1).setForeground(currentPlayer.getColor());
    }
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) throws IOException, ClassNotFoundException {
        if (!over&&selectedPoint != null && model.isValidMove(view,selectedPoint,point)) {
            ChessPiece target = model.getChessPieceAt(point);
            push(selectedPoint,point,target);
            if(mode!=0)  assume();
            concludeMove(selectedPoint,point,target);
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
            } else if(point!=null&&model.getChessPieceAt(selectedPoint).canCapture(target)&&model.isValidCapture(view,selectedPoint,point)){
                push(selectedPoint,point,target);
                if(mode!=0)  assume();
                concludeMove(selectedPoint,point,target);
            }
            view.repaint();
        }
    }
}
