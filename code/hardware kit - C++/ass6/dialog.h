#ifndef DIALOG_H
#define DIALOG_H

#include <QDialog>
#include <QtSerialPort/QSerialPort>

namespace Ui {
class Dialog;
}

class Dialog : public QDialog
{
    Q_OBJECT

public:
    explicit Dialog(QWidget *parent = nullptr);
    ~Dialog();

private slots:
    void serialDataReady();

    void on_stepperToggle_stateChanged(int arg1);

    void on_stepperDirection_stateChanged(int arg1);

    void on_stepperSlider_valueChanged(int value);

    void on_blueToggle_stateChanged(int arg1);

    void on_greenToggle_stateChanged(int arg1);

    void on_servoSlider_sliderReleased();

    void on_lcdLineEdit_textChanged(const QString &arg1);

    void on_sensorToggle_stateChanged(int arg1);

private:
    Ui::Dialog *ui;
    QSerialPort *serial;
};

#endif // DIALOG_H
