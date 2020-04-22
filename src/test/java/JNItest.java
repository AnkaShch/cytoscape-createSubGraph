import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static anka.myapp.internal.JNICreateRandomSubGraph.getSubGraphNodes;

public class JNItest {
    public static void main(String[] args) throws IOException {
        ArrayList<String> nodeList = new ArrayList<>(createListFromFile("src/test/resources/listNodes.txt"));
        ArrayList<String> edgeList = new ArrayList<>(createListFromFile("src/test/resources/listEdges.txt"));
        ArrayList<String> subGraph = new ArrayList<>(getSubGraphNodes(nodeList, edgeList));
        System.out.println("OK, it's subgraph (size " + subGraph.size() + ") :");
        printList(subGraph);
    }

    private static void printList (ArrayList<String> subGraph) {
        for (String v : subGraph) {
            System.out.println(v);
        }
    }

    private static ArrayList<String> createListFromFile(String filename) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String line = buf.readLine();
        while (line != null) {
            list.add(line);
            line = buf.readLine();
        }
        buf.close();
        return list;
    }
}
