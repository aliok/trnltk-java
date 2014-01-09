% M = dlmread("/Users/ali/Desktop/devl-data/trnltk/largefiles/histograms/wordHistogram-a.txt", " ");
M = dlmread("/Users/ali/Desktop/devl-data/trnltk/largefiles/wordCounts.txt", " ");

counts = M(:,2);
hist(counts,1:100);