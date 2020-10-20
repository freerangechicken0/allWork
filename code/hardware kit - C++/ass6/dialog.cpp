#include "dialog.h"
#include "ui_dialog.h"

Dialog::Dialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::Dialog)
{
    ui->setupUi(this);
    serial = new QSerialPort("COM5", this);
    connect(serial, SIGNAL(readyRead()), SLOT(serialDataReady()));
    serial->open(QIODevice::ReadWrite);
}

Dialog::~Dialog()
{
    delete ui;
    delete serial;
}

void Dialog::serialDataReady() {
    if (serial->canReadLine()) {
        char s[80];
        serial->readLine(s, 80);
        s[strlen(s)-1] = '\0';
        if (strcmp(s, "B")) {
            ui->lcdLineEdit->clear();
        }
        if (strncmp(s, "U ", 2) == 0) {
            char z[80];
            strncpy(z, s + 2, strlen(s) - 1); //-- cutting "U " from char array
            int i = 0;
            sscanf(z, "%d", &i);
            int distance = (i*343)/1000;
            ui->sensorBar->setValue(distance);
        }
    }
}

void Dialog::on_stepperToggle_stateChanged(int arg1)
{
    serial->write("S\n");
}

void Dialog::on_stepperDirection_stateChanged(int arg1)
{
    serial->write("T\n");
}

void Dialog::on_stepperSlider_valueChanged(int value)
{
    char num[3];
    sprintf(num, "%d", value);

    serial->write("D ");
    serial->write(num);
    serial->write("\n");
}

void Dialog::on_blueToggle_stateChanged(int arg1)
{
    serial->write("B\n");
}

void Dialog::on_greenToggle_stateChanged(int arg1)
{
    serial->write("G\n");
}

void Dialog::on_servoSlider_sliderReleased()
{
    char num[5];
    sprintf(num, "%d", ui->servoSlider->value());

    serial->write("A ");
    serial->write(num);
    serial->write("\n");
}

void Dialog::on_lcdLineEdit_textChanged(const QString &arg1)
{
    serial->write("M ");
    serial->write(qPrintable(arg1));
    serial->write("\n");
}

void Dialog::on_sensorToggle_stateChanged(int arg1)
{
    serial->write("U\n");
}
