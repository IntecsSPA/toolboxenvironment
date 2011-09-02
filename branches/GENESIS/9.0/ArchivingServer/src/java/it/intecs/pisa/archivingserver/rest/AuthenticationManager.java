

package it.intecs.pisa.archivingserver.rest;

import com.google.gson.JsonObject;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Andrea Marongiu
 */
public class AuthenticationManager {
    
    protected static final String USERS_XML = "conf/armsUsers.xml";
    protected static final String USER_ELEMENT_NAME = "user";
    protected static final String USER_NAME_ATTRIBUTE = "username";
    protected static final String PASSWORD_ATTRIBUTE = "password";
    protected static final String ROLES_ATTRIBUTE = "roles";
    
    public static final String ADMIN_RULE="admin";
    public static final String USER_RULE="user";
    public static final String AUTHORIZATION_HEADER="Authorization"; 
    
    
    /**
     * This method checks client authorization.
     * @param request Request class containing all information to use to authenticate
     * the user
     * @return User roles or NULL if the user is not authorized.
     */
    public String [] authenticateUser(HttpServletRequest request) throws IOException, Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        String userID,password;
        String [] roles=null;
        StringTokenizer st = new java.util.StringTokenizer(request.getHeader("Authorization"));

        if (st.hasMoreTokens()) {
            String basic = st.nextToken();
            // We only handle HTTP Basic authentication

            if (basic.equalsIgnoreCase("Basic")) {
               String credentials = st.nextToken();
               decoder = new sun.misc.BASE64Decoder();
               String userPass =
                  new String(decoder.decodeBuffer(credentials));

               int p = userPass.indexOf(":");
               if (p != -1) {
                  userID = userPass.substring(0, p);
                  password = userPass.substring(p+1);     

                  roles=getRoles(userID,password);
               }
            }
         }

        return roles;
    }
    
    /**
     * This method checks client authorization.
     * @param request Request class containing all information to use to authenticate
     * the user
     * @return User roles or NULL if the user is not authorized.
     */
    public String [] authenticateUser(String authorizationHeader) throws IOException, Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        String userID,password;
        String [] roles=null;
        StringTokenizer st = new java.util.StringTokenizer(authorizationHeader);

        if (st.hasMoreTokens()) {
            String basic = st.nextToken();
            // We only handle HTTP Basic authentication

            if (basic.equalsIgnoreCase("Basic")) {
               String credentials = st.nextToken();
               decoder = new sun.misc.BASE64Decoder();
               String userPass =
                  new String(decoder.decodeBuffer(credentials));

               int p = userPass.indexOf(":");
               if (p != -1) {
                  userID = userPass.substring(0, p);
                  password = userPass.substring(p+1);     

                  roles=getRoles(userID,password);
               }
            }
         }

        return roles;
    }
    
    public String getUser(String authorizationHeader) throws IOException{
        StringTokenizer st = new java.util.StringTokenizer(authorizationHeader);
        BASE64Decoder decoder = new BASE64Decoder();
        String userID=null;
        if (st.hasMoreTokens()) {
            String basic = st.nextToken();
            if (basic.equalsIgnoreCase("Basic")) {
               String credentials = st.nextToken();
               decoder = new sun.misc.BASE64Decoder();
               String userPass =
                  new String(decoder.decodeBuffer(credentials));
               int p = userPass.indexOf(":");
               if (p != -1) {
                  userID = userPass.substring(0, p);  
               }
            }
        }    
     return userID;
    }
    
    private String [] getRoles (String user, String password) throws Exception{
        String [] roles=new String[0];
        DOMUtil du=new DOMUtil();
        File userDocFile=new File(this.getClass().getResource(USERS_XML).toURI());
        Document usersDoc=du.fileToDocument(userDocFile);
        NodeList userNodeList=usersDoc.getElementsByTagName(USER_ELEMENT_NAME);
        Element currentUser=null;
        for(int i=0; i<userNodeList.getLength(); i++){
            currentUser=(Element) userNodeList.item(i);
            if(currentUser.getAttribute(USER_NAME_ATTRIBUTE).equals(user) &&
               currentUser.getAttribute(PASSWORD_ATTRIBUTE).equals(password) ){
               
               roles=currentUser.getAttribute(ROLES_ATTRIBUTE).split(",");
               if(roles.length==0){
                  roles=new String[1];
                  roles[1]=currentUser.getAttribute(ROLES_ATTRIBUTE);
               }
             break;
            }
        }
        return roles;
    }
    
    
    public boolean authenticate(String authorizationHeader, String role) throws Exception{
       String [] roles= this.authenticateUser(authorizationHeader);
       for(int i=0; i<roles.length; i++)
           if(roles[i].equals(role))
              return true;
       return false;
    }
    
    
    public JsonObject notAuthorizatedResponse(){
         JsonObject noyAuthResponse = new JsonObject();
         
         noyAuthResponse.addProperty("success", Boolean.FALSE);
         noyAuthResponse.addProperty("reason", "unauthorized");
         
         return noyAuthResponse;
        
    } 

}
