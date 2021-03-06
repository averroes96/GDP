/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App.Controllers;

import Data.User;
import static Include.Common.getConnection;
import static Include.Common.getSettingValue;
import static Include.Common.startStage;
import Include.Init;
import Include.SMController;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author med
 */
public class LoginController extends SMController implements Initializable,Init {
    
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXButton loginButton;
    @FXML private Label title;
    @FXML private HBox usernameHB, passwordHB;

   
    
    
    public boolean exists(String username, String password) throws SQLException{
        
        Connection con = getConnection();
        String query = "SELECT * FROM user WHERE username = ? AND password = ? AND active = 1";

        PreparedStatement stmt;
        ResultSet rs;
     
        try {

            stmt = con.prepareStatement(query);
            stmt.setString(1, username);        
            stmt.setString(2, password);
            
            rs = stmt.executeQuery(); 
            
            return rs.next();
                
  
        } catch (SQLException ex) {
            try {
                Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe"); 
            } catch (IOException e) {
                exceptionLayout(e, loginButton);
            }
            return false;
        }

    }
    
    @FXML
    public void login(ActionEvent event) throws IOException, SQLException{
        
        if(exists(username.getText().trim(),password.getText().trim())){
            
            User user = User.get(username.getText(), password.getText());
            user.updateLastLogged();                
            ((Node)event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLS_PATH + "Main.fxml"), bundle);
            AnchorPane root = (AnchorPane)loader.load();
            MainController mControl = (MainController)loader.getController();
            mControl.getEmployer(user);
            startStage(root, 1000, 700);

        }
        else{
            customDialog(bundle.getString("user_info"), bundle.getString("user_info_msg"), ERROR_SMALL, true, loginButton);                                        
        }

    }
    
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            bundle = rb;
            
            isAnimated();
            
            animateNode(new ZoomIn(usernameHB));
            animateNode(new ZoomIn(passwordHB));
            animateNode(new ZoomIn(loginButton));
            animateNode(new ZoomIn(title));
            
            title.setText(getSettingValue("app_name"));
            if(bundle.getLocale().getLanguage().equals("ar"))
                anchorPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                        
            loginButton.setOnAction(Action ->{
                try {
                    login(Action);
                } catch (IOException | SQLException ex) {
                    customDialog(bundle.getString("connection_error"), bundle.getString("connection_error_msg"), ERROR_SMALL, true, loginButton);
                }
            });
            
        } catch (SQLException ex) {
            exceptionLayout(ex, loginButton);
        }
    }    
    
}
