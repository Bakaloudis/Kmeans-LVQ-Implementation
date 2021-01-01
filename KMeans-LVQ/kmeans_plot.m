centers  = load ("KMeans_Centers.txt");
clusters = load("KMeans_Clusters.txt");
plot (centers (:,1), centers (:,2),'*',clusters(:,1),clusters(:,2),'+');



