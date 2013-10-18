/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package remoteMethodInvocation;

import view.StartServerWindow;
import clientRemoteMethodInvocation.ClientRemoteMethodInvocation;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import remoteMethodInvocation.RemoteMethodInvocation;

/**
 *
 * @author JH
 */
public class Server extends UnicastRemoteObject implements RemoteMethodInvocation {

   private String url = "rtphonedatabase.cjjtjg8aan4o.sa-east-1.rds.amazonaws.com";
   private String port = "3306";
   private String DBName = "RTPhoneDatabase";
   private String user = "RTPhoneDatabase";
   private String password = "RTPhoneDatabase";
   private Connection connection = null;
   private Statement statement = null;
   private ResultSet resultSet = null;
   private StartServerWindow startServerWindow;

   public Server(StartServerWindow startServerWindow) throws RemoteException {
//      super(Registry.REGISTRY_PORT);
      super();
      this.startServerWindow = startServerWindow;
      try {
         //Exporta o objeto remoto  
         RemoteMethodInvocation rmi = (RemoteMethodInvocation) UnicastRemoteObject
                 .exportObject(this, 0);
         //Liga o stub do objeto remoto no registro  
         Registry registry = LocateRegistry.getRegistry(9000);
         //Dá um nome pra ele no registro  
         registry.bind("RTPhoneServer", rmi);
      } catch (AlreadyBoundException ex) {
         Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      } catch (AccessException ex) {
         Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      } catch (RemoteException Re) {
         System.out.println(Re.getMessage());
      }
   }

   public Connection getConnection() {
      return connection;
   }

   public String getDBName() {
      return DBName;
   }

   public String getPassword() {
      return password;
   }

   public String getPort() {
      return port;
   }

   public ResultSet getResultSet() {
      return resultSet;
   }

   public Statement getStatement() {
      return statement;
   }

   public String getUrl() {
      return url;
   }

   public String getUser() {
      return user;
   }

   @Override
   public String call(String username, String caller) throws RemoteException {
      try {
         ClientRemoteMethodInvocation rmi;
         String address = getUserAddress(username);
         if (!address.isEmpty()) {
            Registry registry = LocateRegistry.getRegistry(address, 9001);
            rmi = (ClientRemoteMethodInvocation) registry.lookup("RTPhoneClient");
            String callerAddress = getUserAddress(caller);
            if (!callerAddress.isEmpty()) {
               boolean check = rmi.call(caller, callerAddress);
               if (check) {
                  return address;
               }
            }
         }
      } catch (NotBoundException ex) {
         Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      }
      return new String();
   }

   private String getUserAddress(String user) {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(password);
         String query = "select `logged` from login where user_id='" + user + "';";
         System.out.println(query);
         resultSet = statement.executeQuery(query);

         while (resultSet.next()) {
            return resultSet.getString("logged");
         }
      } catch (ClassNotFoundException | SQLException e) {
         System.out.println(e);
      }
      return new String();
   }

   @Override
   public Vector<String> getRegisteredUsers() throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(password);
         String query = "select `user_id` from login;";
         System.out.println(query);
         resultSet = statement.executeQuery(query);

         return writeValues(resultSet);
      } catch (Exception e) {
         System.out.println(e);
      }
      return new Vector<>();
   }

   @Override
   public Vector<String> getLoggedUsers() throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(password);
         String query = "select `user_id` from login where !(logged is null or logged is NULL or logged=0 or logged='');";
         System.out.println(query);
         resultSet = statement.executeQuery(query);

         return writeValues(resultSet);
      } catch (Exception e) {
         System.out.println(e);
      }
      return new Vector<>();
   }

   private Vector<String> writeValues(ResultSet resultSet) throws SQLException {
      Vector<String> temp = new Vector<>();
      while (resultSet.next()) {
         temp.add(resultSet.getString("user_id"));
      }
      return temp;
   }

   @Override
   public boolean checkUser(String username, String password, String Address) throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(username);
         System.out.println(password);
         String query = "select * from login where user_id='" + username + "' and password='" + password + "' and (logged is null or logged is NULL or logged=0 or logged='');";
         resultSet = statement.executeQuery(query);
         if (resultSet.next()) {
            return true;
         }
      } catch (Exception e) {
         System.out.println(e);
      }
      return false;
   }

   @Override
   public boolean login(String username, String password, String address) throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(username);
         System.out.println(password);
         String query = "select * from login where user_id='" + username + "' and password='" + password + "' and (logged is null or logged is NULL or logged=0 or logged='');";
         System.out.println(query);
         resultSet = statement.executeQuery(query);
         if (resultSet.next()) {
            query = "UPDATE `RTPhoneDatabase`.`login` SET `logged`='"+address+"' WHERE `user_id`='"+username+"'";
            System.out.println(query);
            statement.executeUpdate(query);
            startServerWindow.getUpdateLoggedUsers().addElement(username);
            return true;
         }
      } catch (Exception e) {
         System.out.println(e);
      }
      return false;
   }

   @Override
   public boolean logoff(String username, String password) throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(username);
         System.out.println(password);
         String query = "select * from login where user_id='" + username + "' and password='" + password + "' and !(logged is null or logged is NULL or logged=0 or logged='');";
         System.out.println(query);
         resultSet = statement.executeQuery(query);
         if (resultSet.next()) {
            query = "UPDATE `RTPhoneDatabase`.`login` SET `logged`=null WHERE `user_id`='" + username + "'";
            System.out.println(query);
            statement.executeUpdate(query);
            startServerWindow.getUpdateLoggedUsers();
            for (int index = 0; index < startServerWindow.getUpdateLoggedUsers().getSize(); index++) {
               if(startServerWindow.getUpdateLoggedUsers().getElementAt(index).equals(username)){
                  startServerWindow.getUpdateLoggedUsers().removeElementAt(index);
               }
            }
            
            return true;
         }
      } catch (Exception e) {
         System.out.println(e);
      }
      return false;
   }

   @Override
   public boolean register(String username, String password) throws RemoteException {
      String dbUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.DBName + "?user=" + this.user + "&password=" + this.password;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         connection = DriverManager.getConnection(dbUrl);
         statement = connection.createStatement();
         System.out.println(username);
         System.out.println(password);
         String query = "select * from login where user_id='" + username + "';";
         resultSet = statement.executeQuery(query);
         if (resultSet.next()) {
            return false;
         } else {
            query = "INSERT INTO `RTPhoneDatabase`.`login` (`user_id`, `password`) VALUES ('" + username + "', '" + password + "');";
            System.out.println(query);
            statement.executeUpdate(query);
            startServerWindow.getUpdateRegisteredUsers().addElement(username);
            return true;
         }
      } catch (Exception e) {
         System.out.println(e);
      }
      return false;
   }
}