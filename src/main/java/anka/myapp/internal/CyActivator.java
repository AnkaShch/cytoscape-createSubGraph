package anka.myapp.internal;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.osgi.framework.BundleContext;

import java.util.Properties;

public class CyActivator extends AbstractCyActivator {

    public static final String APPNAME = "SubGraph";
    private static CyAppAdapter appAdapter;
    private static CyEventHelper eventHelper;
    private static CyApplicationManager cyApplicationManager;
    private static CySwingApplication cyDesktopService;
    private static CyServiceRegistrar cyServiceRegistrar;
    private static CyNetworkFactory networkFactory;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewFactory networkViewFactory;
    private static CyNetworkViewManager networkViewManager;
    private CreateSubGraph subGraph;

    public CyActivator() {
        super();
    }

    @Override
    public void start(BundleContext context) {

        this.appAdapter = getService(context, CyAppAdapter.class);
        this.eventHelper = getService(context, CyEventHelper.class);
        this.cyApplicationManager = getService(context, CyApplicationManager.class);
        this.cyDesktopService = getService(context, CySwingApplication.class);
        this.cyServiceRegistrar = getService(context, CyServiceRegistrar.class);
        this.networkFactory = getService(context, CyNetworkFactory.class);
        this.networkManager = getService(context, CyNetworkManager.class);
        this.networkViewFactory = getService(context, CyNetworkViewFactory.class);
        this.networkViewManager = getService(context, CyNetworkViewManager.class);
        this.subGraph = new CreateSubGraph(cyApplicationManager, APPNAME);
        registerService(context, subGraph, CyAction.class, new Properties());

    }

    public static CySwingApplication getCyDesktopService(){
        return cyDesktopService;
    }

    public static CyServiceRegistrar getCyServiceRegistrar() {
        return cyServiceRegistrar;
    }

    public static CyApplicationManager getCyApplicationManager(){
        return cyApplicationManager;
    }

    public static CyNetworkManager getCyNetworkManager() {
        return networkManager;
    }

    public static CyNetworkViewFactory getCyNetworkViewFactory() {
        return networkViewFactory;
    }

    public static CyNetworkViewManager getCyNetworkViewManager() {
        return networkViewManager;
    }

    public static CyAppAdapter getCyAppAdapter() {
        return appAdapter;
    }
}
