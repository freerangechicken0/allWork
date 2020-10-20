#ifndef ITEM_H
#define ITEM_H

#include <QString>

class Item
{
public:
    Item();
    Item(QString des, QString par, QString pur, double cos, int units);
    ~Item();

    QString to_QString();

    QString getDescription();
    QString getPartNumber();
    QString getPurchaseLocation();
    double getCost();
    int getUnits();

    void setDescription(QString des);
    void setPartNumber(QString par);
    void setPurchaseLocation(QString pur);
    void setCost(double cos);
    void setUnits(int uni);
private:
    QString description;
    QString partNumber;
    QString purchaseLocation;
    double cost;
    int units;
};

#endif // ITEM_H
