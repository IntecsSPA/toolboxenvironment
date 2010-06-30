package it.intecs.pisa.gis.geoserver.rest;

import java.net.URL;

/**
 *
 * @author Andrea Marongiu
 */
public interface RestAPI {



    public void setGeoserverURL(String geoserverURL);

    public URL getCreateRasterURL(String workspaceName, String newStoreName, String newStoreType) throws Exception;

    public URL getCreateVectorURL(String workspaceName, String newStoreName, String newStoreType) throws Exception;

    public URL getStylesURL() throws Exception;

    public URL getCreateWorkspaceURL(String newWorkspaceName) throws Exception;

    public URL getWorkspacesURL() throws Exception;

    public URL getLayerURL(String layerName) throws Exception;

    public String[] getRasterSupportedFormats();

    public String[] getStyleSupportedFormats();

    public String[] getVectorSupportedFormats();

    public boolean isSupported(String format);

    public boolean isVectorFormat(String format);

    public boolean isRasterFormat(String format);

    public boolean isStyleFormat(String format);

}
