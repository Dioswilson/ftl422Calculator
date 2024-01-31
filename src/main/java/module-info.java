module com.dioswilson.ftl422calculator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.dioswilson.ftl422calculator to javafx.fxml;
    exports com.dioswilson.ftl422calculator;
    exports com.dioswilson.ftl422calculator.util;
    opens com.dioswilson.ftl422calculator.util to javafx.fxml;
}