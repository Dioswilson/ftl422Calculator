package com.dioswilson.ftl422calculator;

import com.dioswilson.ftl422calculator.util.CalculatorResult;
import com.dioswilson.ftl422calculator.util.Vector3D;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    public TextField destX;
    @FXML
    public TextField destZ;
    @FXML
    public TextField alignerX;
    @FXML
    public TextField alignerZ;
    @FXML
    public TextField maxTnt;
    @FXML
    public TextArea textArea;
    @FXML
    public ListView<CalculatorResult> resultsList;
    @FXML
    public Label directionLabel;
    @FXML
    public BorderPane calculateResults;

    FtlCalculatorModel model = new FtlCalculatorModel();
    @FXML
    private BorderPane mainBorderPane;

    public void calculateButton() {
        resultsList.getItems().clear();
        calculateResults.setVisible(true);
        textArea.setVisible(false);

        try {
            double destinationX = Double.parseDouble(destX.getText());
            double destinationZ = Double.parseDouble(destZ.getText());
            int alignerX = Integer.parseInt(this.alignerX.getText());
            int alignerZ = Integer.parseInt(this.alignerZ.getText());
            int maxTnt = Integer.parseInt(this.maxTnt.getText());

            List<CalculatorResult> acceptableResults = model.getAcceptableResults(alignerX, alignerZ, destinationX + 0.5, destinationZ + 0.5, maxTnt);

            Collections.sort(acceptableResults);
            int quadrant = acceptableResults.get(0).quadrant();
            String quadrantString = getQuadrantString(quadrant);

            directionLabel.setText(String.format("Direction: %s (%s  bits:", quadrant, quadrantString) + String.format("%2s)", Integer.toBinaryString(quadrant)).replace(" ", "0"));
            for (CalculatorResult result : acceptableResults) {
                resultsList.getItems().add(result);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input" + e.getMessage());
            //Idk, just do nothing. Maybe some notification
        }

        dumpStateToFile("state.properties");
    }

    public void chunkLoadingButton() {
        try {
            CalculatorResult selectedItem = resultsList.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {

                calculateResults.setVisible(false);
                textArea.setVisible(true);
                textArea.clear();

                int alignerX = Integer.parseInt(this.alignerX.getText());
                int alignerZ = Integer.parseInt(this.alignerZ.getText());

                List<Pair<Vector3D, Vector3D>> locationPairs = model.simulateMovement(alignerX, alignerZ, selectedItem.bluePearl(), selectedItem.redPearl(), selectedItem.blueTnt(), selectedItem.redTnt(), selectedItem.quadrant());

                StringBuilder tracking = new StringBuilder();

                int ticks = selectedItem.pearlTicks();

                for (int i = 0; i < locationPairs.size(); i++) {
                    Pair<Vector3D, Vector3D> pair = locationPairs.get(i);
                    int pearlChunkX = (int) pair.getKey().getX() >> 4;
                    int pearlChunkZ = (int) pair.getKey().getZ() >> 4;
                    tracking.append("Tick: ").append(i);
                    if (ticks == i) {
                        tracking.append("★");
                    }
                    tracking.append("\t");

                    tracking.append("Pearl: ").append(pair.getKey()).append("\t");
                    tracking.append("TNT: ").append(pair.getValue()).append("\n");
                }


                textArea.setText(tracking.toString());

                textArea.setScrollTop(0.0);

            }
        } catch (NumberFormatException e) {
            //IDK
        }

        dumpStateToFile("state.properties");
    }

    public void programButton() {
        CalculatorResult selectedItem = resultsList.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            calculateResults.setVisible(false);
            textArea.setVisible(true);
            textArea.clear();


            StringBuilder program = new StringBuilder();

            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    program.append("============PEARL============\n");
                }
                else {
                    program.append("============TNT============\n");
                }

                for (int side = 0; side < 2; side++) {
                    int tnt;
                    if (side == 0) {
                        if (j == 0) {
                            tnt = selectedItem.bluePearl();
                        }
                        else {
                            tnt = selectedItem.blueTnt();
                        }
                    }
                    else {
                        if (j == 0) {
                            tnt = selectedItem.redPearl();
                        }
                        else {
                            tnt = selectedItem.redTnt();
                        }
                    }
                    int t286 = tnt / 286;
                    tnt = tnt % 286;
                    int t143 = tnt / 143;
                    tnt = tnt % 143;
                    int t11 = tnt / 11;
                    int t1 = tnt % 11;

                    if (t11 == 1) {
                        t11 = 0;
                        t1 = 11;
                    }
                    if (t11 == 0 && t143 == 0 && t286 > 0) {
                        t286 -= 1;
                        t11 = 13;
                        t143 = 1;
                    }

                    program.append(side == 0 ? "----Blue----" : "----Red----").append("\n");
                    program.append("Large: \n");
                    for (int i = 8; i > 0; i = i / 2) {
                        if (i == (t286 & i)) {
                            program.append("+").append(286 * i).append(" TNT\n");
                        }
                    }
                    program.append("Medium: \n");
                    for (int i = 8; i > 0; i = i / 2) {
                        if (i == (t11 & i)) {
                            program.append("+").append(11 * i).append(" TNT\n");
                        }
                    }
                    if (t143 == 1) {
                        program.append("+143 TNT\n");
                    }
                    program.append("Small: \n");
                    for (int i = 8; i > 0; i = i / 2) {
                        if (i == (t1 & i)) {
                            program.append("+").append(i).append(" TNT\n");
                        }
                    }
                    if (side == 0) {
                        int getnum = selectedItem.quadrant();
                        program.append("----Direction----\n");
                        program.append("Left Bit: ").append(getnum / 2).append("\n");
                        program.append("Right Bit: " + (getnum % 2) + "\n");
                    }
                }
            }
            textArea.setText(program.toString());

        }
        dumpStateToFile("state.properties");
    }

    private String getQuadrantString(int quadrant) {
        return switch (quadrant) {
            case 0 -> "South";
            case 1 -> "West";
            case 2 -> "East";
            case 3 -> "North";
            default -> "";
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        mainBorderPane.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        Properties properties = getPropertiesFromFile("state.properties");
        setStateFromProperties(properties);
    }


    private Properties getPropertiesFromFile(String fileName) {
        Properties properties = new Properties();
        try {
            FileInputStream fi = new FileInputStream("state.properties");
            properties.load(fi);
            fi.close();
        } catch (Exception e) {
            System.out.println("Couldn't load properties file");
        }
        return properties;
    }

    private void dumpStateToFile(String fileName) {
        Properties properties = new Properties();
        properties.setProperty("destX", destX.getText());
        properties.setProperty("destZ", destZ.getText());
        properties.setProperty("alignerX", alignerX.getText());
        properties.setProperty("alignerZ", alignerZ.getText());
        properties.setProperty("maxTnt", maxTnt.getText());
        try {
            FileOutputStream fr = new FileOutputStream(fileName);
            properties.store(fr, null);
            fr.close();
        } catch (Exception e) {
            System.out.println("Couldn't save properties file");
        }
    }

    void setStateFromProperties(Properties properties) {
        destX.setText(properties.getProperty("destX"));
        destZ.setText(properties.getProperty("destZ"));
        alignerX.setText(properties.getProperty("alignerX"));
        alignerZ.setText(properties.getProperty("alignerZ"));
        try {
            Integer.parseInt(properties.getProperty("maxTnt"));
            maxTnt.setText(properties.getProperty("maxTnt"));
        } catch (NumberFormatException e) {
            maxTnt.setText("2299");
        }

    }
}