package controller;


import listener.GameListener;
import model.Constant;
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
import java.util.Stack;

/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 *
*/
public class GameController implements GameListener {

    private Chessboard model;
    private ChessboardComponent view;
    private PlayerColor currentPlayer;

    // Record whether there is a selected piece before
    private ChessboardPoint selectedPoint;
    private boolean over;
    private int redCount,blueCount;
    private Stack<Object[]> stack = new Stack();
    public GameController(ChessboardComponent view, Chessboard model,boolean isLoad) throws IOException {
        this.view = view;
        this.model = model;
        this.currentPlayer = PlayerColor.BLUE;
        view.registerController(this);
        initialize();
        if(isLoad){
            load();
        }
        else{
            view.initiateChessComponent(model);
            view.repaint();
        }
    }
    public Chessboard getModel(){
        return model;
    }
    public ChessboardComponent getView(){
        return view;
    }
    public PlayerColor getCurrentPlayer(){
        return currentPlayer;
    }
    public int getRedCount(){
        return redCount;
    }

    public Stack getStack() {
        return stack;
    }
    public Object[] getStack(int n) {
        return stack.get(n);
    }

    public int getBlueCount(){
        return blueCount;
    }
    private void initialize() {
        redCount = 8;
        blueCount = 8;
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {

            }
        }
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
    }

    private void win(ChessboardPoint point) {
        System.out.printf("(blue:%d red:%d)\n",blueCount,redCount);
        if(view.denCell.contains(point)||blueCount==0||redCount==0){
            if(currentPlayer == PlayerColor.BLUE||redCount==0) JOptionPane.showMessageDialog(view, "Blue player wins!");
            else if(view.denCell.contains(point)||blueCount==0) JOptionPane.showMessageDialog(view, "Red player wins!");
            over = true;
        }
    }
    public void save() throws IOException {
        File save = new File("save.txt");
        if(!save.exists()){
            save.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("save.txt"));
        bw.write(getBlueCount()+" ");
        bw.write(getRedCount()+"\n");
        bw.write(getCurrentPlayer()+"\n");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint temp = new ChessboardPoint(i,j);
                if(getModel().getChessPieceAt(temp)!=null){
                    bw.write(getModel().getChessPieceAt(temp).getOwner()+" ");
                    bw.write(getModel().getChessPieceAt(temp).getName()+" ");
                    bw.write(getModel().getChessPieceAt(temp).getRank()+" ");
                    bw.write(i+" ");
                    bw.write(j+"\n");
                }
            }
        }
        bw.write(getStack().size() + "\n");
        for (int i = 0; i < getStack().size(); i++) {
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
            new Point(row, col);
            model.setGrid(row, col, Integer.parseInt(argument[2]), argument[0], argument[1]);
            ChessPiece chessPiece = model.getGrid()[row][col].getPiece();
        }
        getView().initiateChessComponent(model);
        int n = Integer.parseInt(br.readLine());
        for (int i = 0; i < n; i++) {
            String temp = br.readLine();
            System.out.println(temp);
            String[] argument = temp.split(" +");
            ChessboardPoint des = new ChessboardPoint(Integer.parseInt(argument[0]),Integer.parseInt(argument[1]));
            ChessboardPoint src = new ChessboardPoint(Integer.parseInt(argument[2]),Integer.parseInt(argument[3]));
            currentPlayer = argument[4].equals("BLUE")?PlayerColor.BLUE:PlayerColor.RED;
            ChessPiece chess = argument[4].equals("null")?null:new ChessPiece(currentPlayer,argument[5],Integer.parseInt(argument[6]));
            push(des,src,chess);
        }
        getView().repaint();
        br.close();
    }

    public void push(ChessboardPoint des,ChessboardPoint src, ChessPiece chess){
        Object[] ob = new Object[]{des,src,chess};
        System.out.println(stack.size());
        stack.push(ob);
    }
    public void undo(){
        if(stack.empty()) return;
        Object[] ob = stack.pop();
        ChessboardPoint des = (ChessboardPoint)ob[0];
        ChessboardPoint src = (ChessboardPoint)ob[1];
        ChessPiece chess = (ChessPiece)ob[2];
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
    }
    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        if (!over&&selectedPoint != null && model.isValidMove(view,selectedPoint,point)) {
            if(view.trapCell.contains(point)){
                model.getChessPieceAt(selectedPoint).setRank(0);
            }
            else if(view.trapCell.contains(selectedPoint)){
                model.getChessPieceAt(selectedPoint).setRank();
            }
            push(selectedPoint,point,model.getChessPieceAt(point));
            model.moveChessPiece(selectedPoint, point);
            view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
            selectedPoint = null;
            view.repaint();
            win(point);
            swapColor();
            // TODO: if the chess enter Dens or Traps and so on
        }
    }

    @Override
    public void onPlayerClickChessPiece(ChessboardPoint point, ChessComponent component) {
        if(!over){
            if (selectedPoint == null) {
                if (model.getChessPieceOwner(point).equals(currentPlayer)) {
                    selectedPoint = point;
                    component.setSelected(true);
                    component.repaint();
                }
            } else if (selectedPoint.equals(point)) {
                selectedPoint = null;
                component.setSelected(false);
                component.repaint();
            }
            if(point!=null&&selectedPoint!=null&&model.getChessPieceAt(selectedPoint).canCapture(model.getChessPieceAt(point))&&model.isValidCapture(view,selectedPoint,point)){
                if(view.trapCell.contains(point)){
                    model.getChessPieceAt(selectedPoint).setRank(0);
                }
                else if(view.trapCell.contains(selectedPoint)){
                    model.getChessPieceAt(selectedPoint).setRank();
                }
                if(model.getChessPieceAt(point).getOwner().equals(PlayerColor.RED))redCount--;
                else blueCount--;
                push(selectedPoint,point,model.getChessPieceAt(point));
                model.captureChessPiece(view,selectedPoint,point);
                view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
                selectedPoint = null;
                view.repaint();
                win(point);
                swapColor();
            }
            view.repaint();
        }
    }
}
