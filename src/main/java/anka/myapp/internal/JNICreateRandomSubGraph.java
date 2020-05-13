package anka.myapp.internal;

import org.scijava.nativelib.NativeLoader;

import java.io.IOException;
import java.util.ArrayList;

public class JNICreateRandomSubGraph {
    public static native ArrayList<String> getSubGraphNodes(ArrayList<String> nodes, ArrayList<String> edges);

    static {
        //System.load("/home/anka/practic/CreatorRandomSubGraph/lib/randSubGraph.so");
        try {

            NativeLoader.loadLibrary("randSubGraph");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
