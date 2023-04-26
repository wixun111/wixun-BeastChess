package model;


import java.io.Serializable;

public class ChessPiece implements Serializable,Cloneable {
    // the owner of the chess
    private final PlayerColor owner;

    // Elephant? Cat? Dog? ...
    private final String name;
    private int rank;
    private int saveRank;
    private int value;

    public ChessPiece(PlayerColor owner, String name, int rank) {
        this.owner = owner;
        this.name = name;
        this.rank = rank;
        this.saveRank = rank;
        this.value = switch (name) {
            case "Elephant" -> 2300;
            case "Lion" -> 1500;
            case "Tiger" -> 1300;
            case "Leopard" -> 1000;
            case "Wolf","Rat" -> 800;
            case "Dog"  -> 700;
            case "Cat" -> 400;
            default -> 0;
        };
    }


    public boolean canCapture(ChessPiece target) {
        if(!target.owner.getColor().equals(this.owner.getColor())){
            if(target.rank==8&&this.rank==1){
                return true;
            }
            return target.rank <= this.rank && !(this.rank == 8 && target.rank == 1);
        }
        return false;
    }

    public String getName() {
        return name;
    }
    public int getRank(){
        return rank;
    }

    public PlayerColor getOwner() {
        return owner;
    }

    public int getValue() {
        return value;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    public void setRank() {
        this.rank = this.saveRank;
    }
    @Override
    public String toString() {
        return name;
    }
}
