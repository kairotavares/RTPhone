
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import rmi.RMI;
import util.RTP.Phone;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author JH
 */
public class MainWindow extends javax.swing.JFrame {

   private String url = "rtphonedatabase.cjjtjg8aan4o.sa-east-1.rds.amazonaws.com";
   private String port = "3306";
   private String DBName = "RTPhoneDatabase";
   private String user = "RTPhoneDatabase";
   private String password = "RTPhoneDatabase";
   private Connection connection = null;
   private Statement statement = null;
   private ResultSet resultSet = null;
   private Phone phone;
   private LoginWindow loginWindow;
   private DefaultListModel model;
   private DefaultListModel model2;
   private boolean open;

   /**
    * Creates new form MainWindow
    */
   public MainWindow(LoginWindow loginWindow) {
      initComponents();
      this.loginWindow = loginWindow;
      phone = null;
      open = true;
      updateData();
      int delay = 10000; //milliseconds
      ActionListener taskPerformer = new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            updateData();
         }
      };
      new Timer(delay, taskPerformer).start();
   }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Phone getPhone() {
        return phone;
    }

   private void updateData() {
      if (open) {
         updateLoggedUsers();
         updateRegisteredUsers();
      }
   }

   public JButton getjButtonCall() {
      return jButtonCall;
   }
   
   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jTabbedPane1 = new javax.swing.JTabbedPane();
      jScrollPane1 = new javax.swing.JScrollPane();
      model = new DefaultListModel();
      jListLoggedUsers = new JList(model);
      jScrollPane2 = new javax.swing.JScrollPane();
      model2 = new DefaultListModel();
      jListRegisteredUsers = new JList(model2);
      jButtonCall = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
            formWindowClosing(evt);
         }
      });

      jListLoggedUsers.setModel(model);
      jScrollPane1.setViewportView(jListLoggedUsers);

      jTabbedPane1.addTab("Logged Users", jScrollPane1);

      jListRegisteredUsers.setModel(model2);
      jScrollPane2.setViewportView(jListRegisteredUsers);

      jTabbedPane1.addTab("Registered Users", jScrollPane2);

      jButtonCall.setText("Call");
      jButtonCall.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonCallActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jButtonCall)
                  .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButtonCall)
            .addContainerGap())
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void jButtonCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCallActionPerformed
      if (jButtonCall.getText().equals("Call")) {
         try {
            Registry registry = LocateRegistry.getRegistry(loginWindow.getjTextFieldHost().getText(), 9000);
            loginWindow.setRmi((RMI) registry.lookup("RTPhoneServer"));
            String check = loginWindow.getRmi().call((String) this.jListLoggedUsers.getSelectedValue(), loginWindow.getjTextFieldUsername().getText());
            if (!check.isEmpty()) {
               this.phone = new Phone(check, 16384, 32766);
               this.phone.start();
               jButtonCall.setText("Hang Up");
            } else {
               JOptionPane.showMessageDialog(null, "erro");
            }

         } catch (Exception e) {
            System.out.println(e);
         }
      } else {
         this.phone.stop();
         this.phone = null;
      }
   }//GEN-LAST:event_jButtonCallActionPerformed

   private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      open = false;
      try {
         boolean check = false;
         while (!check) {
            Registry registry = LocateRegistry.getRegistry(loginWindow.getjTextFieldHost().getText(), 9000);
            loginWindow.setRmi((RMI) registry.lookup("RTPhoneServer"));
            check = loginWindow.getRmi().logoff(loginWindow.getjTextFieldUsername().getText(), loginWindow.getjPasswordField().getText());
         }
      } catch (Exception e) {
         System.out.println(e);
      }
   }//GEN-LAST:event_formWindowClosing
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButtonCall;
   private javax.swing.JList jListLoggedUsers;
   private javax.swing.JList jListRegisteredUsers;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTabbedPane jTabbedPane1;
   // End of variables declaration//GEN-END:variables

   public Vector<String> getLoggedUsers() throws SQLException, ClassNotFoundException {
      String dbUrl = "jdbc:mysql://" + url + ":" + port + "/" + DBName + "?user=" + user + "&password=" + password;
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(dbUrl);
      statement = connection.createStatement();
      String query = "select `user_id` from login where !(logged is null or logged is NULL or logged=0 or logged='');";
      resultSet = statement.executeQuery(query);
      return writeValues(resultSet);
   }

   private Vector<String> writeValues(ResultSet resultSet) throws SQLException {
      Vector<String> temp = new Vector<>();
      while (resultSet.next()) {
         temp.add(resultSet.getString("user_id"));
      }
      return temp;
   }

   public Vector<String> getRegisteredUsers() throws ClassNotFoundException, SQLException {
      String dbUrl = "jdbc:mysql://" + url + ":" + port + "/" + DBName + "?user=" + user + "&password=" + password;
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(dbUrl);
      statement = connection.createStatement();
      String query = "select `user_id` from login;";
      resultSet = statement.executeQuery(query);
      return writeValues(resultSet);
   }

   private void updateLoggedUsers() {
      try {
         Vector<String> check = getLoggedUsers();
         model.clear();
         for (int index = 0; index < check.size(); index++) {
            model.addElement(check.get(index));
         }
      } catch (SQLException ex) {
         Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ClassNotFoundException ex) {
         Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   private void updateRegisteredUsers() {
      try {
         Vector<String> check = getRegisteredUsers();
         model2.clear();
         for (int index = 0; index < check.size(); index++) {
            model2.addElement(check.get(index));
         }
      } catch (ClassNotFoundException ex) {
         Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SQLException ex) {
         Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
