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
package it.intecs.pisa.archivingserver.services;

import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

public class FTPService {

    private static final String ADMIN = "admin";
    private static final String RES = "res";
    private static final String CONF = "conf";
    private static final String FTPD_CONF = "ftpd.xml";
    private static final String USERS_CONF = "users.properties";
    private static FTPService self;
    private File usersFile;
    private File confFile;
    private File rootDir;
    private FtpServer server;
    private PropertiesUserManager userManager;
    protected Listener listener;

    private FTPService(String rootDir) throws UnknownHostException, IOException, Exception {
        createFtp(rootDir);
    }

    protected void createFtp(String rootDir) throws Exception {
            this.rootDir = new File(rootDir);
            confFile = new File(new File(new File(rootDir, RES), CONF), FTPD_CONF);
            String filePath = confFile.getAbsolutePath();

            usersFile = new File(new File(new File(rootDir, RES), CONF), USERS_CONF);

            Properties prop = Prefs.load(new File(rootDir,"../.."));

            PropertiesUserManagerFactory userFactory;
            userFactory=new PropertiesUserManagerFactory();
            userFactory.setFile(usersFile);
            userFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
            userManager = (PropertiesUserManager) userFactory.createUserManager();

            ListenerFactory listenerFactory;
            listenerFactory=new ListenerFactory();
            listenerFactory.setPort(Integer.parseInt(prop.getProperty(Prefs.PUBLISH_LOCAL_FTP_PORT)));
            listenerFactory.setServerAddress(prop.getProperty(Prefs.PUBLISH_LOCAL_FTP_IP));
            listener=listenerFactory.createListener();

            Hashtable<String,Listener> listeners;
            listeners=new Hashtable<String,Listener>();
            listeners.put("DEFAULT_LISTENER", listener);

            FtpServerFactory serverFactory = new FtpServerFactory();
            serverFactory.setUserManager(userManager);
            serverFactory.setListeners(listeners);
            server = serverFactory.createServer();
            server.start();
    }

    synchronized public static FTPService getInstance(String rootDir) {
        if (self == null) {
            try
            {
                self = new FTPService(rootDir);
            }
            catch(Exception e)
            {
                Log.log("Cannot get FTP server instance");
                self=null;
            }
        }
        return self;
    }

    synchronized public static FTPService getInstance() {
        return self;
    }

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

        user.setAuthorities(new Vector<Authority>());

        userManager.save(user);
    }

    public User getUser(String userName) throws Exception {
        return userManager.getUserByName(userName);
    }

    public void removeUser(String userName) throws Exception {
        if (userManager.doesExist(userName)) {
            userManager.delete(userName);
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

            user.setAuthorities(new Vector<Authority>());

            userManager.save(user);
            return true;
        }
        return false;
    }

    public boolean doesUserHasWritePermission(String userName) throws Exception {
        return (this.getUser(userName).authorize(new WriteRequest()) != null);
    }

    public synchronized void stopServer() {
       server.stop();
       server=null;
    }
    
    public synchronized void deleteServer() {
       server.stop();
       server=null;
       self= null;
    }

    public boolean isServerRunning() {
        return server!=null && server.isStopped() == false;
    }

    public String[] getUsers() throws Exception {
        return userManager.getAllUserNames();
    }

    

}


