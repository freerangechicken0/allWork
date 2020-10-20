#include "mbed.h"
#include "uart.h"
#include <string>
#include <stdio.h>

//uart
Uart uart(USBTX, USBRX);
//LED's
DigitalOut greenLED(PA_5, 0);
DigitalOut blueLED(PA_7, 0);
//lcd
#define LCD_DATA 1
#define LCD_INSTRUCTION 0
void lcdCommand(unsigned char command);
void lcdPutChar(unsigned char c);
void lcdPutString(char *s);
void sensorAction();
static void lcdSetRS(int mode); //-- mode is either LCD_DATA or LCD_INSTRUCTION
static void lcdPulseEN(void);
static void lcdInit8Bit(unsigned char command);
DigitalOut lcdD4(PA_9), lcdD5(PA_8), lcdD6(PB_10), lcdD7(PB_4);
DigitalOut lcdEN(PC_7), lcdRS(PB_6);
//button
DigitalIn button(PC_13);
//servo
PwmOut servo(PB_0);
//sensor
PwmOut usTrig(PB_3);
DigitalIn usEcho(PB_5);
Timer t;
Ticker sensorT;


class stepper {
    private :
        int delay;
        int state; //0 for off, 1 for on
        int direction; //0 for clockwise, 1 for counterclockwise
        int magnet; //number representing which magnet should be turned next, 0-3
        DigitalOut* stepper0;
        DigitalOut* stepper1;
        DigitalOut* stepper2;
        DigitalOut* stepper3;
        void tick(void);
        Ticker t;
    public :
        stepper(int, PinName, PinName, PinName, PinName);
        ~stepper();
        void setDelay(int);
        void toggle(void);
        void toggleDirection() {
            if (direction) (direction = 0);
            else (direction = 1);
        }
};

stepper::stepper(int dly, PinName pin0, PinName pin1, PinName pin2, PinName pin3) {
    stepper0 = new DigitalOut(pin0);
    stepper1 = new DigitalOut(pin1);
    stepper2 = new DigitalOut(pin2);
    stepper3 = new DigitalOut(pin3);
    
    delay = dly;
    state = 0;
    direction = 0;
    magnet = 0;
}

stepper::~stepper() {
    
}

void stepper::setDelay(int newDelay) {
    delay = newDelay;
    toggle();
    toggle();
}

void stepper::toggle(void) {
    if (state) {
        state = 0;
        t.detach();
        stepper0->write(0);
        stepper1->write(0);
        stepper2->write(0);
        stepper3->write(0);
    }
    else {
        state = 1;
        t.attach_us(callback(this, &stepper::tick), delay*1000);
    }
}

void stepper::tick(void) {
    if (magnet == 0) {
        stepper0->write(1);
        stepper1->write(0);
        stepper2->write(0);
        stepper3->write(0);
        if (direction) (magnet = 3);
        else (magnet++);
    }
    
    else if (magnet == 1) {
        stepper0->write(0);
        stepper1->write(1);
        stepper2->write(0);
        stepper3->write(0);
        if (direction) (magnet--);
        else (magnet++);
    }
    
    else if (magnet == 2) {
        stepper0->write(0);
        stepper1->write(0);
        stepper2->write(1);
        stepper3->write(0);
        if (direction) (magnet--);
        else (magnet++);
    }
    
    else if (magnet == 3) {
        stepper0->write(0);
        stepper1->write(0);
        stepper2->write(0);
        stepper3->write(1);
        if (direction) (magnet--);
        else (magnet = 0);
    }
}

void sensorAction() {
    while (! usEcho.read()); 
    t.start();
    while (usEcho.read());
    t.stop();
    int pulseWidth = t.read_us();
    t.reset();
    char y[6];
    sprintf(y, "%d", pulseWidth);
    uart.putString("U ");
    uart.putString(y);
    uart.putString("\n");
}


