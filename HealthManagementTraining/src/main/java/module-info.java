module com.sixthgroup.healthmanagementtraining {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    opens com.sixthgroup.healthmanagementtraining to javafx.fxml;
    exports com.sixthgroup.healthmanagementtraining;
    exports com.sixthgroup.healthmanagementtraining.services;
    
    opens com.sixthgroup.healthmanagementtraining.pojo to javafx.base;


}
