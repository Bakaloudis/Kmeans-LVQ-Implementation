package LVQ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import KMeans.Centroid;
import KMeans.Point;

public class LVQ {
	
	private static double total_dispersion = 0.0;
	private static double lowest_dispersion = 0.0;
	
	private static final int M = 5; 		// number of clusters
	private static final int t = 5; 		// number of seasons
	private static double n = 0.1;  		// learning rate
	
	private static ArrayList<Point> points_array = new ArrayList<>();				// array that holds the points that we created
	private static ArrayList<Centroid> centers_array = new ArrayList<>();			// array that holds the centers of our clustering
	private static ArrayList<ArrayList<Point>> clusters_array = new ArrayList<>();  // array that holds the clusters and their points
	private static ArrayList<Double> dispersions_array  = new ArrayList<>();		// array that holds the dispersion value for each cluster
	
	private static ArrayList<Centroid> previousCenters = new ArrayList<>();
    private static ArrayList<ArrayList<Point>> previousClusters = new ArrayList<>();
    
    public static void pointsFromFile() throws FileNotFoundException{  // reading the file that we created and extracting the points
    	
    	File file = new File("S2.txt");
        Scanner input = new Scanner(file); 
     
        while (input.hasNext()) {
            String x = input.next();  // x value
            String y = input.next();  // y value 
            Point point = new Point(Double.valueOf(x),Double.valueOf(y));
            points_array.add(point);
        }   
        input.close();
    }
    
    public static void randomCenters() {  		  // picking random points as centers, from the points we created
    	
    	for (int i=0; i < M; i++) {
    		int random_number = (int)(Math.random()*points_array.size());
    		Point p = points_array.remove(random_number);
    		Centroid center = new Centroid(p,i);
    		centers_array.add(center);
    	}
    }
    
    public static double eucl_distance(Point tmp_point,Point tmp_center) {  // computing the euclideian distance
    	
    	double dist_x = Math.pow(tmp_point.getX()-tmp_center.getX(),2);
    	double dist_y = Math.pow(tmp_point.getY()-tmp_center.getY(),2);
    	double dist = Math.sqrt(dist_x + dist_y);
    	
    	return dist;
    }
    
    public static Centroid bestFit(Point p) {  		// finding the closest center to a given point
    	
    	int closest_center = 0;
    	ArrayList<Double> distance_array = new ArrayList<>();
    	
    	for (int i=0 ; i < centers_array.size(); i++) {
    		distance_array.add(eucl_distance(p,centers_array.get(i).getWeight())); 
    	}
    	
    	double dis = distance_array.get(0);
    	for (int i=1 ; i < M; i++) {
    		if (distance_array.get(i) < dis ) {
    			dis = distance_array.get(i);
    			closest_center = i;
    		}
    	}
    	
    	return centers_array.get(closest_center);
    }
    
    public static void predict() {  			  // assigning all points to clusters based on the euclideian distance
    	
    	for (int i=0; i < M; i++) {
            ArrayList<Point> cluster = new ArrayList<>();
            clusters_array.add(cluster);
        }
    	
    	for (int i=0; i < points_array.size(); i++) {
    		Centroid winner = bestFit(points_array.get(i));
    		clusters_array.get(winner.getTeam()).add(points_array.get(i));
    	}

    }
    
    public static void train() {  				  // finding the best centers for our data 
    	
    	for(int i=0 ; i < t; i++) {
    		for(int j=0; j < points_array.size(); j++) {
    			Centroid winner = bestFit(points_array.get(i));
    			int winner_position = centers_array.indexOf(winner);
    			Point old_weight = winner.getWeight();
    			
    			double new_x = old_weight.getX() + n * (points_array.get(j).getX()-old_weight.getX());
    			double new_y = old_weight.getY() + n * (points_array.get(j).getY()-old_weight.getY());
    			
    			Point new_weight = new Point(new_x,new_y);
    			centers_array.get(winner_position).setWeight(new_weight);
    		}
    		
    		n-= 0.95*n;
    	}
		
	}
    
    public static void dispersion() {			  // calculating the dispersion value for each cluster using the euclideian distance
    	
    	for (int i = 0; i < clusters_array.size(); i++) {
            double dispersion = 0.0;
            ArrayList<Point> cluster = clusters_array.get(i);
            Centroid center = centers_array.get(i);

            for (int j=0; j < cluster.size(); j++) {
                dispersion += eucl_distance(cluster.get(j), center.getWeight());
            }
            
            dispersions_array.add(dispersion);
            
        }
    }
    
    
    public static void main(String[] args) throws IOException {
    	
    	for (int i=0; i < 5; i++) {  // we run our LVQ for 5 repetitions
    		
			 pointsFromFile();
	         randomCenters();
	         train();
	         predict();
	         dispersion();
	         
	         for (int j=0; j < dispersions_array.size();j++) {   // calculating the total dispersion for our clustering
	                total_dispersion += dispersions_array.get(j);
	         }
	         
	         System.out.println("Iteration number "  + (i+1) + " , Total Dispersion: " + total_dispersion);
	         
	         if (lowest_dispersion == 0.0 || lowest_dispersion > total_dispersion) {
	            	
	        	 lowest_dispersion = total_dispersion;  // update
	            
	        	 previousCenters.clear();
	        	 previousClusters.clear();

	        	 for (int x=0; x < centers_array.size(); x++) {   // saving the best centers
	        		 previousCenters.add(centers_array.get(x));
	        	 }
          
	        	 for (int z=0; z < clusters_array.size(); z++) {	 // saving the best clusters
	        		 previousClusters.add(clusters_array.get(z));
	        	 }
	         }
	         
	         points_array.clear();
	         centers_array.clear();
	         clusters_array.clear();
	         dispersions_array.clear();
	         
	         total_dispersion = 0.0;
	         
		 }
    	
		 System.out.println("\nBest dispersion : " + lowest_dispersion);
		 
		 File center_file = new File("LVQ_Centers.txt");
	     FileWriter center_writer = new FileWriter(center_file);
	     
	     for(int i=0; i < previousCenters.size(); i++) {
	            center_writer.write(previousCenters.get(i).getWeight().getX()+" "+previousCenters.get(i).getWeight().getY()+"\n");
	     }
	     
	     center_writer.close();
	     
	     File cluster_file = new File("LVQ_Clusters.txt");
	     FileWriter cluster_writer = new FileWriter(cluster_file);
	     
	     for (int i=0; i < previousClusters.size(); i++) {
	         for(int j=0; j < previousClusters.get(i).size(); j++) {
	             cluster_writer.write(previousClusters.get(i).get(j).getX()+" "+previousClusters.get(i).get(j).getY()+"\n");
	         }
	     }
	     
	     cluster_writer.close();
	     
	 }
}
