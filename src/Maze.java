import java.util.*;

/** Break walls with DSU; minimum-cost spanning tree by Kruskal and Prim. */
public class Maze {
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int p = 0;
        for (int i = 0; i < w.count(); i++) {
            if (dsu.union(w.a()[i], w.b()[i])) result[p++] = i;
        }
        return Arrays.copyOf(result, p);
    }
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> w.cost()[i]));
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] result = new int[Math.max(0, w.cells() - 1)];
        int p = 0;
        for (int edge : order) {
            if (dsu.union(w.a()[edge], w.b()[edge])) result[p++] = edge;
            if (p == result.length) break;
        }
        return Arrays.copyOf(result, p);
    }
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n == 0) return 0;
        int[] degree = new int[n];
        for (int i = 0; i < w.count(); i++) { degree[w.a()[i]]++; degree[w.b()[i]]++; }
        int[][] incident = new int[n][];
        for (int i = 0; i < n; i++) incident[i] = new int[degree[i]];
        int[] usedSlots = new int[n];
        for (int i = 0; i < w.count(); i++) {
            incident[w.a()[i]][usedSlots[w.a()[i]]++] = i;
            incident[w.b()[i]][usedSlots[w.b()[i]]++] = i;
        }
        boolean[] seen = new boolean[n];
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> w.cost()[i]));
        seen[0] = true;
        for (int e : incident[0]) pq.add(e);
        int reached = 1;
        long total = 0;
        while (reached < n && !pq.isEmpty()) {
            int e = pq.poll(), a = w.a()[e], b = w.b()[e];
            if (seen[a] && seen[b]) continue;
            int next = seen[a] ? b : a;
            seen[next] = true;
            reached++;
            total += w.cost()[e];
            for (int edge : incident[next]) pq.add(edge);
        }
        return total;
    }
}
