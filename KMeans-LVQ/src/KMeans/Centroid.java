package KMeans;

public class Centroid {
	
    private Point weight;
    private int team;

    public Centroid (Point weight, int team) {
        
        this.weight = weight;
        this.team = team;
    }

    public Point getWeight() {
        return weight;
    }

    public void setWeight(Point weight) {
        this.weight = weight;
    }

    public int getTeam() {
        return team;
    }

}
