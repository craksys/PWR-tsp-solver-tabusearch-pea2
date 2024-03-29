package com.company;

import java.util.Random;

public class TabuSearch {
    private static long millisActualTime; //zmienne do pomiaru czasu
    private static long executionTime;
    private static long bestSolutionTime;
    private final Graph graph; //graf
    public int[] bestPath; //najlepsza ścieżka
    public int bestCost = Integer.MAX_VALUE; // najlepszy koszt
    Random rand = new Random();
    private final long timeLimit;
    private final int neighbor;
    public TabuSearch(Graph graph, long timeLimit, int neighbor) {
        this.graph = graph;
        this.timeLimit = timeLimit;
        this.neighbor = neighbor;
    }

    public void solve() { //funkcja rozwiązująca tabu search
        int[] currentPath;
        int[] nextPath;
        int[][] tabuMatrix = new int[graph.matrix.length][graph.matrix.length];
        int iterations = 10 * graph.matrix.length; //liczba iteracji to 10 krotnosc liczby miast
        int nextCost;
        int currentCost;
        int[] savePath;
        bestCost = Integer.MAX_VALUE;
        millisActualTime = System.currentTimeMillis();
        while (true) {
            currentPath = generateRandomPath();
            nextPath = currentPath;
            currentCost = calculatePathCost(currentPath);
            for (int a = 0; a < iterations; a++) {
                currentPath = nextPath.clone();
                nextCost = currentCost;
                savePath = currentPath.clone();//zachowanie ścieżki
                for (int i = 1; i < graph.matrix.length; i++) {
                    for (int j = i + 1; j < graph.matrix.length; j++) {
                        if (neighbor == 1) {
                            swap(i, j, currentPath);
                        } else if (neighbor == 2) {
                            insertv2(i, j, currentPath);
                        } else if (neighbor == 3) {
                            makeReverse(i, j, currentPath);
                        }

                        currentCost = calculatePathCost(currentPath);

                        if ((currentCost < bestCost)) {
                            bestPath = currentPath.clone();
                            bestCost = currentCost;
                            bestSolutionTime = System.currentTimeMillis() - millisActualTime;
                            if ((tabuMatrix[i][j] == 0)) {
                                nextCost = currentCost;
                                nextPath = currentPath.clone();
                            }
                        }
                        if ((currentCost < nextCost) && (tabuMatrix[i][j] < a)) {
                            nextCost = currentCost;
                            nextPath = currentPath.clone();
                            tabuMatrix[i][j] += graph.matrix.length;
                        }
                        currentPath = savePath.clone(); // przywrocenie sciezki
                    }
                }

                for (int x = 0; x < graph.matrix.length; x++) { //dekrementacja o 1
                    for (int y = 0; y < graph.matrix.length; y++) {
                        if (tabuMatrix[x][y] > 0) {
                            tabuMatrix[x][y] -= 1;
                        }
                    }
                }

                executionTime = System.currentTimeMillis() - millisActualTime;
                if (executionTime > timeLimit) { //minuta przerwy
                    System.out.println(bestCost);
                    for (int j : bestPath) {
                        System.out.print(j + " ");
                    }
                    System.out.println("0");
                    System.out.println("Najlepsze rozwiązanie znaleziono w: " + bestSolutionTime + " ms");
                    return;
                }
            }
        }
    }

    private void random() { //funkcja testująca losowe ścieżki - do testów
        millisActualTime = System.currentTimeMillis();
        int[] currentPath;
        int currentCost;
        while (true) {
            currentPath = generateRandomPath();
            currentCost = calculatePathCost(currentPath);
            if ((currentCost < bestCost) /*&& (tabuMatrix[i][j] == 0)*/) {
                bestPath = currentPath.clone();
                bestCost = currentCost;
            }
            executionTime = System.currentTimeMillis() - millisActualTime;
            if (executionTime > timeLimit) { //minuta przerwy
                System.out.println(bestCost);
                for (int j : bestPath) {
                    System.out.print(j + " ");
                }
                System.out.println("0");
                System.out.println("Najlepsze rozwiązanie znaleziono w: " + bestSolutionTime + " ms");
                return;
            }
        }
    }

    private int[] generateRandomPath() { //generowanie losowej ścieżki
        int[] randomPath = new int[graph.matrix.length];
        for (int i = 0; i < graph.matrix.length; i++) {
            randomPath[i] = i;
        }
        for (int i = 1; i < randomPath.length; i++) {//funkcja losująca kolejność
            int randomIndexToSwap = rand.nextInt(randomPath.length - 1) + 1;//wszystkie oprocz 0
            int temp = randomPath[randomIndexToSwap];
            randomPath[randomIndexToSwap] = randomPath[i];
            randomPath[i] = temp;
        }

        return randomPath;
    }

    private int calculatePathCost(int[] path) {
        int cost = 0;
        for (int i = 0; i < path.length - 1; i++) {
            cost += graph.matrix[path[i]][path[i + 1]];
        }
        cost += graph.matrix[(path[(path.length - 1)])][path[0]];
        return cost;
    }

    private void swap(int i, int j, int[] path) {
        int temp = path[i];
        path[i] = path[j];
        path[j] = temp;
    }


    private void makeReverse(int i, int j, int[] path) {
        int temp;
        while (i < j) {
            temp = path[i];
            path[i] = path[j];
            path[j] = temp;
            i++;
            j--;
        }
    }

    private void insert(int i, int j, int[] path) { //błędna funkcja
        int temp = path[i];
        path[i] = path[j];

        while (j < path.length - 1) {
            path[j] = path[j + 1];
            j++;
        }
        path[(path.length - 1)] = temp;
    }

    private void insertv2(int i, int j, int[] path) {
        int[] tempTab = new int[path.length];
        int x;
        for (x = 0; x < i; x++) {
            tempTab[x] = path[x];
        }
        tempTab[i] = path[j];
        for (x = x + 1; x < j + 1; x++) {
            tempTab[x] = path[x - 1];
        }

        for (; x < path.length; x++) {
            tempTab[x] = path[x];
        }

        System.arraycopy(tempTab, 0, path, 0, path.length);
    }

}
