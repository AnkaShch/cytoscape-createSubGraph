package anka.myapp.internal;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static anka.myapp.internal.JNICreateRandomSubGraph.getSubGraphNodes;

public class CreateSubGraph extends AbstractCyAction {

    CyNetwork currentnetwork;
    CyNetworkView currentnetworkview;
    String currentNetworkName;
    CyApplicationManager applicationManager;

    public CreateSubGraph(CyApplicationManager applicationManager, String appname) {
        super("Create Random Sub Network");
        setPreferredMenu("Apps");

        this.applicationManager = applicationManager;
        this.currentNetworkName = appname;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.currentnetwork = applicationManager.getCurrentNetwork();
        this.currentnetworkview = applicationManager.getCurrentNetworkView();

        List<CyNode> nodeList =  new ArrayList<CyNode>(currentnetwork.getNodeList());
        List<CyEdge> edgeList = new ArrayList<CyEdge>(currentnetwork.getEdgeList());
        CyTable nTable = currentnetwork.getDefaultNodeTable();
        CyTable eTable = currentnetwork.getDefaultEdgeTable();
        unselectAll(nTable, eTable, nodeList, edgeList);

        ArrayList<String> nodeListForCPP = new ArrayList<>(listNodesToStringList(nodeList));
        ArrayList<String> edgeListForCpp = new ArrayList<>(listEdgesToStringList(edgeList));

        ArrayList<String> subGraphFromCPP = new ArrayList<>(getSubGraphNodes(nodeListForCPP, edgeListForCpp));

        List<CyNode> requiredNodes = new ArrayList<CyNode>(createFromListString(subGraphFromCPP));

        List<CyEdge> requiredEdges = new ArrayList<CyEdge>(findNeighbourEdges(edgeList, requiredNodes));
        select(nTable, eTable, requiredNodes, requiredEdges);
        createSubNetwork(requiredNodes, requiredEdges);

    }

     private ArrayList<String> listNodesToStringList(List<CyNode> oldList) {
        ArrayList<String> l = new ArrayList<String>();
        for (CyNode elm: oldList) {
            l.add(String.valueOf(elm.getSUID()));
        }
        return l;
    }

    private ArrayList<String> listEdgesToStringList(List<CyEdge> oldList) {
        ArrayList<String> l = new ArrayList<String>();
        for (CyEdge elm : oldList) {
            CyNode u = elm.getSource(), v = elm.getTarget();
            l.add(String.valueOf(u.getSUID()) + " " + String.valueOf(v.getSUID()));
        }
        return l;
    }

    private List<CyNode> createFromListString(ArrayList<String> nodes) {
        List<CyNode> l = new ArrayList<CyNode>();
        for (String node : nodes) {
            l.add(currentnetwork.getNode(Long.parseLong(node)));
        }
        return l;
    }

    public void printListEdges(List<CyEdge> edgeList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("listEdges.txt"));
        for (CyEdge edge : edgeList) {
            CyNode u = edge.getSource(), v = edge.getTarget();
            writer.write(String.valueOf(u.getSUID()) + " " + String.valueOf(v.getSUID()) +"\n");
        }
        writer.close();
    }

    public List<CyEdge> findNeighbourEdges(List<CyEdge> edgeList, List<CyNode> neighbourNodes) {
        List<CyEdge> neighbourEges = new ArrayList<CyEdge>();
        for(CyEdge e : edgeList) {
            if(neighbourNodes.contains(e.getSource()) && neighbourNodes.contains(e.getTarget()))
                neighbourEges.add(e);
        }
        return neighbourEges;
    }

    public void unselectAll(CyTable nTable, CyTable eTable, List<CyNode> nodeList, List<CyEdge> edgeList){
        for(CyEdge e : edgeList){
            CyRow row = eTable.getRow(e.getSUID());
            row.set("selected", false);
        }
        for(CyNode n : nodeList){
            CyRow row = nTable.getRow(n.getSUID());
            row.set("selected", false);
        }
    }

    public void select(CyTable nTable, CyTable eTable, List<CyNode> nList, List<CyEdge> eList){
        for(CyEdge e : eList){
            CyRow row = eTable.getRow(e.getSUID());
            row.set("selected", true);
        }
        for(CyNode n : nList){
            CyRow row = nTable.getRow(n.getSUID());
            row.set("selected", true);
        }
    }

    public void createSubNetwork(List<CyNode> requiredNodes, List<CyEdge> requiredEdges) {

        CyRootNetwork root = ((CySubNetwork)currentnetwork).getRootNetwork();
        CyNetwork subNetwork = root.addSubNetwork(requiredNodes, requiredEdges);
        String networkName = currentNetworkName + " " + requiredNodes.size();
        subNetwork.getRow(subNetwork).set(CyNetwork.NAME, networkName);
        CyActivator.getCyNetworkManager().addNetwork(subNetwork);

        CyNetworkView subNetView = CyActivator.getCyNetworkViewFactory().createNetworkView(subNetwork);
        CyActivator.getCyNetworkViewManager().addNetworkView(subNetView);
        updateView(currentnetworkview, subNetView, "force-directed");
    }

    public static void updateView(CyNetworkView origNetView, CyNetworkView view, String layoutAlgorName){
        CyAppAdapter appAdapter = CyActivator.getCyAppAdapter();
        final CyLayoutAlgorithmManager alMan = appAdapter.getCyLayoutAlgorithmManager();
        CyLayoutAlgorithm algor = null;
        if (layoutAlgorName == null) {
            algor = alMan.getDefaultLayout(); // default grid layout
        } else{
            algor = alMan.getLayout(layoutAlgorName);
        }
        if(algor == null){
            algor = alMan.getDefaultLayout();
            throw new IllegalArgumentException ("No such algorithm found '" + layoutAlgorName + "'.");
        }
        TaskIterator itr = algor.createTaskIterator(view,algor.createLayoutContext(),CyLayoutAlgorithm.ALL_NODE_VIEWS,null);
        appAdapter.getTaskManager().execute(itr);// We use the synchronous task manager otherwise the visual style and updateView() may occur before the view is relayed out:
        SynchronousTaskManager<?> synTaskMan = appAdapter.getCyServiceRegistrar().getService(SynchronousTaskManager.class);
        synTaskMan.execute(itr);
        view.updateView(); // update view layout part
        appAdapter.getVisualMappingManager().getVisualStyle(origNetView).apply(view); // update view style part
    }

}
