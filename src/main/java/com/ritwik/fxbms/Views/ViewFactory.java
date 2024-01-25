package com.ritwik.fxbms.Views;

import com.ritwik.fxbms.Controllers.Client.ClientController;
import com.ritwik.fxbms.Controllers.Signup.SignupController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
 //Client Views
    private final ObjectProperty<ClientMenuOptions> clientSelectedMenuItem;
    private final ObjectProperty<SignupMenuOptions> signupMenuItem;
    private AnchorPane dashboardView;
    //Signup View

    private AnchorPane transactionView;

    private AnchorPane accountsView;

    private AnchorPane form1View;
    private AnchorPane form2View;
    private AnchorPane form3View;
    public ViewFactory(){
        this.clientSelectedMenuItem = new SimpleObjectProperty<>();
        this.signupMenuItem = new SimpleObjectProperty<>();
    }

    /*
    * Client View Section
    * */

    public ObjectProperty<ClientMenuOptions> getClientSelectedMenuItem() {

        return clientSelectedMenuItem;
    }
    public ObjectProperty<SignupMenuOptions> getSignupMenuItem(){
        return signupMenuItem;
    }

//Signup Section
    public AnchorPane getForm1View() {
        if (form1View == null) {
            try {
                form1View = new FXMLLoader(getClass().getResource("/Fxml/Signup/Signup_Form1.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return form1View;
    }

    public AnchorPane getForm2View() {
        if (form2View == null) {
            try {
                form2View = new FXMLLoader(getClass().getResource("/Fxml/Signup/Signup_Form2.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return form2View;
    }

    public AnchorPane getForm3View() {
        if (form3View == null) {
            try {
                form3View = new FXMLLoader(getClass().getResource("/Fxml/Signup/Signup_Form3.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return form3View;
    }
// End Signup Section Menu Buttons
    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Client/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getTransactionView() {
        if (transactionView == null) {
            try {
                transactionView = new FXMLLoader(getClass().getResource("/Fxml/Client/Transaction.fxml")).load();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactionView;
    }

    public AnchorPane getAccountsView() {
        if (accountsView == null) {
            try {
                accountsView = new FXMLLoader(getClass().getResource("/Fxml/Client/Accounts.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountsView;
    }

    public void showClientWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        loader.setController(clientController);
        createStage(loader);
    }
    public void showSignupWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Signup/Signup.fxml"));
        SignupController signupController = new SignupController();
        loader.setController(signupController);
        createStage(loader);
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("FINANCIAL HUB MANAGER");
        Image icon = new Image(getClass().getResourceAsStream("/Images/bank2.png"));
        stage.getIcons().add(icon);
        stage.show();
    }

    public void closeStage(Stage stage) {
        stage.close();
    }
}
