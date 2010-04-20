/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pluginscore;

import java.io.File;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import it.intecs.pisa.common.tbx.Interface;
import java.util.Set;
import org.apache.commons.collections.map.MultiKeyMap;

/**
 *
 * @author Massimiliano
 */
public class InterfacePluginManager extends ToolboxPluginManager {

    public static final String EXTENSION_TYPE_INTERFACEPLUGIN = "interfacePlugin";
    protected Interface[] interfaces = new Interface[0];
    protected Interface outOfRepo;
    protected MultiKeyMap interfaceRepoDir;
    private static InterfacePluginManager manager = new InterfacePluginManager();

    @Override
    protected void handleTag(File dir, Element el) {
        int parsedInterfaces = 0;
        Interface[] newInterfaces = null;
        Interface interf;
        File completePath;

        if(interfaceRepoDir==null)
                interfaceRepoDir=new MultiKeyMap();

        completePath = new File(dir, el.getAttribute("descriptionFile"));

        if (el.getAttribute("name").equals("")) {
            outOfRepo = new Interface();
            outOfRepo.initFromFile(completePath);
        } else {
            parsedInterfaces = interfaces.length;
            newInterfaces = new Interface[parsedInterfaces + 1];

            for (int i = 0; i < parsedInterfaces; i++) {
                newInterfaces[i] = interfaces[i];
            }

            interf= new Interface();
            interf.initFromFile(completePath);

            newInterfaces[parsedInterfaces] = interf;
            
            interfaceRepoDir.put(interf.getName(),interf.getVersion(),interf.getType(),interf.getMode(), dir);

            interfaces = newInterfaces;
        }
    }

    public Interface getOutOfRepoInterfaceDescription() {
        return outOfRepo;
    }

    public Interface[] getInterfaces() {
        return this.interfaces;
    }

    public Interface[] getInterfaces(String serviceType) throws Exception {
        Interface[] typedInterfaces;
        int matchingInterface = 0;
        int i = 0;
        String type;

        for (Interface interf : interfaces) {
            type = interf.getType();
            if (type.equals(serviceType)) {
                matchingInterface++;
            }
        }


        typedInterfaces = new Interface[matchingInterface];
        for (Interface interf : interfaces) {
            type = interf.getType();
            if (type.equals(serviceType)) {
                typedInterfaces[i] = (Interface) interf.clone();
                i++;
            }
        }

        return typedInterfaces;
    }

    public String[][] getInterfacesInfos(String serviceType) throws Exception {
        Interface[] typedInterfaces;
        Interface interfac;
        String[][] interfNames;
        int i = 0;
        Hashtable<String,Interface> namesHash;
        String name;
        Set<String> keys;

        typedInterfaces = getInterfaces(serviceType);
        namesHash = new Hashtable<String, Interface>();

        for (Interface interf : typedInterfaces) {
            name = interf.getMode();
            if (name != null && name.equals("") == false) {
                namesHash.put(interf.getName()+" "+interf.getVersion(), interf);
            }
        }

        keys = namesHash.keySet();
        interfNames = new String[keys.size()][2];
        for (Object o : keys.toArray()) {
            //interfNames[i] = new String[2];
            interfac=(Interface) namesHash.get((String)o);
            interfNames[i][0]=new String(interfac.getName());
            interfNames[i][1]=new String(interfac.getVersion());
            i++;
        }


        return interfNames;
    }

    public boolean hasInterfaceDescription(String name, String version, String type, String mode) {
        for (Interface interf : interfaces) {
            if (interf.getName().equals(name) && interf.getVersion().equals(version) &&
                    interf.getType().equals(type) && interf.getMode().equals(mode)) {
                return true;
            }
        }

        return false;
    }

    public File getInterfaceDescriptionPluginDirectory(String name, String version, String type, String mode)
    {
        return (File) this.interfaceRepoDir.get(name,version,type,mode);
    }


    public Interface[] getInterfacesDescription(String name, String version, String type) throws Exception {
        int elemCount = 0;
        int i = 0;
        Interface[] matchingInterfaces;

        for (Interface interf : interfaces) {
            if (interf.getName().equals(name) && interf.getVersion().equals(version) &&
                    interf.getType().equals(type)) {
                elemCount++;
            }
        }

        matchingInterfaces = new Interface[elemCount];
        for (Interface interf : interfaces) {
            if (interf.getName().equals(name) && interf.getVersion().equals(version) &&
                    interf.getType().equals(type)) {
                matchingInterfaces[i] = (Interface) interf.clone();
                i++;
            }
        }
        return matchingInterfaces;
    }

    public Interface getInterfaceDescription(String name, String version, String type, String mode) throws Exception {

        for (Interface interf : interfaces) {
            if (interf.getName().equals(name) && interf.getVersion().equals(version) &&
                    interf.getType().equals(type) && interf.getMode().equals(mode)) {
                return (Interface) interf.clone();
            }
        }

        return null;
    }

    public Interface getInterfaceDescription(Interface implInterface) throws Exception {
        String name, version, type, mode;

        name=implInterface.getName();
        version=implInterface.getVersion();
        type=implInterface.getType();
        mode=implInterface.getMode();
        
        return getInterfaceDescription(name, version, type, mode);
    }

    @Override
    protected boolean isTagHandled(String tagname) {
        return tagname.equals(EXTENSION_TYPE_INTERFACEPLUGIN);
    }

    public static InterfacePluginManager getInstance() {
        return manager;
    }

    public File getSchemaDirForInterface(String interfaceName, String interfaceVersion, String interfaceType, String interfaceMode) throws Exception {
        Interface interfaceDescription;
        File schemaDir;
        File interfaceDir;

        interfaceDescription = getInterfaceDescription(interfaceName, interfaceVersion, interfaceType, interfaceMode);
        interfaceDir=getInterfaceDescriptionPluginDirectory(interfaceName, interfaceVersion, interfaceType, interfaceMode);

        schemaDir = new File(interfaceDir, interfaceDescription.getSchemaDir());
        return schemaDir;
    }

    public String[] getInterfacesModes(String name, String version, String type) {
        try {
            Interface[] matchingInterfaces;
            String[] modes;
            String mode;
            Set<String> keys;
            Hashtable<String, String> modesHash;
            int i = 0;

            if (this.interfaces == null) {
                return new String[0];
            }

            matchingInterfaces = this.getInterfacesDescription(name, version, type);
            modesHash = new Hashtable<String, String>();

            for (Interface interf : matchingInterfaces) {
                mode = interf.getMode();
                if (mode != null && mode.equals("") == false) {
                    modesHash.put(mode, "");
                }
            }
            //modes=new String[typesHash.size()];
            keys = modesHash.keySet();
            modes = new String[keys.size()];
            for (Object o : keys.toArray()) {
                modes[i] = (String) o;
                i++;
            }
            return modes;
        } catch (Exception ex) {
            Logger.getLogger(InterfacePluginManager.class.getName()).log(Level.SEVERE, null, ex);
            return new String[0];
        }
    }

    public String[] getInterfacesTypes() {
        Hashtable<String, String> typesHash;
        String type;
        Set<String> keys;
        String[] types;
        int i = 0;

        if (this.interfaces == null) {
            return new String[0];
        }

        typesHash = new Hashtable<String, String>();

        for (Interface interf : interfaces) {
            type = interf.getType();
            if (type != null && type.equals("") == false) {
                typesHash.put(type, "");
            }
        }

        //modes=new String[typesHash.size()];
        keys = typesHash.keySet();
        types = new String[keys.size()];
        for (Object o : keys.toArray()) {
            types[i] = (String) o;
            i++;
        }

        return types;
    }
}
