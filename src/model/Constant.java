package model;

public enum Constant {
    CHESSBOARD_ROW_SIZE(9),CHESSBOARD_COL_SIZE(7),
    NONE(0),EASY(5),NORMAL(6),DIFFICULT(8);
    private final int num;
    Constant(int num){
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
