import java.util.*;

/** Iterative depth-first traversal of the opened maze. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}
    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int[][] g = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbors : g) if (neighbors.length == 1) deadEnds++;
        if (n == 0) return new Result(new int[0], 0, deadEnds);
        boolean[] visited = new boolean[n];
        int[] parent = new int[n], direction = new int[n], stack = new int[n];
        Arrays.fill(parent, -1);
        int top = 0, totalVisited = 1;
        stack[0] = 0; visited[0] = true;
        while (top >= 0 && !visited[n - 1]) {
            int u = stack[top];
            if (direction[u] == 4) { top--; continue; }
            int dir = direction[u]++;
            int v = switch (dir) {
                case 0 -> u - cols;
                case 1 -> u + 1;
                case 2 -> u + cols;
                default -> u - 1;
            };
            if (v < 0 || v >= n || (dir == 1 && u % cols == cols - 1) || (dir == 3 && u % cols == 0) || visited[v]) continue;
            boolean isOpen = false;
            for (int other : g[u]) if (other == v) { isOpen = true; break; }
            if (!isOpen) continue;
            visited[v] = true; totalVisited++; parent[v] = u; stack[++top] = v;
        }
        if (!visited[n - 1]) return new Result(new int[0], totalVisited, deadEnds);
        int length = 0;
        for (int u = n - 1; u != -1; u = parent[u]) length++;
        int[] path = new int[length];
        for (int u = n - 1, i = length - 1; i >= 0; u = parent[u], i--) path[i] = u;
        return new Result(path, totalVisited, deadEnds);
    }
}
