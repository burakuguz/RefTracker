package pages;

import DatabaseClasses.*;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import static pages.LoginController.dbConnection;
import tableObjects.CampsTable;
import tableObjects.RefugeeTable;

public class CampsController implements Initializable {

    @FXML
    private TableView<CampsTable> campsTable = new TableView<>();
    @FXML
    private Button addCampButton;
    @FXML
    private Button findCampButton;
    @FXML
    private Button campDetailButton;
    @FXML
    private Button campEditButton;
    @FXML
    private Button deleteCampButton;

    EntityManager em;
    
    @FXML
    private void addClicked(MouseEvent event) {
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("AddCamps.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        AddCampsController acc = Loader.getController();

        Parent p = Loader.getRoot();
        Stage s = new Stage();
        s.setScene(new Scene(p));
        acc.setS(s);
        s.show();
    }
    
    @FXML
    private void findClicked(MouseEvent event) {
        //find camp
    }
    @FXML
    private void deleteClicked(MouseEvent event) {
        em = LoginController.dbConnection.newEntityManager();
        TypedQuery<CampSite> q1 = em.createQuery("SELECT t FROM CampSite t WHERE t.name ='" + campsTable.getSelectionModel().getSelectedItem().getName() + "'", CampSite.class);
        CampSite ct = q1.getSingleResult();
        Query q2 = em.createQuery("DELETE FROM CampSite t where t.id='" + ct.getId().toString() + "'");
        q2.executeUpdate();
        em.getTransaction().commit();
        CampsTable selectedItem = campsTable.getSelectionModel().getSelectedItem();
        campsTable.getItems().remove(selectedItem);
        em.close();
    }
    @FXML
    private void editClicked(MouseEvent event) {
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("EditCamp.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        EditCampController ecc = Loader.getController();

        Parent p = Loader.getRoot();
        Stage s = new Stage();
        s.setScene(new Scene(p));
        ecc.setS(s);
        try {
            em = LoginController.dbConnection.newEntityManager();
            TypedQuery<CampSite> q1 = em.createQuery("SELECT t FROM CampSite t WHERE t.name ='" + campsTable.getSelectionModel().getSelectedItem().getName() + "'", CampSite.class);
            CampSite ct = q1.getSingleResult();
            ecc.setCampSite(ct);
        } catch (Exception e) {
            e.printStackTrace();
        }
        s.show();
    }
    @FXML
    private void detailClicked(MouseEvent event) {
        
    }
    
    private ObservableList<CampsTable> campSites = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        campSites = FXCollections.observableArrayList();

        EntityManager em = dbConnection.newEntityManager();
        TypedQuery<CampSite> tq = em.createQuery("SELECT cs FROM CampSite cs", CampSite.class);
        List<CampSite> csList = tq.getResultList();
        for (int i = 0; i < csList.size(); i++) {
            CampSite c = csList.get(i);
            campSites.add(new CampsTable(c.getName(), c.getLocation(), findCampType(c)));
        }

        em.close();
        setColumns();
    }

    public void setCampSites(ObservableList<CampsTable> campSites) {
        this.campSites = campSites;
    }

    public ObservableList<CampsTable> getCampSites() {
        return campSites;
    }

    public void setColumns() {

        TableColumn name = new TableColumn("Name");
        TableColumn location = new TableColumn("Location");
        TableColumn campType = new TableColumn("Camp Type");
        TableColumn requirement = new TableColumn("Requirement");

        name.setCellValueFactory(new PropertyValueFactory<CampsTable, String>("name"));
        location.setCellValueFactory(new PropertyValueFactory<CampsTable, String>("location"));
        campType.setCellValueFactory(new PropertyValueFactory<CampsTable, String>("campType"));
        requirement.setCellValueFactory(new PropertyValueFactory<CampsTable, String>("requirement"));

        campsTable.getColumns().clear();
        campsTable.getColumns().addAll(name, location, campType, requirement);
        campsTable.setItems(campSites);
    }

    public String findCampType(CampSite campS) {
        int campTypeID = campS.getCampType();

        em = LoginController.dbConnection.newEntityManager();
        TypedQuery<CampType> q1 = em.createQuery("SELECT t FROM CampType t WHERE t.id ='" + campTypeID + "'", CampType.class);
        CampType ct = q1.getSingleResult();
        em.close();
        return ct.getName();
    }
}
