module com.sixthgroup.healthmanagementtraining {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires java.prefs;
    requires spring.security.crypto;
    opens com.sixthgroup.healthmanagementtraining to javafx.fxml;
    exports com.sixthgroup.healthmanagementtraining;
    exports com.sixthgroup.healthmanagementtraining.services;
    exports com.sixthgroup.healthmanagementtraining.pojo;
    opens com.sixthgroup.healthmanagementtraining.pojo to javafx.base, org.mockito;
    
  
  

}
