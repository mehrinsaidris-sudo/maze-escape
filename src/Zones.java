import java.util.*;

/** Strongly connected zones and shortest paths in the condensation DAG. */
public class Zones {
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s), in = Graphs.slidesInto(s);
        boolean[] visited = new boolean[n];
        int[] finish = new int[n], stack = new int[n], next = new int[n];
        int count = 0;
        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;
            int top = 0; stack[0] = start; visited[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[u] >= out[u].length) { finish[count++] = u; top--; continue; }
                int edge = out[u][next[u]++], v = s.to()[edge];
                if (!visited[v]) { visited[v] = true; stack[++top] = v; }
            }
        }
        int[] zone = new int[n]; Arrays.fill(zone, -1);
        int id = 0;
        for (int i = count - 1; i >= 0; i--) {
            int start = finish[i];
            if (zone[start] != -1) continue;
            int top = 0; stack[0] = start; zone[start] = id;
            while (top >= 0) {
                int u = stack[top--];
                for (int edge : in[u]) {
                    int v = s.from()[edge];
                    if (zone[v] == -1) { zone[v] = id; stack[++top] = v; }
                }
            }
            id++;
        }
        return zone;
    }
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        // Each slide is a candidate edge. Repeated edges are harmless for min and max relaxations.
        int[] counts = new int[zoneCount];
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a != b) counts[a]++;
        }
        int[][] edges = new int[zoneCount][];
        for (int i = 0; i < zoneCount; i++) edges[i] = new int[counts[i]];
        Arrays.fill(counts, 0);
        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a != b) edges[a][counts[a]++] = i;
        }
        long[] stages = new long[zoneCount], dist = new long[zoneCount];
        long INF = Long.MAX_VALUE / 4;
        Arrays.fill(stages, 1);
        Arrays.fill(dist, INF);
        dist[zoneOf[0]] = 0;
        for (int zone = 0; zone < zoneCount; zone++) {
            for (int edge : edges[zone]) {
                int to = zoneOf[s.to()[edge]];
                stages[to] = Math.max(stages[to], stages[zone] + 1);
                if (dist[zone] != INF) dist[to] = Math.min(dist[to], dist[zone] + s.time()[edge]);
            }
        }
        long longest = 0;
        for (long v : stages) longest = Math.max(longest, v);
        return new long[]{longest, dist[zoneOf[s.n() - 1]]};
    }
}
