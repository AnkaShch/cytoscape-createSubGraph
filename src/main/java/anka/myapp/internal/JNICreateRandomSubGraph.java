package anka.myapp.internal;

import java.util.ArrayList;

public class JNICreateRandomSubGraph {
    public static native ArrayList<String> getSubGraphNodes(ArrayList<String> nodes, ArrayList<String> edges);

    static {
        System.load("/home/anka/practic/CreatorRandomSubGraph/lib/randSubGraph.so");
    }
}
