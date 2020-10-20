#include "item.h"

Item::Item()
{
    description = "Default Item";
    partNumber = "AA0000";
    purchaseLocation = "Default Location";
    cost = 0;
    units = 1;
}

Item::Item(QString des, QString par, QString pur, double cos, int uni) {
    description = des;
    partNumber = par;
    purchaseLocation = pur;
    cost = cos;
    units = uni;
}

Item::~Item() {

}

QString Item::to_QString() {
    return QString(description + " : " + partNumber + " : " + purchaseLocation + " : " + QString::number(cost, 'f', 2) + " : " + QString::number(units));
}

QString Item::getDescription() {
    return description;
}

QString Item::getPartNumber() {
    return partNumber;
}

QString Item::getPurchaseLocation() {
    return purchaseLocation;
}

double Item::getCost() {
    return cost;
}

int Item::getUnits() {
    return units;
}

void Item::setDescription(QString des) {
    description = des;
}

void Item::setPartNumber(QString par) {
    partNumber = par;
}

void Item::setPurchaseLocation(QString pur) {
    purchaseLocation = pur;
}

void Item::setCost(double cos) {
    cost = cos;
}

void Item::setUnits(int uni) {
    units = uni;
}
