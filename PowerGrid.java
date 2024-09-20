import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.lang.Math;

class PowerLine {
    String cityA;
    String cityB;

    public PowerLine(String cityA, String cityB) {
        this.cityA = cityA;
        this.cityB = cityB;
    }
}


class Node {
    String city;
    Integer vertex;
    Node parent;
    Boolean bridge;
    ArrayList<Node> children = new ArrayList<>();
    public Node(String city, Integer vertex){
        this.city = city;
        this.vertex= vertex;
        bridge = false;
    }
}
// Students can define new classes here

public class PowerGrid {
    int numCities;
    int numLines;
    String[] cityNames;
    PowerLine[] powerLines;
    ArrayList<ArrayList<String>> ad;
    Boolean[] visited;
    Integer[] low;
    Integer[] disc;
    int timer = 0;
    ArrayList<PowerLine> critical = new ArrayList<>();
    HashMap<String, Integer> map = new HashMap<>();
    HashMap<String, Node> treemap = new HashMap<>();
    Integer up[][];
    int N;
    int[] nob;


    Integer[] in;
    Integer[] out;
    Integer[] level;

    // Students can define private variables here

    public PowerGrid(String filename) throws Exception {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        numCities = Integer.parseInt(br.readLine());
        numLines = Integer.parseInt(br.readLine());
        cityNames = new String[numCities];
        for (int i = 0; i < numCities; i++) {
            cityNames[i] = br.readLine();
        }
        powerLines = new PowerLine[numLines];
        for (int i = 0; i < numLines; i++) {
            String[] line = br.readLine().split(" ");
            powerLines[i] = new PowerLine(line[0], line[1]);
        }

        // System.out.println(Arrays.toString(cityNames));
    
        N = (int)Math.ceil(Math.log(numCities)/Math.log(2));
        visited = new Boolean[numCities];
        disc = new Integer[numCities];
        low = new Integer[numCities];
        ad = new ArrayList<>();
        up = new Integer[numCities+1][N];
        nob = new int[numCities];
        nob[0] = 0;
        level = new Integer[numCities]; 
        // System.out.println(numCities+ " "+ numLines);


        for (int i  = 0 ; i<numCities ; i++){
            treemap.put(cityNames[i],new Node(cityNames[i],i));
            map.put(cityNames[i],i);
            ad.add(new ArrayList<>());
            visited[i] = false;
            // System.out.println(cityNames[i]);
        }
        for (int i = 0; i<numLines ;i++){
            int temp = map.get(powerLines[i].cityA);
            int tempe = map.get(powerLines[i].cityB);
            ad.get(temp).add(powerLines[i].cityB);
            ad.get(tempe).add(powerLines[i].cityA);
            // System.out.println(powerLines[i].cityA + " "+powerLines[i].cityB);
        }
        DFS(cityNames[0],null);

        // TO be completed by students
    }

    private int min(int a, int b){
        if (a<b){
            return a;
        }
        else{
            return b;
        }
    }

    public void DFS(String vertex , String p){
        // System.out.println("city = "+vertex);
        visited[map.get(vertex)] = true;
        timer++;
        disc[map.get(vertex)] = timer;
        low[map.get(vertex)] = timer;
        for (int i = 0 ; i <ad.get(map.get(vertex)).size(); i++){
            String k = ad.get(map.get(vertex)).get(i);
            if (k.equals(p)){
                continue;
            }
            if (visited[map.get(k)] == false){
                // up[map.get(k)][0] = map.get(vertex);
                treemap.get(vertex).children.add(treemap.get(k));
                DFS(k,vertex);
                low[map.get(vertex)] = min(low[map.get(k)],low[map.get(vertex)]);
                if (low[map.get(k)]>disc[map.get(vertex)]){
                    // vertex to k is a bridge edge
                    PowerLine temp = new PowerLine(vertex, k);
                    critical.add(temp);
                    treemap.get(k).bridge = true;
                }
                continue;
            }
            if (visited[map.get(k)] == true ){
                low[map.get(vertex)] = min(disc[map.get(k)],low[map.get(vertex)]);
                continue;
            }
        }
    }


