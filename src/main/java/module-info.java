module com.ritwik.fxbms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires mysql.connector.j;

    opens com.ritwik.fxbms to javafx.fxml;
    exports com.ritwik.fxbms;
    exports com.ritwik.fxbms.Controllers;
    exports com.ritwik.fxbms.Controllers.Admin;
    exports com.ritwik.fxbms.Controllers.Client;
    exports com.ritwik.fxbms.Models;
    exports com.ritwik.fxbms.Views;
}