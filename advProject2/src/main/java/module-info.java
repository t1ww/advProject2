module se233.advproject2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
                            
    opens se233.advproject2 to javafx.fxml;
    exports se233.advproject2;
    exports se233.advproject2.controller;
    exports se233.advproject2.objects;
    exports se233.advproject2.view;
    exports se233.advproject2.example;
}