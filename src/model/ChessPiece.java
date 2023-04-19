package model;


public class ChessPiece {
    // the owner of the chess
    private PlayerColor owner;

    // Elephant? Cat? Dog? ...
    private String name;
    private int rank;
    private int saveRank;

    public ChessPiece(PlayerColor owner, String name, int rank) {
        this.owner = owner;
        this.name = name;
        this.rank = rank;
        this.saveRank = rank;
    }

    public boolean canCapture(ChessPiece target) {
        if(!target.owner.getColor().equals(this.owner.getColor())){
            System.out.printf("(piece rank:%d target rank:%d)\n",this.rank,target.rank);
            if(target.rank==8&&this.rank==1){
                return true;
            }
            if((target.rank<=this.rank&&!(this.rank==8&&target.rank==1))){
                return true;
            }
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
    public void setRank(int rank) {
        this.saveRank = this.rank;
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
