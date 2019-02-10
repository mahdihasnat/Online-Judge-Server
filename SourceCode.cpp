<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.effect.*?>
<?import javafx.geometry.*?>
<?import java.lang.*?>
<?import java.net.*?>
<?import java.util.*?>
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<BorderPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="500.0" prefWidth="680.0" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="OnlineJudge.Submission.SubmissionShowFXMLController">
   <top>
      <GridPane BorderPane.alignment="CENTER">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
        </columnConstraints>
        <rowConstraints>
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
        </rowConstraints>
         <children>
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="#" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Author" GridPane.columnIndex="1" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Problem" GridPane.columnIndex="2" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Lang" GridPane.columnIndex="3" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Verdict" GridPane.columnIndex="4" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Time" GridPane.columnIndex="5" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Memory" GridPane.columnIndex="6" />
            <Button mnemonicParsing="false" prefHeight="31.0" prefWidth="87.0" text="Sent" GridPane.columnIndex="7" />
            <Label fx:id="SubmisionId" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="SubmisionId" GridPane.rowIndex="1" />
            <Label fx:id="Handle" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="Handle" GridPane.columnIndex="1" GridPane.rowIndex="1" />
            <Label fx:id="ProblemId" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="ProblemId" GridPane.columnIndex="2" GridPane.rowIndex="1" />
            <Label fx:id="Language" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="Language" GridPane.columnIndex="3" GridPane.rowIndex="1" />
            <Label fx:id="Verdict" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="Verdict" GridPane.columnIndex="4" GridPane.rowIndex="1" />
            <Label fx:id="TimeTaken" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="TimeTaken" GridPane.columnIndex="5" GridPane.rowIndex="1" />
            <Label fx:id="MemoryTaken" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="MemoryTaken" GridPane.columnIndex="6" GridPane.rowIndex="1" />
            <Label fx:id="Time" alignment="CENTER" contentDisplay="CENTER" prefHeight="31.0" prefWidth="87.0" text="Time" GridPane.columnIndex="7" GridPane.rowIndex="1" />
            
            
         </children>
      </GridPane>
   </top>
   <center>
      
   </center>
   <center>
      <ScrollPane prefHeight="200.0" prefWidth="200.0" BorderPane.alignment="CENTER">
        <content>
          <VBox prefWidth="680.0" BorderPane.alignment="CENTER">
         <children>
            <Button alignment="CENTER" contentDisplay="CENTER" mnemonicParsing="false" prefHeight="31.0" prefWidth="450.0" text="Code">
               <VBox.margin>
                  <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
               </VBox.margin>
               <opaqueInsets>
                  <Insets />
               </opaqueInsets>
            </Button>
            <TextArea fx:id="Code" VBox.vgrow="ALWAYS">
               <VBox.margin>
                  <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
               </VBox.margin>
            </TextArea>
            <Button alignment="CENTER" contentDisplay="CENTER" mnemonicParsing="false" prefHeight="31.0" prefWidth="450.0" text="Comment">
               <VBox.margin>
                  <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
               </VBox.margin>
            </Button>
            <TextArea fx:id="Comment">
               <VBox.margin>
                  <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
               </VBox.margin>
            </TextArea>
         </children>
      </VBox>
            
        </content>
      </ScrollPane>
   </center>
</BorderPane>

