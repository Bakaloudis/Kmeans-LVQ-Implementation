centers  = load ("LVQ_Centers.txt");
clusters = load("LVQ_Clusters.txt");
plot (centers (:,1), centers (:,2),'*',clusters(:,1),clusters(:,2),'+');