    public ArrayList<PowerLine> criticalLines() {
        // System.out.println("critical lines");
        // for (int i = 0 ;i<critical.size();i++){
        //     System.out.println(critical.get(i).cityA + " " +critical.get(i).cityB);
        // }
        return critical;
        /*
         * Implement an efficient algorithm to compute the critical transmission lines
         * in the power grid.
         
         * Expected running time: O(m + n), where n is the number of cities and m is the
         * number of transmission lines.
         */

        // return new ArrayList<PowerLine>();
    }



    public void preDFS(String vertex , String p){
        level[map.get(vertex)] = level[map.get(p)]+1;
        up[map.get(vertex)][0] = map.get(p);
        if (treemap.get(vertex).bridge){
            nob[map.get(vertex)] = nob[map.get(p)] +1;
        }
        else{
            nob[map.get(vertex)] = nob[map.get(p)];
        }
        for (int j = 1; j < N ; j++){
            up[map.get(vertex)][j] = up[up[map.get(vertex)][j-1]][j-1];
        }
        for (int i = 0 ; i <treemap.get(vertex).children.size(); i++){
            String k = treemap.get(vertex).children.get(i).city;
            preDFS(k, vertex);
        }
    }

    public int abs (int a){
        if(a>=0){
            return a;
        }
        return -a;
    }

    public int lca(int u, int v){
        if (level[u]<level[v]){
            int temp = u;
            u = v;
            v = temp;
        }
        while(level[u] != level[v]){
            for(int i = 0; i<N+1 ; i++){
                if (Math.pow(2,i)>level[u]-level[v]){
                    u = up[u][i-1];
                    break;
                }
            }
        }
        if (u==v){
            return u;
        }
        for(int i = N-1; i>=0 ; i--){
            if (up[u][i] != up[v][i]){
                u = up[u][i];
                v = up[v][i];
            }
        }
        return up[u][0];
        
    }

    public void preprocessImportantLines() {
        // System.out.println("preprocess");
        timer = 0;
        in = new Integer[numCities];
        out = new Integer[numCities];
        level[0] = 0;
        for (int i = 0; i<numCities+1 ; i++){
            for (int j = 0 ; j<N;j++){
                up[i][j] = numCities+1;
            }
        }

        preDFS(cityNames[0], cityNames[0]);

        // for (int i = 0; i<numCities+1 ; i++){
        //     System.out.println(Arrays.toString(up[i]));
        // }
        // System.out.println(Arrays.toString(up));


        /*
         * Implement an efficient algorithm to preprocess the power grid and build
         * required data structures which you will use for the numImportantLines()
         * method. This function is called once before calling the numImportantLines()
         * method. You might want to define new classes and data structures for this
         * method.
         
         * Expected running time: O(n * logn), where n is the number of cities.
         */
        return;
    }

    public int numImportantLines(String cityA, String cityB) {
        // System.out.println("number of important lines "+ cityA + " "+cityB);
        int ancestor = lca(map.get(cityA), map.get(cityB));
        // System.out.println("nob is " + Arrays.toString(nob));
        // System.out.println("lca is pls god "+ ancestor);
        // System.out.println("lca is "+ancestor);

        // System.out.println(ancestor);
        /*
         * Implement an efficient algorithm to compute the number of important
         * transmission lines between two cities. Calls to numImportantLines will be
         * made only after calling the preprocessImportantLines() method once.
         
         * Expected running time: O(logn), where n is the number of cities.
         */
        return nob[map.get(cityA)] - 2*nob[ancestor] + nob[map.get(cityB)];
        // return 0;
    }

    // public static void main(String args[]){
    //     String s = "atharv.txt";
    //     try{
    //     PowerGrid dummy = new PowerGrid(s);
    //     // System.out.println(dummy.criticalLines());
    //     dummy.preprocessImportantLines();
    //     System.out.println(dummy.numImportantLines("G", "J"));
    //     System.out.println(dummy.numImportantLines("D", "E"));
    //     System.out.println(dummy.numImportantLines("H", "O"));

    //     }
    //     catch(FileNotFoundException e){
    //         System.out.println("File not found");
    //     }
    //     catch(Exception e){
    //         System.out.println(e);
    //     }
    // }
}