module se233.advproject2 {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens se233.advproject2 to javafx.fxml;
    exports se233.advproject2;
}