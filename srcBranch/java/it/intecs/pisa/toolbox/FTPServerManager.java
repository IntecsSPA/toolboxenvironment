/* 
 *
 * ****************************************************************************
 *  Copyright 2003*2008 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 *  File Name:         $RCSfile: FTPServerManager.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.toolbox;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.BaseUser;
import org.apache.ftpserver.usermanager.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.PropertiesUserManager;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.apache.ftpserver.usermanager.TransferRatePermission;
import org.apache.ftpserver.usermanager.WritePermission;
import org.apache.ftpserver.usermanager.WriteRequest;

public class FTPServerManager {

    private static final String ADMIN = "admin";
    private static final String RES = "res";
    private static final String CONF = "conf";
    private static final String FTPD_CONF = "ftpd.xml";
    private static final String USERS_CONF = "users.properties";
    private static FTPServerManager self;
    private File usersFile;
    private File confFile;
    private File rootDir;
    private FtpServer server;
    private PropertiesUserManager userManager;
    private String errorMessage;

    private FTPServerManager(String rootDir) throws UnknownHostException, IOException, Exception {
        createFtp(rootDir);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    synchronized public static FTPServerManager getInstance(String rootDir) throws UnknownHostException, IOException, Exception {
        if (self == null) {
            self = new FTPServerManager(rootDir);
        }
        return self;
    }

    synchronized public static FTPServerManager getInstance() {
        return self;
    }

    public String getPort() {
        return String.valueOf(server.getListener("default").getPort());
    }

    public String getServerHost() {
        return server.getListener("default").getServerAddress().getCanonicalHostName();
    }

    public void updatePort(String port) throws Exception {
        try
        {
        server.getListener("default").setPort(Integer.valueOf(port));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updatePassiveModePort(String ports) throws FileNotFoundException, Exception {
        try {
        if(ports!=null && ports.equals("")==false)
            server.getListener("default").getDataConnectionConfiguration().setPassivePorts(ports);
          } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void updateServerHost(String host) throws FileNotFoundException, Exception {
        try {
             if(host!=null && host.equals("")==false)
                server.getListener("default").setServerAddress(InetAddress.getByName(host));
             else server.getListener("default").setServerAddress(null);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void updateAdminSettings(String password, String homeDir) throws Exception {
        addUser(ADMIN, password, homeDir);
    }

   /* private static void checkFile(File file) throws IOException {
        if (!file.exists() || !file.canRead() || !file.canWrite()) {
            throw new IOException("Non existent or not accessible file: " + file.getAbsolutePath());
        }
    }*/

    /*private static void checkDir(File dir) throws IOException {
        checkFile(dir);
        if (!dir.isDirectory()) {
            throw new IOException("Non existent or not accessible file: " + dir.getAbsolutePath());
        }
    }*/

    public void addUser(String userName, String password, String root) throws Exception {
        if (userName == null || userName.length() == 0 || password == null || password.length() == 0) {
            return;
        }
        File newRoot = new File(root);
        if (!newRoot.exists() && !newRoot.mkdirs()) {
            return;
        }
        BaseUser user = userManager.doesExist(userName) ? (BaseUser) userManager.getUserByName(userName) : new BaseUser();
        user.setName(userName);
        user.setPassword(password);
        user.setHomeDirectory(root);
        userManager.save(user);
    }

    public void addUser(String userName, String password, String root, boolean writePermission) throws Exception {
        if (userName == null || userName.length() == 0 || password == null || password.length() == 0) {
            return;
        }
        File newRoot = new File(root);
        if (!newRoot.exists() && !newRoot.mkdirs()) {
            return;
        }
        BaseUser user = userManager.doesExist(userName) ? (BaseUser) userManager.getUserByName(userName) : new BaseUser();
        user.setName(userName);
        user.setEnabled(true);
        user.setPassword(password);
        user.setHomeDirectory(root);

        List<Authority> authorities = new ArrayList<Authority>();

        if (writePermission) {
            authorities.add(new WritePermission());
        }

        int maxLogin = 0;
        int maxLoginPerIP = 0;

        authorities.add(new ConcurrentLoginPermission(maxLogin, maxLoginPerIP));

        int uploadRate = 0;
        int downloadRate = 0;

        authorities.add(new TransferRatePermission(downloadRate, uploadRate));

        user.setAuthorities(authorities.toArray(new Authority[0]));

        userManager.save(user);
    }

    public User getUser(String userName) throws Exception {
        return userManager.getUserByName(userName);
    }

    public void removeUser(String userName) throws Exception {
        if (userManager.doesExist(userName)) {
            userManager.delete(userName);
//            userManager.reload();
        }
    }

    public boolean setWritePermission(String userName, boolean writePermission) throws Exception {
       
            BaseUser user = (BaseUser) getUser(userName);
            
            if(user!=null)
            {
            List<Authority> authorities = new ArrayList<Authority>();

            if (writePermission) {
                authorities.add(new WritePermission());
            }

            user.setAuthorities(authorities.toArray(new Authority[0]));

            userManager.save(user);
            return true;
        }
        return false;
    }

    public boolean doesUserHasWritePermission(String userName) throws Exception {
        return (this.getUser(userName).authorize(new WriteRequest()) != null);
    }

    public synchronized void startServer(String rootDir) throws Exception {
       if(server==null)
            createFtp(rootDir);
       
        server.start();
    }

    public synchronized void stopServer() {
       server.stop();
       server=null;
    }

    public boolean isServerRunning() {
        return server!=null && server.isStopped() == false;
    }

    public String[] getUsers() throws Exception {
        return userManager.getAllUserNames();
    }

    protected void createFtp(String rootDir) throws Exception {
        try {
            this.rootDir = new File(rootDir);
            confFile = new File(new File(new File(rootDir, RES), CONF), FTPD_CONF);
            String filePath = confFile.getAbsolutePath();
            XmlBeanFactory bf = new XmlBeanFactory(new FileSystemResource(filePath));

            if (bf.containsBean("server")) {
                server = (FtpServer) bf.getBean("server");
            } else {
                String[] beanNames = bf.getBeanNamesForType(FtpServer.class);
                if (beanNames.length == 1) {
                    server = (FtpServer) bf.getBean(beanNames[0]);
                } else if (beanNames.length > 1) {
                    server = (FtpServer) bf.getBean(beanNames[0]);
                } else {
                    throw new Exception("XML configuration does not contain a server configuration");
                }
            }

            usersFile = new File(new File(new File(rootDir, RES), CONF), USERS_CONF);
            userManager = new PropertiesUserManager();
            userManager.setPropFile(usersFile);
            //userManager.setFile(usersFile);
            userManager.configure();
            server.setUserManager(userManager);
            
           
        } catch (Exception e) {
            this.errorMessage = e.getLocalizedMessage();
            throw e;
        }
    }

}