int main() {
    //initialise lcd
    lcdEN.write(0); //-- GPIO_WriteBit(GPIOC, LCD_EN, Bit_RESET);
    wait_us(15000); //-- delay for >15msec second after power on
    lcdInit8Bit(0x30); //-- we are in "8bit" mode
    wait_us(4100); //-- 4.1msec delay
    lcdInit8Bit(0x30); //-- but the bottom 4 bits are ignored
    wait_us(100); //-- 100usec delay
    lcdInit8Bit(0x30);
    lcdInit8Bit(0x20);
    lcdCommand(0x28); //-- we are now in 4bit mode, dual line
    lcdCommand(0x08); //-- display off
    lcdCommand(0x01); //-- display clear
    wait_us(2000); //-- needs a 2msec delay !!
    lcdCommand(0x06); //-- cursor increments
    lcdCommand(0x0f); //-- display on, cursor(blinks) on
    
    //initialise servo
    servo.period_us(20000);
    
    //initialise stepper
    stepper thisstepper(4, PC_0, PC_1, PC_2, PC_3);
    
    //initialise ultrasonic sensor
    int sensorOn = 0;
    usTrig.period_us(50000);
    usTrig.pulsewidth_us(10);    
     
    while (1) {
        if (button == 0) {
            uart.writeLine("B\n");
        }
        char c[80];
        if (uart.canReadLine()) {
            uart.readLine(c);
            c[strlen(c)-1] = '\0';
            
            if (strncmp(c, "M ", 2) == 0) {
                lcdCommand(0x01); //-- clear display
                wait_us(2000); //-- wait 2msec
                
                char z[80];
                strncpy(z, c + 2, strlen(c) - 1); //-- cutting "M " from char array
                
                lcdPutString(z);
            }
            else if (strcmp(c, "G") == 0) {
                if (greenLED) (greenLED = 0);
                else (greenLED = 1);
            }
            else if (strcmp(c, "B") == 0) {
                if (blueLED) (blueLED = 0);
                else (blueLED = 1);
            }
            else if (strcmp(c, "U") == 0) {
               if (sensorOn) {
                   sensorT.detach();
                   sensorOn = 0;
                   uart.putString("U 0\n");
               }
               else {
                   sensorT.attach_us(sensorAction, 100000);
                   sensorOn = 1;
               }
            }
            else if (strncmp(c, "A ", 2) == 0) {
                char z[5];
                strncpy(z, c + 2, strlen(c) - 2); //-- cutting "A " from char array
                int i = 0;
                sscanf(z, "%d", &i);
                if (i >= 1000) (servo.pulsewidth_us(i));
            }
            else if (strcmp(c, "S") == 0) (thisstepper.toggle());
            else if (strcmp(c, "T") == 0) (thisstepper.toggleDirection());
            else if (strncmp(c, "D ", 2) == 0) {
                char z[strlen(c)];
                strncpy(z, c + 2, strlen(c) - 2); //-- cutting "D " from char array
                int i = 0;
                sscanf(z, "%d", &i);
                if (i >= 4) (thisstepper.setDelay(i));
            }    
        }
    }
}

static void lcdSetRS(int mode)
{
 lcdRS.write(mode);
}

static void lcdPulseEN(void)
{
 lcdEN.write(1);
 wait_us(1); //-- enable pulse must be >450ns
 lcdEN.write(0);
 wait_us(1);
}

static void lcdInit8Bit(unsigned char command)
{
 lcdSetRS(LCD_INSTRUCTION);
 lcdD4.write((command>>4) & 0x01); //-- bottom 4 bits
 lcdD5.write((command>>5) & 0x01); //-- are ignored
 lcdD6.write((command>>6) & 0x01);
 lcdD7.write((command>>7) & 0x01);
 lcdPulseEN();
 wait_us(37); //-- let it work on the data
}

void lcdCommand(unsigned char command)
{
 lcdSetRS(LCD_INSTRUCTION);
 lcdD4.write((command>>4) & 0x01);
 lcdD5.write((command>>5) & 0x01);
 lcdD6.write((command>>6) & 0x01);
 lcdD7.write((command>>7) & 0x01);
 lcdPulseEN(); //-- this can't be too slow or it will time out
 lcdD4.write(command & 0x01);
 lcdD5.write((command>>1) & 0x01);
 lcdD6.write((command>>2) & 0x01);
 lcdD7.write((command>>3) & 0x01);
 lcdPulseEN();
 wait_us(37); //-- let it work on the data
}

void lcdPutChar(unsigned char c)
{
 lcdSetRS(LCD_DATA);
 lcdD4.write((c>>4) & 0x01);
 lcdD5.write((c>>5) & 0x01);
 lcdD6.write((c>>6) & 0x01);
 lcdD7.write((c>>7) & 0x01);
 lcdPulseEN(); //-- this can't be too slow or it will time out
 lcdD4.write(c & 0x01);
 lcdD5.write((c>>1) & 0x01);
 lcdD6.write((c>>2) & 0x01);
 lcdD7.write((c>>3) & 0x01);
 lcdPulseEN();
 wait_us(37); //-- let it work on the data
}

void lcdPutString(char s[]) {
 for (int i = 0; s[i] != '\0'; i++) (lcdPutChar(s[i]));
}