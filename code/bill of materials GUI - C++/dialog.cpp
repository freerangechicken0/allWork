#include "dialog.h"
#include "ui_dialog.h"
#include "item.h"
#include <QString>
#include <QFile>
#include <QFileDialog>
#include <QTextStream>

Dialog::Dialog(QWidget *parent)
    : QDialog(parent)
    , ui(new Ui::Dialog)
{
    ui->setupUi(this);
}

Dialog::~Dialog()
{
    delete ui;
}

void Dialog::on_addItemButton_clicked()
{
    //check that all inputs have entries if not set errorLabel
    if (ui->descriptionInput->text().isEmpty()) { ui->errorLabel->setText("Description Required"); }
    else if (ui->partNumberInput->text().isEmpty()) { ui->errorLabel->setText("Part Number Required"); }
    else if (ui->purchaseLocationInput->text().isEmpty()) { ui->errorLabel->setText("Purchase Location Required"); }
    else if (ui->costInput->text().size() == 1) { ui->errorLabel->setText("Cost Required"); } //decimal place means cant use isEmpty()
    else if (ui->unitsInput->text().isEmpty()) { ui->errorLabel->setText("Units Required"); }
    else {
        //create item and set variables
        Item item = Item();

        item.setDescription(ui->descriptionInput->text());
        item.setPartNumber(ui->partNumberInput->text());
        item.setPurchaseLocation(ui->purchaseLocationInput->text());
        item.setCost(ui->costInput->text().toDouble());
        item.setUnits(ui->unitsInput->text().toInt());

        //clear inputs and error label
        ui->descriptionInput->clear();
        ui->partNumberInput->clear();
        ui->purchaseLocationInput->clear();
        ui->costInput->clear();
        ui->unitsInput->clear();

        ui->errorLabel->clear();

        //add item to BOM and set totalItems and totalCost
        //take current BOM text and add a new line and the item's QString
        ui->itemDisplay->setText(ui->itemDisplay->toPlainText() + "\n" + item.to_QString());
        //take the current total items and add one to it
        ui->totalItemsDisplay->setText(QString::number(ui->totalItemsDisplay->toPlainText().toInt() + 1));
        //take the current total cost and add the items cost * units
        ui->totalCostDisplay->setText(QString::number(ui->totalCostDisplay->toPlainText().toDouble() + (item.getCost() * item.getUnits()), 'f', 2));
    }
}

void Dialog::on_exportButton_clicked()
{
    //save BOM to file
    //get filename
    QString filename = QFileDialog::getSaveFileName(this, "Save", "", "Text (*.txt)");
    //get file
    QFile outputFile {filename};
    //open file
    outputFile.open(QIODevice::WriteOnly);
    //create output stream
    QTextStream outputStream {&outputFile};
    //send BOM to output stream
    outputStream << ui->itemDisplay->toPlainText();
    //close file
    outputFile.close();
}
